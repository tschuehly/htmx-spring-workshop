package de.tschuehly.easy.spring.auth.user;

import de.tschuehly.easy.spring.auth.web.exception.InfoException;

public class UserNotFoundException extends InfoException {

  public UserNotFoundException(String message) {
    super(message);
  }
}
