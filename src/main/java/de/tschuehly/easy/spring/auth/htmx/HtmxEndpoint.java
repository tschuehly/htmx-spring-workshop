package de.tschuehly.easy.spring.auth.htmx;

import de.tschuehly.spring.viewcomponent.core.IViewContext;
import de.tschuehly.spring.viewcomponent.jte.ViewContext;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.RenderingResponse;
import org.springframework.web.servlet.function.RequestPredicate;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

public class HtmxEndpoint<T> implements RouterFunction<ServerResponse> {

  private final String path;
  private final HttpMethod method;
  private final Supplier<ViewContext> viewContextSupplier;
  private final Function<T, ViewContext> function;

  ParameterizedTypeReference<T> requestType = new ParameterizedTypeReference<>() {
  };

  public HtmxEndpoint(String path, HttpMethod method, Function<T, ViewContext> function) {
    this.path = path;
    this.method = method;
    this.function = function;
    this.viewContextSupplier = null;
  }

  public HtmxEndpoint(String path, HttpMethod method, Supplier<ViewContext> viewContextSupplier) {
    this.path = path;
    this.method = method;
    this.viewContextSupplier = viewContextSupplier;
    this.function = null;
    this.requestType = null;
  }

  @NotNull
  @Override
  public Optional<HandlerFunction<ServerResponse>> route(@NotNull ServerRequest request) {
    RequestPredicate predicate = RequestPredicates.method(method).and(RequestPredicates.path(path));
    if (predicate.test(request)) {
      ViewContext viewContext = getBody(request);
      return Optional.of(
          req -> RenderingResponse.create(IViewContext.Companion.getViewComponentTemplate(viewContext))
              .modelAttribute(viewContext)
              .build()
      );
    }
    return Optional.empty();
  }

  private ViewContext getBody(ServerRequest req) {
    if (function == null) {
      return viewContextSupplier.get();
    }

    try {
      return function.apply(
          req.body(requestType)
      );
    } catch (ServletException | IOException e) {
      throw new RuntimeException(e);
    }
  }

  public String call() {
    return "hx-" + method.name().toLowerCase() + " =\"" + path + "\"";
  }

}
