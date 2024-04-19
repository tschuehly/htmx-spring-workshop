package de.tschuehly.easy.spring.auth.htmx;

import gg.jte.TemplateEngine;
import gg.jte.html.policy.PolicyGroup;
import gg.jte.html.policy.PreventInvalidAttributeNames;
import gg.jte.html.policy.PreventOutputInTagsAndAttributes;
import gg.jte.html.policy.PreventUnquotedAttributes;
import gg.jte.html.policy.PreventUppercaseTagsAndAttributes;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
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

  static class JtePolicy extends PolicyGroup {

    JtePolicy() {
      addPolicy(new PreventUppercaseTagsAndAttributes());
      addPolicy(new PreventOutputInTagsAndAttributes(false));
      addPolicy(new PreventUnquotedAttributes());
      addPolicy(new PreventInvalidAttributeNames());
    }
  }
}
