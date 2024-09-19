package com.bacos.mokengeli.biloko.application.exception;

import lombok.Getter;

@Getter
public class UserServiceException extends Exception {

    private String technicalId;

    public UserServiceException(String technicalId, String message) {
        super(message);
        this.technicalId = technicalId;
    }
}
