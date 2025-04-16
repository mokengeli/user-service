package com.bacos.mokengeli.biloko.application.service;

import com.bacos.mokengeli.biloko.application.exception.ServiceException;
import com.bacos.mokengeli.biloko.application.domain.DomainUser;
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


    public DomainUser getUserByEmployeeNumber(String employeeNumber) throws ServiceException {
        if (employeeNumber == null) {
            throw new ServiceException(UUID.randomUUID().toString(), "Le matricule doit etre fourni ");
        }
        Optional<DomainUser> optUser = this.userPort.getUserByEmployeeNumber(employeeNumber);
        if (optUser.isPresent()) {
            return optUser.get();
        }
        throw new ServiceException(UUID.randomUUID().toString(), "Utilisateur non trouvé");
    }




}
