package de.tschuehly.easy.spring.auth.htmx;

import java.lang.annotation.Annotation;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Controller;


@ConfigurationProperties("htmx.endpoint")
public class HtmxEndpointConfig {

  private final List<Class<? extends Annotation>> annotationClassesToScan;

  public HtmxEndpointConfig(List<Class<? extends Annotation>> annotationClassesToScan) {
    this.annotationClassesToScan = annotationClassesToScan;
  }

  public List<Class<? extends Annotation>> annotationClassesToScan() {
    return annotationClassesToScan;
  }
}
