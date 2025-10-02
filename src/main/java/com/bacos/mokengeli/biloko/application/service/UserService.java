package com.bacos.mokengeli.biloko.application.service;

import com.bacos.mokengeli.biloko.application.domain.*;
import com.bacos.mokengeli.biloko.application.domain.model.ConnectedUser;
import com.bacos.mokengeli.biloko.application.exception.ServiceException;
import com.bacos.mokengeli.biloko.application.port.UserPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class UserService {

    private final UserPort userPort;
    private final UserAppService userAppService;
    private final NotificationService notificationService;

    @Autowired
    public UserService(UserPort userPort, UserAppService userAppService, NotificationService notificationService) {
        this.userPort = userPort;
        this.userAppService = userAppService;
        this.notificationService = notificationService;
    }

    public DomainUser createUser(DomainUser domainUser, String password) throws ServiceException {
        boolean adminUser = this.userAppService.isAdminUser();
        String role = domainUser.getRoles().get(0);

        ConnectedUser connectedUser = this.userAppService.getConnectedUser();
        if (!adminUser && !this.userAppService.isManagerUser()) {
            String employeeNumber = connectedUser.getEmployeeNumber();
            RoleEnum mainRole = this.userAppService.getMainRole();
            String uuid = UUID.randomUUID().toString();
            log.error("[{}]: User [{}] with role {} try to create a user with role {}",
                    uuid, employeeNumber, role, mainRole);
            throw new ServiceException(uuid, "You don't have the right to create user ");
        }
        String tenantCode = connectedUser.getTenantCode();
        if (this.userAppService.isManagerUser() &&
                (!tenantCode.equals(domainUser.getTenantCode()))) {

            String employeeNumber = connectedUser.getEmployeeNumber();
            String uuid = UUID.randomUUID().toString();
            log.error("[{}]: User [{}] with role {} try to create a user with for another restaurant {}",
                    uuid, employeeNumber, role, domainUser.getTenantCode());
            throw new ServiceException(uuid, "You don't have the right to create user for this restaurant");
        }
        DomainUser createdUser = userPort.createNewUser(domainUser, password);
        log.info("User created successfully with ID: {}", createdUser.getId());
// Envoyer l'email de bienvenue (asynchrone, ne bloque pas)
        try {
            notificationService.sendAccountCreationEmail(
                    createdUser.getTenantCode(),
                    createdUser.getEmail(),
                    createdUser.getFirstName(),
                    createdUser.getLastName(),
                    createdUser.getEmployeeNumber(),
                    createdUser.getUserName()
            );
        } catch (Exception e) {
            String errorId = UUID.randomUUID().toString();
            // Log l'erreur mais ne bloque pas la création de l'utilisateur
            log.warn("[{}]: Failed to send account creation email for user {}: {}",
                    errorId, createdUser.getUserName(), e.getMessage());
        }
        return createdUser;

    }


    public DomainUser getUserByEmployeeNumber(String employeeNumber) throws ServiceException {
        ConnectedUser connectedUser = this.userAppService.getConnectedUser();
        String employeeNumberOfConnectedUser = connectedUser.getEmployeeNumber();

        if (employeeNumber == null) {
            String uuid = UUID.randomUUID().toString();
            log.error("[{}]: User [{}] try to getUserByEmployeeNumber without giving that need value",
                    uuid, employeeNumberOfConnectedUser);
            throw new ServiceException(uuid, "The employee number must be given ");
        }
        Optional<DomainUser> optUser = this.userPort.getUserByEmployeeNumber(employeeNumber);
        if (optUser.isPresent()) {
            DomainUser domainUser = optUser.get();
            if (!userAppService.isAdminUser() && !connectedUser.getTenantCode().equals(domainUser.getTenantCode())) {
                String id = UUID.randomUUID().toString();
                log.error("[{}]: User [{}] tried to list users for tenant [{}]", id, connectedUser.getEmployeeNumber(),
                        domainUser.getTenantCode());
                throw new ServiceException(id, "Vous n'avez pas le droit d'effectuer cette action");
            }
            return domainUser;
        }
        String uuid = UUID.randomUUID().toString();
        log.error("[{}]: User [{}]. No User  found with employee number {}",
                uuid, employeeNumberOfConnectedUser, employeeNumber);
        throw new ServiceException(UUID.randomUUID().toString(), "User not found with employee number " + employeeNumber);
    }

    public DomainUser getUserByIdentifier(String identifier) throws ServiceException {

        Optional<DomainUser> optUser = this.userPort.getUserByIdentifier(identifier);
        if (optUser.isPresent()) {
            DomainUser domainUser = optUser.get();
            ConnectedUser user = userAppService.getConnectedUser();
            if (!userAppService.isAdminUser() && !user.getTenantCode().equals(domainUser.getTenantCode())) {
                String id = UUID.randomUUID().toString();
                log.error("[{}]: User [{}] tried to list users for tenant [{}]", id, user.getEmployeeNumber(), domainUser.getTenantCode());
                throw new ServiceException(id, "Vous n'avez pas le droit d'effectuer cette action");
            }
            return domainUser;
        }
        String uuid = UUID.randomUUID().toString();
        log.error("[{}]:  No User  found with employee number {}",
                uuid, identifier);
        throw new ServiceException(UUID.randomUUID().toString(), "User not found with employee number " + identifier);
    }

    public Page<DomainUser> getAllUsers(String tenantCode, int page, int size, String search) throws ServiceException {
        ConnectedUser user = userAppService.getConnectedUser();
        if (!userAppService.isAdminUser() && !user.getTenantCode().equals(tenantCode)) {
            String id = UUID.randomUUID().toString();
            log.error("[{}]: User [{}] tried to list users for tenant [{}]", id, user.getEmployeeNumber(), tenantCode);
            throw new ServiceException(id, "Vous n'avez pas le droit d'effectuer cette action");
        }
        return userPort.findAllUsersByTenant(tenantCode, page, size, search);
    }

    public List<String> getAuthorizedRoleByUserProfile() throws ServiceException {
        List<String> allRoles = this.userPort.getAllRoles();
        return getAuthorizedRoleByUserProfile(allRoles);
    }

    private List<String> getAuthorizedRoleByUserProfile(List<String> allRoles) {

        if (this.userAppService.isAdminUser()) {
            return allRoles;
        }
        if (this.userAppService.isManagerUser()) {
            return allRoles.stream().filter(f -> !f.equals(RoleEnum.ROLE_ADMIN.name()))
                    .toList();
        }
        RoleEnum mainRole = this.userAppService.getMainRole();
        return List.of(mainRole.name());
    }

    public List<DomainUserCount> getUserCountByRole(String tenantCode) throws ServiceException {
        ConnectedUser connected = userAppService.getConnectedUser();
        // seuls les admin ou users du même tenantCode y ont droit
        if (!userAppService.isAdminUser() && !connected.getTenantCode().equals(tenantCode)) {
            String id = UUID.randomUUID().toString();
            log.error("[{}]: User [{}] tried to count users of another tenant [{}]", id, connected.getEmployeeNumber(), tenantCode);
            throw new ServiceException(id, "You don't have the right to count users");
        }
        return userPort.countUsersByRole(tenantCode);
    }

    public boolean isUserNameAvailable(String userName) throws ServiceException {
        ConnectedUser connected = userAppService.getConnectedUser();
        if (!userAppService.isAdminUser() && !userAppService.isManagerUser()) {
            String id = UUID.randomUUID().toString();
            log.error("[{}]: User [{}] don't have right to check username availability", id, connected.getEmployeeNumber());
            throw new ServiceException(id, "You don't have the right to count users");
        }
        return this.userPort.isUserNameAvailable(userName);
    }

    public Boolean verifyValidationPin(Integer pin) {
        ConnectedUser connected = userAppService.getConnectedUser();
        return this.userPort.verifyPin(connected.getEmployeeNumber(), pin);
    }

    public UpdateUserPinResponse updatePin(UpdateUserPinRequest request) throws ServiceException {
        ConnectedUser connected = userAppService.getConnectedUser();
        boolean byPassOldPinValidation = false;


        if (!userAppService.isAdminUser() && userAppService.isManagerUser()) {
            if (request.getTargetEmployeeNumber() != null) {
                Optional<DomainUser> optUserOfTarget = this.userPort.getUserByIdentifier(request.getTargetEmployeeNumber());
                if (optUserOfTarget.isEmpty()) {
                    String id = UUID.randomUUID().toString();
                    log.error("[{}]:[{}] No user found with targetEmployee number {}", id, connected.getEmployeeNumber(),
                            request.getTargetEmployeeNumber());
                    throw new ServiceException(id, "You don't have the right to update the pin for " +
                            "another user users");
                }
                DomainUser domainTargetUser = optUserOfTarget.get();

                if (!connected.getTenantCode().equals(domainTargetUser.getTenantCode())) {
                    String id = UUID.randomUUID().toString();
                    log.error("[{}]:[{}] Connected User Tenant is {} but the targeted is {}", id, connected.getEmployeeNumber(),
                            connected.getTenantCode(), domainTargetUser.getTenantCode());
                    throw new ServiceException(id, "You don't have the right to update the pin for " +
                            "another user users");
                }
                if (!connected.getEmployeeNumber().equals(domainTargetUser.getEmployeeNumber())
                        && !connected.getEmployeeNumber().equals(domainTargetUser.getUserName())) {
                    byPassOldPinValidation = true;
                }

            }
        }

        // si le targetemployee number est different de null je le prend sinon je prend celui de l'utilisateur connecter
        String identifier = request.getTargetEmployeeNumber() != null ?
                request.getTargetEmployeeNumber() : connected.getEmployeeNumber();

        if (userAppService.isAdminUser()) {
            return userPort.updateUserPin(identifier, request, true);
        }


        return userPort.updateUserPin(identifier, request, byPassOldPinValidation);
    }
}
