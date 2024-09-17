package com.bacos.mokengeli.biloko.domain.port;

import com.bacos.mokengeli.biloko.domain.model.DomainUser;

import java.util.Optional;

public interface UserPort {
    DomainUser createNewUser(DomainUser domainUser);                  // Pour créer ou modifier un utilisateur
    void deleteById(Long id);              // Pour supprimer un utilisateur
    Optional<DomainUser> findById(Long id);      // Pour retrouver un utilisateur par son ID
}
