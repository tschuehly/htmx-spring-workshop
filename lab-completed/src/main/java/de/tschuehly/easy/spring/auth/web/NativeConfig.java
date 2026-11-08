package de.tschuehly.easy.spring.auth.web;

import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import java.lang.reflect.Parameter;
import java.util.List;
import net.datafaker.providers.base.BaseProviders;
import net.datafaker.providers.base.Name;
import org.springframework.aot.hint.ExecutableMode;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;

/**
 * Makes spring-view-component work in a GraalVM native image.
 */
@Configuration
@ImportRuntimeHints(NativeConfig.Hints.class)
public class NativeConfig {

  /**
   * spring-view-component's default engine resolves the template class directory via ClassLoader.getResource(""),
   * which a native image doesn't have. Load the precompiled templates from the classpath instead. Skipped with
   * local-development (dev profile) so hot reload keeps the library's engine.
   */
  @Bean
  @ConditionalOnProperty(name = "spring.view-component.local-development", havingValue = "false", matchIfMissing = true)
  TemplateEngine jteTemplateEngine() {
    return TemplateEngine.createPrecompiled(ContentType.Html);
  }

  static class Hints implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
      // kotlin-reflect calls Parameter.getName reflectively when Spring invokes the library's Kotlin @Bean methods
      hints.reflection().registerType(Parameter.class, MemberCategory.INVOKE_PUBLIC_METHODS);
      // datafaker resolves #{Name.male_first_name}-style expressions in credentials().username() reflectively;
      // its bundled metadata misses these (found with the native-image tracing agent)
      hints.reflection().registerType(BaseProviders.class, builder -> builder.withMethod("name", List.of(),
          ExecutableMode.INVOKE));
      hints.reflection().registerType(Name.class, MemberCategory.INVOKE_PUBLIC_METHODS);
    }
  }
}
