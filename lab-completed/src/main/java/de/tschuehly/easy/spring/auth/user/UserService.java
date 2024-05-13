package de.tschuehly.easy.spring.auth.user;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import net.datafaker.Faker;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class UserService {

  public final static List<EasyUser> easyUserList = new ArrayList<>();

  public List<EasyUser> findAll() {
    return easyUserList;
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
    List<EasyUser> easyUsers = easyUserList.stream().filter(
        it -> it.uuid.toString().contains(searchString)
              || it.username.contains(searchString)
              || it.password.contains(
            searchString)
    ).toList();
    if (easyUsers.isEmpty()) {
      throw new UserNotFoundException("No user found with the searchString: \"" + searchString + "\"");
    }
    return easyUsers;
  }

  public List<EasyUser> getPage(int pageNumber, int pageSize) {
    var startIndex = pageNumber * pageSize;
    var endIndex = startIndex + pageSize;
    return easyUserList.subList(startIndex, endIndex);
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

  public Flux<EasyUser> subscribeToNewUserCreation() {
    Faker faker = new Faker();
    return Flux.interval(Duration.ofSeconds(5)).map(
        val -> createUser(faker.internet().username(), faker.internet().password())
    );
  }
}
