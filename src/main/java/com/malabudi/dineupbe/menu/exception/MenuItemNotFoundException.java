package com.malabudi.dineupbe.menu.exception;

public class MenuItemNotFoundException extends RuntimeException {
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
