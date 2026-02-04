package com.malabudi.dineupbe.menu.exception;

import com.malabudi.dineupbe.common.exception.ResourceBadRequestException;

public class InvalidMenuItemException extends ResourceBadRequestException {
    public InvalidMenuItemException(String message) {
        super(message);
    }
}
