package de.tschuehly.easy.spring.auth.htmx;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.support.RouterFunctionMapping;

@Configuration
public class HtmxConfig {

  private final Logger logger = LoggerFactory.getLogger(HtmxConfig.class);

  private final ApplicationContext applicationContext;
  private final RouterFunctionMapping routerFunctionMapping;
  private final HtmxEndpointConfig htmxEndpointConfig;

  public HtmxConfig(ApplicationContext applicationContext,
      RouterFunctionMapping routerFunctionMapping, HtmxEndpointConfig htmxEndpointConfig) {
    this.applicationContext = applicationContext;
    this.routerFunctionMapping = routerFunctionMapping;
    this.htmxEndpointConfig = htmxEndpointConfig;
  }

  @Bean
  ApplicationRunner applicationRunner() {
    return args -> {
      Map<String, Object> beans = new HashMap<>();
      for (Class<? extends Annotation> aClass : htmxEndpointConfig.annotationClassesToScan()) {
        beans.putAll(applicationContext.getBeansWithAnnotation(aClass));
      }
      beans.values().forEach(bean ->
          {
            List<Field> fieldList = Arrays.stream(bean.getClass().getDeclaredFields())
                .filter(method -> AbstractHtmxEndpoint.class.isAssignableFrom(method.getType()))
                .toList();

            fieldList.forEach(field -> {
              AbstractHtmxEndpoint<?, ?> function = (AbstractHtmxEndpoint<?, ?>) ReflectionUtils.getField(field, bean);
              if (function == null) {
                throw new RuntimeException("Router function could not be found");
              }
              logger.info("Add endpoint: {}, with method: {}", function.path, function.method);
              if (routerFunctionMapping.getRouterFunction() == null) {
                routerFunctionMapping.setRouterFunction(function);
              } else {
                RouterFunction<?> routerFunction = routerFunctionMapping.getRouterFunction().andOther(function);
                routerFunctionMapping.setRouterFunction(routerFunction);
              }
            });
          }
      );
      System.out.println(routerFunctionMapping.getRouterFunction());
    };
  }
}
