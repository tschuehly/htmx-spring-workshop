package de.tschuehly.easy.spring.auth.user;

import de.tschuehly.easy.spring.auth.store.Root;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.eclipse.store.storage.embedded.types.EmbeddedStorageManager;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {

  private final EmbeddedStorageManager embeddedStorageManager;

  public UserRepository(EmbeddedStorageManager embeddedStorageManager) {
    this.embeddedStorageManager = embeddedStorageManager;
  }

  private List<EasyUser> getUserList() {
    return ((Root) embeddedStorageManager.root()).easyUserList;
  }

  private Root getRoot() {
    return (Root) embeddedStorageManager.root();
  }

  private List<Group> getGroupList() {
    return ((Root) embeddedStorageManager.root()).userGroupList;
  }

  private Set<Role> getRoleList() {
    return ((Root) embeddedStorageManager.root()).userRoleList;

  }

  public EasyUser createUser(String userName, String password) {
    EasyUser easyUser = new EasyUser(
        userName,
        password,
        new ArrayList<>()
    );
    getUserList().add(easyUser);
    embeddedStorageManager.store(getUserList());
    return easyUser;
  }

  public void createGroup(String groupName, List<Role> roles) {
    if (findGroup(groupName).isPresent()) {
      throw new RuntimeException("group already exists");
    }
    getGroupList().add(
        new Group(
            groupName,
            roles
        )
    );
    embeddedStorageManager.store(getGroupList());
  }

  public Set<Role> createRoles(List<Role> newRoleList) {

    List<Role> existingRoles = getRoleList().stream().map(
        existingRole -> newRoleList.stream().anyMatch(newRole -> Objects.equals(newRole.authority, existingRole.authority))
            ? existingRole : null
    ).filter(Objects::nonNull).toList();

    List<Role> notExistingRoleList = newRoleList.stream()
        .filter(auth -> existingRoles.stream().noneMatch(role -> Objects.equals(role.authority, auth.authority)))
        .toList();

    getRoleList().addAll(notExistingRoleList);
    embeddedStorageManager.store(getRoleList());
    return new HashSet<>(notExistingRoleList);
  }


  public Optional<Group> findGroup(String groupName) {
    return getGroupList().stream().filter(
        it -> Objects.equals(it.groupName, groupName)
    ).findFirst();
  }

  public List<Group> findAllGroups() {
    return getRoot().userGroupList;
  }

  public List<EasyUser> findUsersInGroup(String groupName) {
    Group group = findGroup(groupName).orElseThrow(() -> new RuntimeException("No group found"));
    return getUserList().stream().filter(user -> user.groupList.contains(group)).toList();
  }

  public void deleteGroup(String groupName) {
    Group group = findGroup(groupName).orElseThrow(() -> new RuntimeException("No group found"));
    getRoot().userGroupList.remove(group);
    getRoot().easyUserList.forEach(user -> user.groupList.remove(group));
    embeddedStorageManager.storeAll(getRoot().easyUserList,getRoot().userGroupList);
  }

  public Optional<EasyUser> findUser(String username) {
    return getRoot().easyUserList.stream().filter(user -> Objects.equals(user.username, username)).findFirst();
  }

  public void deleteUser(String username) {
    getUserList().removeIf(user -> Objects.equals(user.username, username));
    embeddedStorageManager.store(getUserList());
  }

  public List<Role> findRoles(List<String> authorities) {

    return authorities.stream().map(auth ->
        getRoleList().stream().filter(role -> Objects.equals(role.authority, auth)).findFirst().orElseThrow(
            () -> new RuntimeException("No role found with authority: " + authorities)
        )
    ).toList();
  }

  public void updateUser(String username, String password, List<Role> roles) {
    EasyUser user = findUser(username).orElseThrow(() -> new RuntimeException("No User with username found:" + username));
  }
}
