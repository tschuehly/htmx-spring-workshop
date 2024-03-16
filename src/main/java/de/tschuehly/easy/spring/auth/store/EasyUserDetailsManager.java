package de.tschuehly.easy.spring.auth.store;

import de.tschuehly.easy.spring.auth.user.EasyUser;
import de.tschuehly.easy.spring.auth.user.Group;
import de.tschuehly.easy.spring.auth.user.Role;
import de.tschuehly.easy.spring.auth.user.UserRepository;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.GroupManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

@Service
public class EasyUserDetailsManager implements UserDetailsManager, GroupManager {

  private final UserRepository userRepository;

  public EasyUserDetailsManager(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public List<String> findAllGroups() {
    return userRepository.findAllGroups().stream().map(it -> it.groupName).toList();
  }

  @Override
  public List<String> findUsersInGroup(String groupName) {
    return userRepository.findUsersInGroup(groupName).stream()
        .map(it -> it.username).toList();
  }

  @Override
  public void createGroup(String groupName, List<GrantedAuthority> authorities) {
    if (authorities.stream().anyMatch(it -> !it.getAuthority().startsWith("ROLE_"))) {
      throw new RuntimeException("Authority must start with ROLE_");
    }
    List<Role> roleList = authorities.stream().map(auth -> new Role(auth.getAuthority())).toList();
    userRepository.createGroup(groupName, roleList);
  }

  @Override
  public void deleteGroup(String groupName) {
    userRepository.deleteGroup(groupName);
  }

  @Override
  public void renameGroup(String oldName, String newName) {

  }

  @Override
  public void addUserToGroup(String username, String group) {

  }

  @Override
  public void removeUserFromGroup(String username, String groupName) {

  }

  @Override
  public List<GrantedAuthority> findGroupAuthorities(String groupName) {
    Group group = userRepository.findGroup(groupName).orElseThrow(
        () -> new RuntimeException("No Group found with grouName" + groupName)
    );
    return group.grantedAuthorityList();
  }

  @Override
  public void addGroupAuthority(String groupName, GrantedAuthority authority) {

  }

  @Override
  public void removeGroupAuthority(String groupName, GrantedAuthority authority) {

  }

  @Override
  public void createUser(UserDetails user) {
    userRepository.createUser(user.getUsername(),user.getPassword());
  }

  @Override
  public void updateUser(UserDetails user) {
    List<Role> roles = userRepository.findRoles(
        user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
    userRepository.updateUser(
        user.getUsername(),
        user.getPassword(),
        roles
    );

  }

  @Override
  public void deleteUser(String username) {
    userRepository.deleteUser(username);

  }

  @Override
  public void changePassword(String oldPassword, String newPassword) {
  }

  @Override
  public boolean userExists(String username) {
    return userRepository.findUser(username).isPresent();
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return userRepository.findUser(username).orElseThrow(
        () -> new RuntimeException("No User found with username: " + username)
    );
  }
}
