package de.tschuehly.easy.spring.auth.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EasyUser {
  public final UUID uuid;
  public final String password;
  public final String username;
  public final List<Group> groupList;

  public EasyUser( String username,String password) {
    this.uuid = UUID.randomUUID();
    this.password = password;
    this.username = username;
    this.groupList = new ArrayList<>();
  }

  public EasyUser(UUID uuid, String username, String password, List<Group> groupList) {
    this.uuid = uuid;
    this.username = username;
    this.password = password;
    this.groupList = groupList;
  }


}
