package com.bacos.mokengeli.biloko.presentation.controller;

import com.bacos.mokengeli.biloko.application.exception.ServiceException;
import com.bacos.mokengeli.biloko.application.service.UserService;
import com.bacos.mokengeli.biloko.application.domain.DomainUser;
import com.bacos.mokengeli.biloko.presentation.exception.ResponseStatusWrapperException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping
    public ResponseEntity<DomainUser> createUser(@RequestBody DomainUser domainUser) {
        return ResponseEntity.ok(userService.createUser(domainUser));
    }


    @GetMapping("/by-username")
    public DomainUser getUserByEmployeeNumber(@RequestParam("username") String username) {
        try {
            return this.userService.getUserByEmployeeNumber(username);
        } catch (ServiceException e) {
            throw new ResponseStatusWrapperException(HttpStatus.BAD_REQUEST, e.getMessage(), e.getTechnicalId());
        }
    }

    @GetMapping
    public ResponseEntity<Page<DomainUser>> getAllUsers(
            @RequestParam("code") String tenantCode,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        try {
            Page<DomainUser> users = userService.getAllUsers(tenantCode, page, size);
            return ResponseEntity.ok(users);
        } catch (ServiceException e) {
            throw new ResponseStatusWrapperException(HttpStatus.BAD_REQUEST, e.getMessage(), e.getTechnicalId());
        }
    }
}
