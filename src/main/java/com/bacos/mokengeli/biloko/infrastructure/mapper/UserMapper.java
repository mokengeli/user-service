package com.bacos.mokengeli.biloko.infrastructure.mapper;

import com.bacos.mokengeli.biloko.application.domain.DomainUser;
import com.bacos.mokengeli.biloko.application.domain.DomainEstablishmentType;
import com.bacos.mokengeli.biloko.application.domain.DomainSubscriptionPlan;
import com.bacos.mokengeli.biloko.infrastructure.model.User;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class UserMapper {

    public DomainUser toDomain(final User user) {
        if (user == null) return null;

        // Récupérer le tenant
        var tenant = user.getTenant();
        DomainEstablishmentType estType = EstablishmentTypeMapper.toDomain(tenant.getEstablishmentType());
        DomainSubscriptionPlan subPlan = SubscriptionPlanMapper.toDomain(tenant.getSubscriptionPlan());

        // Transformer les rôles en List<String>
        List<String> roles = user.getRoles() != null
                ? user.getRoles().stream()
                .map(r -> r.getLabel())
                .collect(Collectors.toList())
                : new ArrayList<>();

        // Transformer les permissions
        List<String> permissions = user.getRoles() != null
                ? user.getRoles().stream()
                .flatMap(r -> r.getPermissions().stream())
                .map(p -> p.getLabel())
                .distinct()
                .collect(Collectors.toList())
                : new ArrayList<>();

        return DomainUser.builder()
                .id(user.getId())
                .tenantId(tenant.getId())
                .tenantCode(tenant.getCode())
                .tenantName(tenant.getName())
                .tenantEstablishmentType(estType)
                .tenantSubscriptionPlan(subPlan)
                .employeeNumber(user.getEmployeeNumber())
                .firstName(user.getFirstName())
                .userName(user.getUserName())
                .lastName(user.getLastName())
                .postName(user.getPostName())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .roles(roles)
                .permissions(permissions)
                .build();
    }

    public DomainUser toLightDomain(final User user) {
        if (user == null) return null;
        return DomainUser.builder()
                .id(user.getId())
                .tenantName(user.getTenant().getName())
                .employeeNumber(user.getEmployeeNumber())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .postName(user.getPostName())
                .userName(user.getUserName())
                .createdAt(user.getCreatedAt())
                .roles(user.getRoles().stream()
                        .map(r -> r.getLabel())
                        .collect(Collectors.toList()))
                .build();
    }

    public User toUser(final DomainUser domainUser) {
        if (domainUser == null) return null;
        return User.builder()
                .id(domainUser.getId())
                .firstName(domainUser.getFirstName())
                .lastName(domainUser.getLastName())
                .userName(domainUser.getUserName())
                .employeeNumber(domainUser.getEmployeeNumber())
                .email(domainUser.getEmail())
                // tenant, roles et permissions sont gérés par UserPort/Service
                .build();
    }
}
