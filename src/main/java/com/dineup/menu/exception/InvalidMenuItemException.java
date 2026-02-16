package com.dineup.menu.exception;

import com.dineup.common.exception.ResourceBadRequestException;

public class InvalidMenuItemException extends ResourceBadRequestException {
    public InvalidMenuItemException(String message) {
        super(message);
    }
}
