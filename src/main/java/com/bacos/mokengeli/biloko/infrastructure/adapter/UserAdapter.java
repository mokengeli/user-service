package com.bacos.mokengeli.biloko.infrastructure.adapter;

import com.bacos.mokengeli.biloko.application.domain.DomainUser;
import com.bacos.mokengeli.biloko.application.domain.DomainUserCount;
import com.bacos.mokengeli.biloko.application.domain.UpdateUserPinRequest;
import com.bacos.mokengeli.biloko.application.domain.UpdateUserPinResponse;
import com.bacos.mokengeli.biloko.application.exception.UserServiceRuntimeException;
import com.bacos.mokengeli.biloko.application.port.UserPort;
import com.bacos.mokengeli.biloko.application.service.PinEncoder;
import com.bacos.mokengeli.biloko.infrastructure.mapper.UserMapper;
import com.bacos.mokengeli.biloko.infrastructure.model.*;
import com.bacos.mokengeli.biloko.infrastructure.repository.RoleRepository;
import com.bacos.mokengeli.biloko.infrastructure.repository.TenantRepository;
import com.bacos.mokengeli.biloko.infrastructure.repository.TenantUserSequenceRepository;
import com.bacos.mokengeli.biloko.infrastructure.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Component
public class UserAdapter implements UserPort {
    private static final int PADDING = 4;

    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final RoleRepository roleRepository;
    private final TenantUserSequenceRepository tenantUserSequenceRepository;
    private final PinEncoder encoder;

    @Autowired
    public UserAdapter(UserRepository userRepository, TenantRepository tenantRepository, RoleRepository roleRepository,
                       TenantUserSequenceRepository tenantUserSequenceRepository, PinEncoder encoder) {
        this.userRepository = userRepository;
        this.tenantRepository = tenantRepository;
        this.roleRepository = roleRepository;
        this.tenantUserSequenceRepository = tenantUserSequenceRepository;
        this.encoder = encoder;
    }

    @Transactional
    @Override
    public DomainUser createNewUser(DomainUser domainUser, String password) {
        String tenantCode = domainUser.getTenantCode();
        Tenant tenant = this.tenantRepository.findByCode(tenantCode);
        if (tenant == null) {
            throw new UserServiceRuntimeException(UUID.randomUUID().toString(), "Aucune entrerprise trouvée avec le tenantCode = " + tenantCode);
        }
        List<String> UserRoles = domainUser.getRoles();
        if (UserRoles.isEmpty()) {
            throw new UserServiceRuntimeException(UUID.randomUUID().toString(), "Aucun role fourni");
        }

        Role role = this.roleRepository.findByLabel(UserRoles.get(0))
                .orElseThrow(() ->
                        new UserServiceRuntimeException(UUID.randomUUID().toString(), "le rôle " + UserRoles.get(0) + " n'existe pas ")
                );


        String employeeNumber = createEmployeeNumber(tenant.getId(), tenantCode);
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        User user = UserMapper.toUser(domainUser);
        user.setStatus(UserStatusEnum.ACTIVE);
        user.setPassword(password);
        user.setTenant(tenant);
        user.setCreatedAt(OffsetDateTime.now());
        user.setRoles(roles);
        user.setEmployeeNumber(employeeNumber);
        user = userRepository.save(user);
        return UserMapper.toDomain(user);
    }

    private String createEmployeeNumber(Long tenantId, String tenantCode) {
        long seq = nextEmployeeSeq(tenantId);
        String format = String.format(
                "%s-%0" + PADDING + "d",
                tenantCode,
                seq
        );
        return format.toLowerCase();
    }

    /**
     * Informe la prochaine valeur de séquence pour ce tenant de façon atomique.
     */
    private long nextEmployeeSeq(Long tenantId) {
        // Verrou pessimiste sur la ligne
        Optional<TenantUserSequence> opt = tenantUserSequenceRepository.findByTenantIdForUpdate(tenantId);

        TenantUserSequence seq = opt.orElseGet(() -> new TenantUserSequence(tenantId, 0L));
        long next = seq.getLastValue() + 1;
        seq.setLastValue(next);
        tenantUserSequenceRepository.save(seq);

        return next;
    }

