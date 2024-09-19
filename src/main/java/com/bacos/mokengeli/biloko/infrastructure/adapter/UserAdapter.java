package com.bacos.mokengeli.biloko.infrastructure.adapter;

import com.bacos.mokengeli.biloko.application.exception.UserServiceRuntimeException;
import com.bacos.mokengeli.biloko.application.model.DomainUser;
import com.bacos.mokengeli.biloko.application.port.UserPort;
import com.bacos.mokengeli.biloko.infrastructure.model.Tenant;
import com.bacos.mokengeli.biloko.infrastructure.model.User;
import com.bacos.mokengeli.biloko.infrastructure.model.UserStatusEnum;
import com.bacos.mokengeli.biloko.infrastructure.repository.TenantRepository;
import com.bacos.mokengeli.biloko.infrastructure.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Component
public class UserAdapter implements UserPort {

    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;

    @Autowired
    public UserAdapter(UserRepository userRepository, TenantRepository tenantRepository) {
        this.userRepository = userRepository;
        this.tenantRepository = tenantRepository;
    }

    @Override
    public DomainUser createNewUser(DomainUser domainUser) {
        Long tenantId = domainUser.getTenantId();
        Optional<Tenant> optTenant = this.tenantRepository.findById(tenantId);
        if (optTenant.isEmpty()) {
            throw new UserServiceRuntimeException(UUID.randomUUID().toString(), "Aucune entrerprise trouvée avec le tenantId = " + tenantId);
        }
        User user = UserMapper.toUser(domainUser);
        user.setStatus(UserStatusEnum.ACTIVE);
        user.setPassword(domainUser.getPassword());
        user.setTenant(optTenant.get());
        user.setCreatedAt(LocalDateTime.now());
        user.setEmployeeNumber(UUID.randomUUID().toString());
        user = userRepository.save(user);
        return UserMapper.toDomain(user);
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public Optional<DomainUser> findById(Long id) {
        Optional<User> optUser = userRepository.findById(id);
        if (optUser.isPresent()) {
            throw new UserServiceRuntimeException(UUID.randomUUID().toString(), "Aucun utilisateur trouvé avec l'id = " + id);
        }
        User user = optUser.get();
        DomainUser domainUser = UserMapper.toDomain(user);
        return Optional.of(domainUser);
    }

    @Override
    public Optional<DomainUser> getUserByEmail(String email) {
        Optional<User> optUser = this.userRepository.findByEmail(email);
        if (optUser.isEmpty()) {
            throw new UserServiceRuntimeException(UUID.randomUUID().toString(), "Aucun utilisateur trouvé avec l'email = " + email);
        }
        User user = optUser.get();
        return Optional.of(UserMapper.toDomain(user));
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
}
