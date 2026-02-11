package com.malabudi.dineupbe.user.exception;

import com.malabudi.dineupbe.common.exception.ResourceNotFoundException;

public class UserNotFoundException extends ResourceNotFoundException {
    public UserNotFoundException() {
        super("User not found");
    }
}
