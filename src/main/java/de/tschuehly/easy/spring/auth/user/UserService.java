package de.tschuehly.easy.spring.auth.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  public final static List<EasyUser> easyUserList = new ArrayList<>();

  public List<EasyUser> findAll() {
    return easyUserList;
  }

  public List<EasyUser> getPage(int pageNumber, int pageSize) {
    var startIndex = pageNumber * pageSize;
    var endIndex = startIndex + pageSize;
    return easyUserList.subList(startIndex, endIndex);
  }

  public EasyUser createUser(String username, String password) {
    EasyUser newUser = new EasyUser(
        username,
        password
    );
    easyUserList.add(newUser);
    return newUser;
  }

  public List<EasyUser> searchUser(String searchString) {
    return easyUserList.stream().filter(
        it -> it.uuid.toString().contains(searchString)
              || it.username.contains(searchString)
              || it.password.contains(
            searchString)
    ).toList();
  }


  public EasyUser findById(UUID uuid) {
    return easyUserList.stream().filter(it -> Objects.equals(uuid, it.uuid)).findFirst()
        .orElseThrow(() -> new RuntimeException("User Not Found"));
  }

  public EasyUser saveUser(UUID uuid, String username, String password) {
    EasyUser userToUpdate = findById(uuid);
    EasyUser newUser = new EasyUser(
        userToUpdate.uuid,
        username,
        password
    );
    easyUserList.set(
        easyUserList.indexOf(userToUpdate),
        newUser
    );
    return newUser;
  }

}
