package com.dineup.auth.exception;

import com.dineup.common.exception.ResourceUnauthorizedException;

public class InvalidCredentialsException extends ResourceUnauthorizedException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
