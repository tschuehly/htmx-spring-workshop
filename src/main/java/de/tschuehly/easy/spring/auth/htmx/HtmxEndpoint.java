package de.tschuehly.easy.spring.auth.htmx;

import de.tschuehly.spring.viewcomponent.core.IViewContext;
import de.tschuehly.spring.viewcomponent.jte.ViewContext;
import java.lang.reflect.ParameterizedType;
import java.util.function.Function;
import java.util.function.Supplier;
import org.springframework.http.HttpMethod;

public class HtmxEndpoint<T> extends AbstractHtmxEndpoint<T, ViewContext> {

  public HtmxEndpoint(String path, HttpMethod method,
      Supplier<ViewContext> supplier) {
    super(path, method, supplier);
  }

  @Override
  String templateName(ViewContext modelAndView) {
    return IViewContext.Companion.getViewComponentTemplate(modelAndView);
  }

  public HtmxEndpoint(String path, HttpMethod method, Function<T, ViewContext> function, Class<T> tClass) {
    super(path, method, function,tClass);
  }
}
