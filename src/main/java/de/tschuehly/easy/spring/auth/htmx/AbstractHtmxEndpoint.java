package de.tschuehly.easy.spring.auth.htmx;

import jakarta.servlet.ServletException;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.List;
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

public abstract class AbstractHtmxEndpoint<T,R> implements RouterFunction<ServerResponse> {

  public final String path;
  public final HttpMethod method;
  private final Supplier<R> supplier;
  private final Function<T, R> function;
  private final Class<T> tClass;
  // TODO: How do I get this type?????
  ParameterizedTypeReference<T> typeRef = new ParameterizedTypeReference<>() {
  };

  public AbstractHtmxEndpoint(String path, HttpMethod method, Function<T, R> function, Class<T> tClass) {

    this.path = path;
    this.method = method;
    this.function = function;
    this.tClass = tClass;
    this.supplier = null;

  }

  public AbstractHtmxEndpoint(String path, HttpMethod method, Supplier<R> supplier) {
    this.path = path;
    this.method = method;
    this.supplier = supplier;
    this.function = null;
    this.tClass = null;
  }

  @NotNull
  @Override
  public Optional<HandlerFunction<ServerResponse>> route(@NotNull ServerRequest request) {
    RequestPredicate predicate = RequestPredicates.method(method).and(RequestPredicates.path(path));
    if (predicate.test(request)) {
      R model = getBody(request);
      return Optional.of(
          req -> RenderingResponse.create(templateName(model))
              .modelAttribute(model)
              .build()
      );
    }
    return Optional.empty();
  }

  abstract String templateName(R modelAndView);

  private R getBody(ServerRequest req) {
    if (function == null && supplier != null) {
      return supplier.get();
    }
    try {
      T body = req.body(tClass);
      if(function != null) {
        return function.apply(
            body
        );
      }
    } catch (ServletException | IOException e) {
      throw new RuntimeException(e);
    }
    throw new RuntimeException("Failed to get body");
  }

  public String call() {
    return "hx-" + method.name().toLowerCase() + " =\"" + path + "\"";
  }

}
