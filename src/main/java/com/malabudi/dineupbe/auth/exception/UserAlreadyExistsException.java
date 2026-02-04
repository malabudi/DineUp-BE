package com.malabudi.dineupbe.auth.exception;

import com.malabudi.dineupbe.common.exception.ResourceConflictException;

public class UserAlreadyExistsException extends ResourceConflictException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
