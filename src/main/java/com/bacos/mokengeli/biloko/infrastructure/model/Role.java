package com.bacos.mokengeli.biloko.infrastructure.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "role_name", nullable = false, unique = true)
    private String roleName;  // Nom du rôle (ex: 'admin', 'serveur')

    private String description;  // Description du rôle

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    // Relation Many-to-Many avec Permissions
    @ManyToMany
    @JoinTable(
            name = "role_permissions",  // Table intermédiaire
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions;  //

    // Relation Many-to-Many avec Users
    @ManyToMany(mappedBy = "roles")
    private Set<User> users;

}