package com.bacos.mokengeli.biloko.domain.exception;

import lombok.Getter;

@Getter
public class UserServiceException extends RuntimeException {

    private String technicalId;

    public UserServiceException(String technicalId, String message) {
        super(message);
        this.technicalId = technicalId;
    }
}
