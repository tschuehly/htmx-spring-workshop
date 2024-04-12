package de.tschuehly.easy.spring.auth.htmx;

import de.tschuehly.spring.viewcomponent.core.component.ViewComponent;
import de.tschuehly.spring.viewcomponent.jte.ViewContext;
import org.springframework.http.HttpMethod;

@ViewComponent
public class HtmxComponent {

  public record UserForm(
      String userName, String password) {

  }

  public HtmxEndpoint<?> htmxTestEndpoint = new HtmxEndpoint<>(
      "/test", HttpMethod.GET, this::test);

  private ViewContext test() {
    return new HtmxContext("world", this);
  }

  public HtmxEndpoint<UserForm> createUserEndpoint = new HtmxEndpoint<>(
      "/createUser",
      HttpMethod.POST,
      this::createUser
  );

  private ViewContext createUser(UserForm userForm) {
    return new HtmxContext("hello", this);
  }

  public record HtmxContext(String test, HtmxComponent server) implements ViewContext {

  }
}
