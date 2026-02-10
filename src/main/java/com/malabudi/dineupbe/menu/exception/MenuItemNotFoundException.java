package com.malabudi.dineupbe.menu.exception;

import com.malabudi.dineupbe.common.exception.ResourceNotFoundException;

public class MenuItemNotFoundException extends ResourceNotFoundException {
    public MenuItemNotFoundException(Long id) {
        super("Menu item with id " + id + " not found");
    }
}
