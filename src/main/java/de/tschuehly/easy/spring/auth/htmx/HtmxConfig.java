package de.tschuehly.easy.spring.auth.htmx;

import de.tschuehly.spring.viewcomponent.core.component.ViewComponent;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.support.RouterFunctionMapping;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Configuration
public class HtmxConfig {

  private final RequestMappingHandlerMapping requestMappingHandlerMapping;
  private final ApplicationContext applicationContext;
  private final RouterFunctionMapping routerFunctionMapping;

  public HtmxConfig(RequestMappingHandlerMapping requestMappingHandlerMapping, ApplicationContext applicationContext,
      RouterFunctionMapping routerFunctionMapping) {
    this.requestMappingHandlerMapping = requestMappingHandlerMapping;
    this.applicationContext = applicationContext;
    this.routerFunctionMapping = routerFunctionMapping;
  }

  @Bean
  ApplicationRunner applicationRunner() {
    return args -> {
      applicationContext.getBeansWithAnnotation(ViewComponent.class)
          .values().forEach(controller ->
              {
                List<Field> fieldList = Arrays.stream(controller.getClass().getDeclaredFields())
                    .filter(method -> method.getType() == HtmxEndpoint.class)
                    .toList();

                fieldList.forEach(field -> {
                  RouterFunction<?> function = (RouterFunction<?>) ReflectionUtils.getField(field, controller);
                  if(routerFunctionMapping.getRouterFunction() == null){
                    routerFunctionMapping.setRouterFunction(function);
                  }
                  RouterFunction<?> routerFunction = routerFunctionMapping.getRouterFunction().andOther(function);
                  routerFunctionMapping.setRouterFunction(routerFunction);
                });
              }
          );
    };
  }
}
