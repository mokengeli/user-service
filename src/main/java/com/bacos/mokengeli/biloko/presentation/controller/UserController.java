package com.bacos.mokengeli.biloko.presentation.controller;

import com.bacos.mokengeli.biloko.application.service.UserService;
import com.bacos.mokengeli.biloko.domain.model.DomainUser;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PutMapping("/{id}")
    public ResponseEntity<DomainUser> updateUser(@PathVariable Long id, @RequestBody DomainUser domainUser) {
        return ResponseEntity.ok(userService.updateUser(id, domainUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
