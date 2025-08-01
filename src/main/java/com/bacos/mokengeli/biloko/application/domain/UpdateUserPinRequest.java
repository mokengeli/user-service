package com.bacos.mokengeli.biloko.application.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateUserPinRequest {
    private String currentPin;      // optional, required if PIN already set
    private Integer newPin;          // exactly 4 digits
    private String targetEmployeeNumber; // optional, managers only
}