    @Override
    public Optional<DomainUser> getUserByEmployeeNumber(String employeeNumber) {
        Optional<User> optUser = this.userRepository.findByEmployeeNumber(employeeNumber);
        if (optUser.isEmpty()) {
            throw new UserServiceRuntimeException(UUID.randomUUID().toString(), "Aucun utilisateur trouvé avec le matricule = " + employeeNumber);
        }
        User user = optUser.get();
        return Optional.of(UserMapper.toDomain(user));
    }

    @Override
    public Page<DomainUser> findAllUsersByTenant(
            String tenantCode,
            int page,
            int size,
            String search
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> result;

        if (search == null || search.trim().isEmpty()) {
            result = userRepository.findByTenantCode(tenantCode, pageable);
        } else {
            result = userRepository.findByTenantCodeAndLastNameContainingIgnoreCase(
                    tenantCode, search, pageable
            );
        }

        return result.map(UserMapper::toDomain);
    }


    @Override
    public List<String> getAllRoles() {
        List<Role> all = this.roleRepository.findAll();
        return all.stream().map(Role::getLabel).toList();
    }

    @Override
    public List<DomainUserCount> countUsersByRole(String tenantCode) {
        List<UserRepository.RoleCount> rows =
                userRepository.countByTenantCodeGroupByRole(tenantCode);
        return rows.stream()
                .map(r -> new DomainUserCount(r.getRole(), r.getCount()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isUserNameAvailable(String userName) {
        return !this.userRepository.existsByUserName(userName);
    }


    @Override
    public Optional<DomainUser> getUserByIdentifier(String identifier) {
        DomainUser domain = UserMapper.toDomain(findUserByEmployeeNumberOrUsername(identifier));
        return Optional.of(domain);
    }

    private User findUserByEmployeeNumberOrUsername(String employeeNumberOrUsername) {
        Optional<User> byEmployeeNumber = this.userRepository.findByEmployeeNumber(employeeNumberOrUsername);
        if (byEmployeeNumber.isEmpty()) {
            Optional<User> byUsername = this.userRepository.findByUserName(employeeNumberOrUsername);
            if (byUsername.isPresent()) {
                return byUsername.get();
            }
            throw new UserServiceRuntimeException(UUID.randomUUID().toString(), "Aucun utilisateur " +
                    "trouvé avec le matricule ou username = " + employeeNumberOrUsername);
        }
        return byEmployeeNumber.get();
    }

    @Override
    public Boolean verifyPin(String identifier, Integer pin) {
        User user = findUserByEmployeeNumberOrUsername(identifier);
        return encoder.matches(pin.toString(), user.getValidationPin());
    }

    @Override
    public UpdateUserPinResponse updateUserPin(String identifier, UpdateUserPinRequest req, boolean byPassOldPinValidation) {
        // 1. determine target user
        User target = this.findUserByEmployeeNumberOrUsername(identifier);

        // 2. if existing PIN, verify currentPin
        if (!byPassOldPinValidation && target.getValidationPin() != null) {
            if (req.getCurrentPin() == null || !encoder.matches(req.getCurrentPin(), target.getValidationPin())) {
                throw new UserServiceRuntimeException(UUID.randomUUID().toString(), "Current PIN incorrect");
            }
        }
        // 3. validate new PIN format
        String newPin = req.getNewPin().toString();
        if (!newPin.matches("\\d{4}")) {
            throw new UserServiceRuntimeException(UUID.randomUUID().toString(), "PIN must be exactly 4 digits");
        }
        if (encoder.isSequential(newPin) || encoder.isRepetitive(newPin)) {
            throw new UserServiceRuntimeException(UUID.randomUUID().toString(), "PIN is too trivial");
        }
        // 4. hash and store
        target.setValidationPin(encoder.encode(newPin));
        userRepository.save(target);
        // 5. optional audit or notification
        return new UpdateUserPinResponse("PIN updated successfully");
    }
}
