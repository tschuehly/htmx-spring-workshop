package de.tschuehly.easy.spring.auth.htmx;

import de.tschuehly.spring.viewcomponent.core.component.ViewComponent;
import de.tschuehly.spring.viewcomponent.jte.ViewContext;
import jakarta.annotation.Nullable;
import org.springframework.http.HttpMethod;

@ViewComponent
public class HtmxComponent {


  public HtmxEndpoint<?> htmxTestEndpoint = new HtmxEndpoint<>(
      "/test", HttpMethod.GET, this::test);

  private ViewContext test() {
    return new HtmxContext(null, this);
  }


  public record UserForm(
      String userName, String password) {
  }
  public HtmxEndpoint<UserForm> createUserEndpoint = new HtmxEndpoint<>(
      "/createUser",
      HttpMethod.POST,
      this::createUser,
      UserForm.class
  );

  private ViewContext createUser(UserForm userForm) {
    return new HtmxContext(userForm, this);
  }

  public record HtmxContext(@Nullable UserForm userForm, HtmxComponent server) implements ViewContext {

  }
}
