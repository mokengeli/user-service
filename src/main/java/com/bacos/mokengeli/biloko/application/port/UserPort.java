package com.bacos.mokengeli.biloko.application.port;

import com.bacos.mokengeli.biloko.application.domain.DomainUser;
import com.bacos.mokengeli.biloko.application.domain.DomainUserCount;
import com.bacos.mokengeli.biloko.application.exception.ServiceException;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface UserPort {
    DomainUser createNewUser(DomainUser domainUser, String password);                  // Pour créer ou modifier un utilisateur

    Optional<DomainUser> getUserByEmployeeNumber(String employeeNumber);

    Page<DomainUser> findAllUsersByTenant(String tenantCode, int page, int size, String search) throws ServiceException;

    List<String> getAllRoles();

    List<DomainUserCount> countUsersByRole(String tenantCode);

    boolean isUserNameAvailable(String userName);
}
