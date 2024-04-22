package de.tschuehly.easy.spring.auth;

import de.tschuehly.easy.spring.auth.user.UserService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Lab3Application {

  public static void main(String[] args) {
    SpringApplication.run(Lab3Application.class, args);
  }
  @Bean
  public ApplicationRunner initializeUsers(UserService userService) {
    return (args) -> userService.createUser(
        "Thomas",
        "This is a password"
    );
  }
}
