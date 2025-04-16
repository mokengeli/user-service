package com.bacos.mokengeli.biloko.application.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DomainTenant {
    private Long id;
    private String code;
    private String name;
}
