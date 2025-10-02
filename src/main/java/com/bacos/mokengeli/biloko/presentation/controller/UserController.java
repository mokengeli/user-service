package com.bacos.mokengeli.biloko.presentation.controller;

import com.bacos.mokengeli.biloko.application.domain.DomainUser;
import com.bacos.mokengeli.biloko.application.domain.DomainUserCount;
import com.bacos.mokengeli.biloko.application.domain.UpdateUserPinRequest;
import com.bacos.mokengeli.biloko.application.domain.UpdateUserPinResponse;
import com.bacos.mokengeli.biloko.application.exception.ServiceException;
import com.bacos.mokengeli.biloko.application.service.UserService;
import com.bacos.mokengeli.biloko.presentation.controller.model.CreateUserRequest;
import com.bacos.mokengeli.biloko.presentation.exception.ResponseStatusWrapperException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping
    public DomainUser createUser(@RequestBody CreateUserRequest createUserRequest) {
        try {
            return userService.createUser(createUserRequest, createUserRequest.getPassword());
        } catch (ServiceException e) {
            throw new ResponseStatusWrapperException(HttpStatus.BAD_REQUEST, e.getMessage(), e.getTechnicalId());
        }
    }


    @GetMapping("/by-employee-number")
    public DomainUser getUserByEmployeeNumber(@RequestParam("employeeNumber") String employeeNumber) {
        try {
            return this.userService.getUserByEmployeeNumber(employeeNumber);
        } catch (ServiceException e) {
            throw new ResponseStatusWrapperException(HttpStatus.BAD_REQUEST, e.getMessage(), e.getTechnicalId());
        }
    }

    @GetMapping("/by-identifier")
    public DomainUser getUserByIdentifier(@RequestParam("identifier") String identifier) {
        try {
            return this.userService.getUserByIdentifier(identifier);
        } catch (ServiceException e) {
            throw new ResponseStatusWrapperException(HttpStatus.BAD_REQUEST, e.getMessage(), e.getTechnicalId());
        }
    }

    @GetMapping
    public Page<DomainUser> getAllUsers(
            @RequestParam("code") String tenantCode,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "search", required = false) String search
    ) {
        try {
            return userService.getAllUsers(tenantCode, page, size, search);
        } catch (ServiceException e) {
            throw new ResponseStatusWrapperException(
                    HttpStatus.BAD_REQUEST,
                    e.getMessage(),
                    e.getTechnicalId()
            );
        }
    }

    /**
     * return the role an user can see respecting a hierarchy
     * according to his own ^profile
     *
     * @return
     */
    @GetMapping("/roles")
    public List<String> getAuthorizedRoleByUserProfile() {
        try {
            return userService.getAuthorizedRoleByUserProfile();
        } catch (ServiceException e) {
            throw new ResponseStatusWrapperException(HttpStatus.BAD_REQUEST, e.getMessage(), e.getTechnicalId());
        }
    }

    @GetMapping("/count-by-role")
    public List<DomainUserCount> countByRole(@RequestParam("tenantCode") String tenantCode) {
        try {
            return userService.getUserCountByRole(tenantCode);
        } catch (ServiceException e) {
            throw new ResponseStatusWrapperException(
                    HttpStatus.BAD_REQUEST, e.getMessage(), e.getTechnicalId());
        }
    }


    @GetMapping("/username/check")
    public boolean isUserNameAvailable(@RequestParam("userName") String userName) {
        try {
            return this.userService.isUserNameAvailable(userName);
        } catch (ServiceException e) {
            throw new ResponseStatusWrapperException(
                    HttpStatus.BAD_REQUEST, e.getMessage(), e.getTechnicalId());
        }
    }

    @GetMapping("/verify/validation/pin")
    public Boolean isValidationPinOk(
            @RequestParam("pin") Integer pin
    ) {
        return userService.verifyValidationPin(pin);
    }

    @PutMapping("/pin")
    public UpdateUserPinResponse updatePin(
            @RequestBody UpdateUserPinRequest request
    ) {
        try {
            return userService.updatePin(request);
        } catch (ServiceException e) {
            throw new ResponseStatusWrapperException(
                    HttpStatus.BAD_REQUEST, e.getMessage(), e.getTechnicalId());
        }
    }

    @PutMapping("/pwd")
    public DomainUser updatePassword(
            @RequestParam("encodedPwd") String encodedPwd
    ) {
        return userService.updatePwd(encodedPwd);
    }
}
