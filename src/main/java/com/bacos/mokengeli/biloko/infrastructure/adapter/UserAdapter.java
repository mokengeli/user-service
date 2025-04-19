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
import java.util.*;

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
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        User user = UserMapper.toUser(domainUser);
        user.setStatus(UserStatusEnum.ACTIVE);
        user.setPassword(password);
        user.setTenant(tenant);
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

    @Override
    public List<String> getAllRoles() {
        List<Role> all = this.roleRepository.findAll();
        return all.stream().map(Role::getLabel).toList();
    }
}
