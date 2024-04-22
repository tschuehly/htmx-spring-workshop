package de.tschuehly.easy.spring.auth;

import de.tschuehly.easy.spring.auth.group.GroupService;
import de.tschuehly.easy.spring.auth.user.UserService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Lab4Application {

  public static void main(String[] args) {
    SpringApplication.run(Lab4Application.class, args);
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
      groupService.createGroup("USER_GROUP");
      groupService.createGroup("ADMIN_GROUP");
    };
  }
}
