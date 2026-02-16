package com.dineup.menu.exception;

import com.dineup.common.exception.ResourceNotFoundException;

public class MenuGroupNotFoundException extends ResourceNotFoundException {
    public MenuGroupNotFoundException(Long id ) {
        super("Menu group with id " + id + " not found");
    }
}
