package com.bacos.mokengeli.biloko.presentation.controller;

import com.bacos.mokengeli.biloko.application.exception.UserServiceException;
import com.bacos.mokengeli.biloko.application.service.UserService;
import com.bacos.mokengeli.biloko.application.model.DomainUser;
import com.bacos.mokengeli.biloko.presentation.exception.ResponseStatusWrapperException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
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
        } catch (UserServiceException e) {
            throw new ResponseStatusWrapperException(HttpStatus.BAD_REQUEST, e.getMessage(), e.getTechnicalId());
        }
    }
}
