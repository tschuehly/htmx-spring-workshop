package de.tschuehly.easy.spring.auth;

import gg.jte.CodeResolver;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.html.OwaspHtmlPolicy;
import gg.jte.html.policy.PolicyGroup;
import gg.jte.html.policy.PreventInvalidAttributeNames;
import gg.jte.html.policy.PreventOutputInTagsAndAttributes;
import gg.jte.html.policy.PreventUnquotedAttributes;
import gg.jte.html.policy.PreventUppercaseTagsAndAttributes;
import gg.jte.resolve.DirectoryCodeResolver;
import gg.jte.springframework.boot.autoconfigure.JteProperties;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JteConfig {
  @Bean
  TemplateEngine configureTemplateEngine(JteProperties jteProperties){
    String[] split = jteProperties.getTemplateLocation().split("/");
    CodeResolver codeResolver = new DirectoryCodeResolver(FileSystems.getDefault().getPath("", split));
    TemplateEngine templateEngine = TemplateEngine.create(codeResolver, Paths.get("jte-classes"), ContentType.Html,
        getClass().getClassLoader());
    templateEngine.setHtmlPolicy(new JtePolicy());
    return templateEngine;

  }
  static class JtePolicy extends PolicyGroup{
    JtePolicy(){
      addPolicy(new PreventUppercaseTagsAndAttributes());
      addPolicy(new PreventOutputInTagsAndAttributes(false));
      addPolicy(new PreventUnquotedAttributes());
      addPolicy(new PreventInvalidAttributeNames());
    }
  }
}
