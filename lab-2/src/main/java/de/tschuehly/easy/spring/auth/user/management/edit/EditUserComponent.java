package de.tschuehly.easy.spring.auth.user.management.edit;

import de.tschuehly.easy.spring.auth.user.EasyUser;
import de.tschuehly.easy.spring.auth.user.UserService;
import de.tschuehly.spring.viewcomponent.core.component.ViewComponent;
import de.tschuehly.spring.viewcomponent.jte.ViewContext;
import java.util.UUID;

@ViewComponent
public class EditUserComponent {

  private final UserService userService;

  public EditUserComponent(UserService userService) {
    this.userService = userService;
  }

  public record EditUserContext(UUID uuid, String username, String password) implements ViewContext {

  }

  public ViewContext render(UUID uuid) {
    EasyUser user = userService.findById(uuid);
    return new EditUserContext(user.uuid,user.username,user.password);
  }
}
