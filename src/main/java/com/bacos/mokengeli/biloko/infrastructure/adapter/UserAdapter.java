package com.bacos.mokengeli.biloko.infrastructure.adapter;

import com.bacos.mokengeli.biloko.domain.exception.UserServiceException;
import com.bacos.mokengeli.biloko.domain.model.DomainUser;
import com.bacos.mokengeli.biloko.domain.port.UserPort;
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
            throw new UserServiceException(UUID.randomUUID().toString(), "No tenant found with given id = " + tenantId);
        }
        User user = UserMapper.toUser(domainUser);
        user.setStatus(UserStatusEnum.ACTIVE);
        user.setPassword(domainUser.getPassword());
        user.setTenant(optTenant.get());
        user.setCreatedAt(LocalDateTime.now());
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
            throw new UserServiceException(UUID.randomUUID().toString(), "No user found with given id = " + id);
        }
        User user = optUser.get();
        DomainUser domainUser = UserMapper.toDomain(user);
        return Optional.of(domainUser);
    }
}
