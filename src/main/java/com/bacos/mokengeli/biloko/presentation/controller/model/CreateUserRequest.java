package com.bacos.mokengeli.biloko.presentation.controller.model;

import com.bacos.mokengeli.biloko.application.domain.DomainUser;
import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreateUserRequest extends DomainUser {
    private String password;
}
