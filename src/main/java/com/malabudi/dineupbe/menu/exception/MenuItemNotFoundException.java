package com.malabudi.dineupbe.menu.exception;

import com.malabudi.dineupbe.common.exception.ResourceNotFoundException;

public class MenuItemNotFoundException extends ResourceNotFoundException {
    public MenuItemNotFoundException(String message) {
        super(message);
    }

    public MenuItemNotFoundException() {
        super("Menu item not found");
    }

    public MenuItemNotFoundException(Integer id) {
        super("Menu item with id " + id + " not found");
    }
}
