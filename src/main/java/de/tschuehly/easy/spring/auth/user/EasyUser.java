package de.tschuehly.easy.spring.auth.user;

import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class EasyUser implements UserDetails {

  public final String password;
  public final String username;
  public final List<Group> groupList;

  public EasyUser(String username, String password, List<Group> groupList) {
    this.username = username;
    this.password = password;
    this.groupList = groupList;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return groupList.stream().flatMap(it -> it.roleList.stream().map(role -> role.authority))
        .map(SimpleGrantedAuthority::new)
        .toList();
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
