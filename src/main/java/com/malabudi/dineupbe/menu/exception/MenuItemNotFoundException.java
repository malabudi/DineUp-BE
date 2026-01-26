package com.malabudi.dineupbe.menu.exception;

public class MenuItemNotFoundException extends RuntimeException {
    public MenuItemNotFoundException(String message) {
        super(message);
    }
}
