package de.tschuehly.easy.spring.auth.htmx;

import gg.jte.TemplateEngine;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JteConfig {

  final
  TemplateEngine templateEngine;

  public JteConfig(TemplateEngine templateEngine) {
    this.templateEngine = templateEngine;
  }

  @PostConstruct
  void configureTemplateEngine() {
    templateEngine.setHtmlPolicy(new JtePolicy());
  }


}
