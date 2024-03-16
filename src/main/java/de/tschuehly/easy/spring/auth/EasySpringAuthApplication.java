package de.tschuehly.easy.spring.auth;

import org.eclipse.store.integrations.spring.boot.types.EclipseStoreSpringBoot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(EclipseStoreSpringBoot.class)
public class EasySpringAuthApplication {

  public static void main(String[] args) {
    SpringApplication.run(EasySpringAuthApplication.class, args);
  }

}
