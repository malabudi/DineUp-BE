package com.dineup.menu.exception;

import com.dineup.common.exception.ResourceNotFoundException;

public class MenuItemNotFoundException extends ResourceNotFoundException {
    public MenuItemNotFoundException(Long id) {
        super("Menu item with id " + id + " not found");
    }
}
