package de.tschuehly.easy.spring.auth.group;

import de.tschuehly.easy.spring.auth.user.EasyUser;
import java.util.ArrayList;
import java.util.List;

public class EasyGroup {
  public final String groupName;
  public final List<EasyUser> memberList = new ArrayList<>();

  public EasyGroup(String groupName) {
    this.groupName = groupName;
  }

}
