package de.tschuehly.easy.spring.auth.user;

import de.tschuehly.easy.spring.auth.store.Root;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
  private List<EasyUser> getUserList(){
    return ((Root) embeddedStorageManager.root()).easyUserList;
  }

  private Root getRoot(){
    return (Root) embeddedStorageManager.root();
  }
  private List<Group> getGroupList(){
    return ((Root) embeddedStorageManager.root()).userGroupList;
  }
  private List<Role> getRoleList(){
    return ((Root) embeddedStorageManager.root()).userRoleList;

  }
  public EasyUser createUser(UserDetails userDetails){
    EasyUser easyUser = new EasyUser(
        userDetails.getUsername(),
        userDetails.getPassword(),
        groupList);
    getUserList().add(easyUser);
    embeddedStorageManager.store(getUserList());
    return easyUser;
  }

  public void createGroup(String groupName, List<GrantedAuthority> authorities) {
    if(findGroup(groupName).isPresent()){
      throw new RuntimeException("group already exists");
    }

    getGroupList().add()
  }

  public List<Role> createRoles(List<GrantedAuthority> authorities){
    authorities.stream().filter(
        it -> it.getAuthority()
    )
    getRoleList().stream().
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
    return getRoot().groupUserMap.get(group);
  }

  public void deleteGroup(String groupName) {
    Group group = findGroup(groupName).orElseThrow(() -> new RuntimeException("No group"));
    getRoot().groupRoleMap.remove(group);
    getRoot().groupUserMap.remove(group);
  }

  public Optional<EasyUser> findUser(String username) {
    return Optional.ofNullable(getRoot().userNameMap.get(username));
  }
}
