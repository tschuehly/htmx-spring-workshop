package de.tschuehly.easy.spring.auth.store;


import de.tschuehly.easy.spring.auth.group.Group;
import de.tschuehly.easy.spring.auth.role.Role;
import de.tschuehly.easy.spring.auth.user.EasyUser;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AuthRoot {

  public final List<EasyUser> easyUserList = new ArrayList<>();
  public final List<Group> userGroupList = new ArrayList<>();
  public final Set<Role> userRoleList = new HashSet<>();
}
