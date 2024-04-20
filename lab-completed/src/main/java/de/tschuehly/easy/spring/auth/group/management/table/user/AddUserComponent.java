package de.tschuehly.easy.spring.auth.group.management.table.user;

import de.tschuehly.easy.spring.auth.user.EasyUser;
import de.tschuehly.easy.spring.auth.user.UserService;
import de.tschuehly.spring.viewcomponent.core.component.ViewComponent;
import de.tschuehly.spring.viewcomponent.jte.ViewContext;
import java.util.List;

@ViewComponent
public class AddUserComponent {
  private final UserService userService;

  public AddUserComponent(UserService userService) {
    this.userService = userService;
  }

  public record AddUserContext(String groupName, List<EasyUser> easyUserList) implements ViewContext{}

  public ViewContext render(String groupName){
    return new AddUserContext(groupName,userService.findAll());
  }
}
