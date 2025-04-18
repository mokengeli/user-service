package com.bacos.mokengeli.biloko.application.service;

import com.bacos.mokengeli.biloko.application.domain.model.ConnectedUser;
import com.bacos.mokengeli.biloko.application.exception.ServiceException;
import com.bacos.mokengeli.biloko.application.domain.DomainUser;
import com.bacos.mokengeli.biloko.application.port.UserPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class UserService {

    private final UserPort userPort;
    private final UserAppService userAppService;
    @Autowired
    public UserService(UserPort userPort, UserAppService userAppService) {
        this.userPort = userPort;
        this.userAppService = userAppService;
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

    public Page<DomainUser> getAllUsers(String tenantCode, int page, int size) throws ServiceException {
        ConnectedUser user = userAppService.getConnectedUser();
        if (!userAppService.isAdminUser() && !user.getTenantCode().equals(tenantCode)) {
            String id = UUID.randomUUID().toString();
            log.error("[{}]: User [{}] tried to list users for tenant [{}]", id, user.getEmployeeNumber(), tenantCode);
            throw new ServiceException(id, "Forbidden");
        }
        return userPort.findAllUsersByTenant(tenantCode, page, size);
    }


}
