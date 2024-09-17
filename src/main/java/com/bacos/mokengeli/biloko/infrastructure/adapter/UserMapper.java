package com.bacos.mokengeli.biloko.infrastructure.adapter;


import com.bacos.mokengeli.biloko.domain.model.DomainUser;
import com.bacos.mokengeli.biloko.infrastructure.model.User;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserMapper {

    public DomainUser toDomain(final User user) {
        if (user == null) {
            return null;
        }
        return DomainUser.builder().id(user.getId()).firstName(user.getFirstName()).lastName(user.getLastName())
                .email(user.getEmail()).createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt()).build();
    }

    public User toUser(final DomainUser domainUser) {
        if (domainUser == null) {
            return null;
        }

        return User.builder().id(domainUser.getId()).firstName(domainUser.getFirstName())
                .email(domainUser.getEmail()).lastName(domainUser.getLastName())
                .postName(domainUser.getPostName()).build();
    }
}
