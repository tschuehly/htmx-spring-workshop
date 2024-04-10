package de.tschuehly.easy.spring.auth.htmx;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo.BuilderConfiguration;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

public class HtmxConfig {
  private final RequestMappingHandlerMapping requestMappingHandlerMapping;
  private final ApplicationContext applicationContext;

  public HtmxConfig(RequestMappingHandlerMapping requestMappingHandlerMapping, ApplicationContext applicationContext) {
    this.requestMappingHandlerMapping = requestMappingHandlerMapping;
    this.applicationContext = applicationContext;
  }

  ApplicationRunner applicationRunner(){
    return args -> {
      applicationContext.getBeansWithAnnotation(Controller.class).values().forEach(
          (bean) -> {
            Class<?> type = ClassUtils.getUserClass(bean.getClass());
            Arrays.stream(type.getMethods()).forEach(method -> {
              HtmxEndpoint annotation = AnnotationUtils.findAnnotation(method, HtmxEndpoint.class);
              Field field = ReflectionUtils.findField(annotation.htmxAction(), "path");
              String path = (String) ReflectionUtils.getField(field,null);

              new BuilderConfiguration();
              requestMappingHandlerMapping.registerMapping(
                  /* mapping = */ RequestMappingInfo.paths(path)
                      .methods(RequestMethod.GET).build(),
                  /* handler = */ bean,
                  /* method = */ method
              );

            });
          }
      );
    };
  }
}
