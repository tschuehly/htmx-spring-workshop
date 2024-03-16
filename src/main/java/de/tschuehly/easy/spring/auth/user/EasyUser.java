package de.tschuehly.easy.spring.auth.user;

import java.util.List;

public class EasyUser {

  public final String password;
  public final String username;
  public final List<Group> groupList;

  public EasyUser(String username, String password, List<Group> groupList) {
    this.username = username;
    this.password = password;
    this.groupList = groupList;
  }


}
