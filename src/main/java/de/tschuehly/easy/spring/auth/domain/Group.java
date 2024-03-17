package de.tschuehly.easy.spring.auth.domain;

import java.util.List;

public class Group {

  public final String groupName;
  public final List<Role> roleList;

  public Group(String groupName, List<Role> roleList) {
    this.groupName = groupName;
    this.roleList = roleList;
  }

}
