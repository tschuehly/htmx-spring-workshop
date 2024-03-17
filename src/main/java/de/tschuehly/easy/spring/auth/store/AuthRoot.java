package de.tschuehly.easy.spring.auth.store;


import de.tschuehly.easy.spring.auth.domain.Group;
import de.tschuehly.easy.spring.auth.domain.Role;
import de.tschuehly.easy.spring.auth.domain.EasyUser;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AuthRoot {
  public final List<EasyUser> easyUserList = new ArrayList<>();
  public final List<Group> userGroupList = new ArrayList<>();
  public final Set<Role> userRoleList = new HashSet<>();
}
