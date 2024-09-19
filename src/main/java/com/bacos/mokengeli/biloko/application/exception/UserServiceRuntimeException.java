package com.bacos.mokengeli.biloko.application.exception;

import lombok.Getter;

@Getter
public class UserServiceRuntimeException extends RuntimeException {

    private String technicalId;

    public UserServiceRuntimeException(String technicalId, String message) {
        super(message);
        this.technicalId = technicalId;
    }
}
