package com.bacos.mokengeli.biloko.infrastructure.adapter;

import com.bacos.mokengeli.biloko.application.domain.DomainUser;
import com.bacos.mokengeli.biloko.application.domain.DomainUserCount;
import com.bacos.mokengeli.biloko.application.exception.UserServiceRuntimeException;
import com.bacos.mokengeli.biloko.application.port.UserPort;
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

    @Autowired
    public UserAdapter(UserRepository userRepository, TenantRepository tenantRepository, RoleRepository roleRepository,
                       TenantUserSequenceRepository tenantUserSequenceRepository) {
        this.userRepository = userRepository;
        this.tenantRepository = tenantRepository;
        this.roleRepository = roleRepository;
        this.tenantUserSequenceRepository = tenantUserSequenceRepository;
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
    public Page<DomainUser> findAllUsersByTenant(String tenantCode, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository
                .findByTenantCode(tenantCode, pageable)
                .map(UserMapper::toLightDomain);
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
}
