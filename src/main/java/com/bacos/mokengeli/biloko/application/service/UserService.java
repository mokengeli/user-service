package com.bacos.mokengeli.biloko.application.service;

import com.bacos.mokengeli.biloko.application.exception.UserServiceException;
import com.bacos.mokengeli.biloko.application.model.DomainUser;
import com.bacos.mokengeli.biloko.application.port.UserPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

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

    public DomainUser getUserByEmail(String email) throws UserServiceException {
        if (email == null) {
            throw new UserServiceException(UUID.randomUUID().toString(), "Email doit etre fourni ");
        }
        Optional<DomainUser> optUser = this.userPort.getUserByEmail(email);
        if (optUser.isPresent()) {
            return optUser.get();
        }
        throw new UserServiceException(UUID.randomUUID().toString(), "Utilisateur non trouvé");
    }

    public DomainUser getUserByEmployeeNumber(String employeeNumber) throws UserServiceException {
        if (employeeNumber == null) {
            throw new UserServiceException(UUID.randomUUID().toString(), "Le matricule doit etre fourni ");
        }
        Optional<DomainUser> optUser = this.userPort.getUserByEmployeeNumber(employeeNumber);
        if (optUser.isPresent()) {
            return optUser.get();
        }
        throw new UserServiceException(UUID.randomUUID().toString(), "Utilisateur non trouvé");
    }




}
