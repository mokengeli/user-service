package com.bacos.mokengeli.biloko.infrastructure.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

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
    @JsonBackReference  // Empêche la sérialisation récursive
    @EqualsAndHashCode.Exclude  // Exclut du hashCode et equals
    @ToString.Exclude  // Exclut du toString()
    private Set<Role> roles;
}