package com.bacos.mokengeli.biloko.infrastructure.adapter;

import com.bacos.mokengeli.biloko.application.exception.UserServiceRuntimeException;
import com.bacos.mokengeli.biloko.application.domain.DomainUser;
import com.bacos.mokengeli.biloko.application.domain.RoleEnum;
import com.bacos.mokengeli.biloko.application.port.UserPort;
import com.bacos.mokengeli.biloko.infrastructure.mapper.UserMapper;
import com.bacos.mokengeli.biloko.infrastructure.model.Role;
import com.bacos.mokengeli.biloko.infrastructure.model.Tenant;
import com.bacos.mokengeli.biloko.infrastructure.model.User;
import com.bacos.mokengeli.biloko.infrastructure.model.UserStatusEnum;
import com.bacos.mokengeli.biloko.infrastructure.repository.RoleRepository;
import com.bacos.mokengeli.biloko.infrastructure.repository.TenantRepository;
import com.bacos.mokengeli.biloko.infrastructure.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Component
public class UserAdapter implements UserPort {

    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public UserAdapter(UserRepository userRepository, TenantRepository tenantRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.tenantRepository = tenantRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public DomainUser createNewUser(DomainUser domainUser) {
        Long tenantId = domainUser.getTenantId();
        Optional<Tenant> optTenant = this.tenantRepository.findById(tenantId);
        if (optTenant.isEmpty()) {
            throw new UserServiceRuntimeException(UUID.randomUUID().toString(), "Aucune entrerprise trouvée avec le tenantId = " + tenantId);
        }
        Role role = this.roleRepository.findByLabel(RoleEnum.ROLE_USER.name())
                .orElseThrow(() ->
                        new UserServiceRuntimeException(UUID.randomUUID().toString(), "le Role_USER n'existe pas ")
                );
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        User user = UserMapper.toUser(domainUser);
        user.setStatus(UserStatusEnum.ACTIVE);
        user.setPassword(domainUser.getPassword());
        user.setTenant(optTenant.get());
        user.setCreatedAt(LocalDateTime.now());
        user.setRoles(roles);
        user.setEmployeeNumber(UUID.randomUUID().toString());
        user = userRepository.save(user);
        return UserMapper.toDomain(user);
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
                .map(UserMapper::toLigthDomain);
    }
}
