package com.bacos.mokengeli.biloko.application.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DomainUserCount {
    private String role;
    private Long count;
}
