package com.bacos.mokengeli.biloko.application.service;

import com.bacos.mokengeli.biloko.domain.model.DomainUser;
import com.bacos.mokengeli.biloko.domain.port.UserPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserPort userPort;

    @Autowired
    public UserService(UserPort userPort) {
        this.userPort = userPort;
    }

    public DomainUser createUser(DomainUser domainUser) {
        return userPort.createNewUser(domainUser);
    }

    public DomainUser updateUser(Long id, DomainUser domainUser) {
        Optional<DomainUser> existingUser = userPort.findById(id);
        if (existingUser.isPresent()) {
            DomainUser updatedDomainUser = existingUser.get();
            updatedDomainUser.setFirstName(domainUser.getFirstName());
            updatedDomainUser.setLastName(domainUser.getLastName());
            updatedDomainUser.setPostName(domainUser.getPostName());
            updatedDomainUser.setEmail(domainUser.getEmail());
            updatedDomainUser.setPassword(domainUser.getPassword());
            updatedDomainUser.setUpdatedAt(domainUser.getUpdatedAt());
            return userPort.createNewUser(updatedDomainUser);
        } else {
            throw new IllegalArgumentException("Utilisateur non trouvé");
        }
    }

    public void deleteUser(Long id) {
        userPort.deleteById(id);
    }
}
