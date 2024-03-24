package de.tschuehly.easy.spring.auth.user;

import java.util.UUID;

public class EasyUser {

  public final UUID uuid;
  public final String password;
  public final String username;

  public EasyUser(String username, String password) {
    this.uuid = UUID.randomUUID();
    this.password = password;
    this.username = username;
  }

  public EasyUser(UUID uuid, String username, String password) {
    this.uuid = uuid;
    this.username = username;
    this.password = password;
  }


}
