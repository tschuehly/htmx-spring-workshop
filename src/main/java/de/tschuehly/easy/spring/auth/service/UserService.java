package de.tschuehly.easy.spring.auth.service;

import de.tschuehly.easy.spring.auth.domain.EasyUser;
import de.tschuehly.easy.spring.auth.store.AuthRoot;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private final AuthRoot authRoot = new AuthRoot();

  public EasyUser findById(UUID uuid) {
    return authRoot.easyUserList.stream().filter(it -> Objects.equals(uuid, it.uuid)).findFirst()
        .orElseThrow(() -> new RuntimeException("User Not Found"));
  }

  public List<EasyUser> findAll() {
    return authRoot.easyUserList;
  }

  public EasyUser createUser(String username, String password) {
    EasyUser newUser = new EasyUser(
        username,
        password
    );
    authRoot.easyUserList.add(newUser);
    return newUser;
  }

  public EasyUser saveUser(UUID uuid, String username, String password) {
    EasyUser userToUpdate = findById(uuid);
    EasyUser newUser = new EasyUser(
        userToUpdate.uuid,
        username,
        password,
        userToUpdate.groupList
    );
    authRoot.easyUserList.set(
        authRoot.easyUserList.indexOf(userToUpdate),
        newUser
    );
    return newUser;
  }
}
