package de.tschuehly.easy.spring.auth.store;


import de.tschuehly.easy.spring.auth.user.Group;
import de.tschuehly.easy.spring.auth.user.Role;
import de.tschuehly.easy.spring.auth.user.EasyUser;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Root {

  public final List<EasyUser> easyUserList = new ArrayList<>();
  public final List<Group> userGroupList = new ArrayList<>();
  public final List<Role> userRoleList = new ArrayList<>();
  public final Map<String, EasyUser> userNameMap = new HashMap<>();
  public final Map<EasyUser, List<Group>> userGroupMap = new HashMap<>();
  public final Map<Group, List<EasyUser>> groupUserMap = new HashMap<>();
  public final Map<Group, List<Role>> groupRoleMap = new HashMap<>();
}
