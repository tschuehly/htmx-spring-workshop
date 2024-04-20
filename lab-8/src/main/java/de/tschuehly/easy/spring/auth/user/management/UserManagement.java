package de.tschuehly.easy.spring.auth.user.management;

import de.tschuehly.easy.spring.auth.user.UserController;
import de.tschuehly.easy.spring.auth.web.Page;
import de.tschuehly.spring.viewcomponent.core.component.ViewComponent;
import de.tschuehly.spring.viewcomponent.jte.ViewContext;
import org.springframework.core.annotation.Order;

@ViewComponent
@Order(1)
public class UserManagement implements Page {

  public ViewContext render() {
    return new UserManagementContext();
  }

  public record UserManagementContext() implements ViewContext {

  }

  @Override
  public NavigationItem navigationItem() {
    return new NavigationItem("User Management", UserController.USER_MANAGEMENT_PATH);
  }

}
