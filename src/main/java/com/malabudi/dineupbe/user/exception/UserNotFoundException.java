package com.malabudi.dineupbe.user.exception;

import com.malabudi.dineupbe.common.exception.ResourceNotFoundException;

public class UserNotFoundException extends ResourceNotFoundException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
