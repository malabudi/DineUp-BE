package com.malabudi.dineupbe.menu.exception;

import com.malabudi.dineupbe.common.exception.ResourceBadRequestException;

public class InvalidMenuGroupException extends ResourceBadRequestException {
    public InvalidMenuGroupException(String message) {
        super(message);
    }
}
