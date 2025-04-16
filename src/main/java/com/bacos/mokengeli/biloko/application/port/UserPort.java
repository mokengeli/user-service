package com.bacos.mokengeli.biloko.application.port;

import com.bacos.mokengeli.biloko.application.domain.DomainUser;

import java.util.Optional;

public interface UserPort {
    DomainUser createNewUser(DomainUser domainUser);                  // Pour créer ou modifier un utilisateur
    Optional<DomainUser> getUserByEmployeeNumber(String employeeNumber);
}
