package com.malabudi.dineupbe.menu.exception;

import com.malabudi.dineupbe.common.exception.ResourceNotFoundException;

public class MenuGroupNotFoundException extends ResourceNotFoundException {
    public MenuGroupNotFoundException() {
      super("Menu group not found");
    }

    public MenuGroupNotFoundException(String message) {
        super(message);
    }

  public MenuGroupNotFoundException(Long id ) {
    super("Menu group with id " + id + " not found");
  }
}
