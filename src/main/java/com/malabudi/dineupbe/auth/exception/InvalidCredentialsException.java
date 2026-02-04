package com.malabudi.dineupbe.auth.exception;

import com.malabudi.dineupbe.common.exception.ResourceUnauthorizedException;

public class InvalidCredentialsException extends ResourceUnauthorizedException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
