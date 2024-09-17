package com.bacos.mokengeli.biloko.infrastructure.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "permissions")
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "permission_name", nullable = false, unique = true)
    private String permissionName;  // Nom de la permission (ex: 'create_order')

    private String description;  // Description de la permission

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Relation Many-to-Many avec Roles
    @ManyToMany(mappedBy = "permissions")  // Référence à la relation définie dans Role
    private Set<Role> roles;
}