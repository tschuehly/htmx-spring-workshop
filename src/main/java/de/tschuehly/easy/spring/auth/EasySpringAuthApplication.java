package de.tschuehly.easy.spring.auth;

import de.tschuehly.easy.spring.auth.store.EasyUserDetailsManager;
import org.eclipse.store.integrations.spring.boot.types.EclipseStoreSpringBoot;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@Import(EclipseStoreSpringBoot.class)
public class EasySpringAuthApplication {

  public static void main(String[] args) {
    SpringApplication.run(EasySpringAuthApplication.class, args);
  }

  @Bean
  PasswordEncoder passwordEncoder(){
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }
  @Bean
  public ApplicationRunner initializeUsers(EasyUserDetailsManager userDetailsManager) {
    return (args) -> {
      String userName = "admin";
      if(!userDetailsManager.userExists(userName)){
        userDetailsManager.createUser(
            User.builder()
                .username(userName)
                .password("{bcrypt}$2a$10$jdJGhzsiIqYFpjJiYWMl/eKDOd8vdyQis2aynmFN0dgJ53XvpzzwC")
                .build()
        );
        userDetailsManager.createGroup("GROUP_USERS", AuthorityUtils.createAuthorityList("ROLE_USER"));
        userDetailsManager.addUserToGroup(userName,"GROUP_USERS");
        userDetailsManager.createGroup("GROUP_ADMINS", AuthorityUtils.createAuthorityList("ROLE_ADMIN"));
        userDetailsManager.addUserToGroup(userName,"GROUP_ADMINS");
      }
    };
  }
}
