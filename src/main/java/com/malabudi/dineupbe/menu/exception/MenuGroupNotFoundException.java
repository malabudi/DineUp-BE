package com.malabudi.dineupbe.menu.exception;

public class MenuGroupNotFoundException extends RuntimeException {
    public MenuGroupNotFoundException() {
      super("Menu group not found");
    }

    public MenuGroupNotFoundException(String message) {
        super(message);
    }

  public MenuGroupNotFoundException(Integer id ) {
    super("Menu group with id " + id + " not found");
  }
}
