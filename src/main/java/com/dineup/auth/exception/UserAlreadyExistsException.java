package com.dineup.auth.exception;

import com.dineup.common.exception.ResourceConflictException;

public class UserAlreadyExistsException extends ResourceConflictException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
