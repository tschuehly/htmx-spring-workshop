package de.tschuehly.easy.spring.auth;

import de.tschuehly.easy.spring.auth.group.GroupService;
import de.tschuehly.easy.spring.auth.user.UserService;
import net.datafaker.Faker;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class EasySpringAuthApplication {

  public static void main(String[] args) {
    SpringApplication.run(EasySpringAuthApplication.class, args);
  }
  @Bean
  public ApplicationRunner initializeUsers(UserService userService, GroupService groupService) {
    return (args) -> {
      userService.createUser(
          "Thomas",
          "This is a password"
      );
      userService.createUser(
          "Cassandra",
          "Test1234"
      );
      Faker faker = new Faker();
      for (int i = 0; i < 10000; i++) {
        userService.createUser(
            faker.internet().username(),
            faker.internet().password()
        );
      }
      groupService.createGroup("USER_GROUP");
      groupService.createGroup("ADMIN_GROUP");
    };
  }
}
