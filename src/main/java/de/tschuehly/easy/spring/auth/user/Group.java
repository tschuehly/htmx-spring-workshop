package de.tschuehly.easy.spring.auth.user;

import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class Group {

  public final String groupName;
  public final List<Role> roleList;

  public Group(String groupName, List<Role> roleList) {
    this.groupName = groupName;
    this.roleList = roleList;
  }


  public List<GrantedAuthority> grantedAuthorityList() {
    return roleList.stream().map(role -> (GrantedAuthority) new SimpleGrantedAuthority(role.authority)).toList();
  }
}
