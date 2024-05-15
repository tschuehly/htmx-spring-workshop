package de.tschuehly.easy.spring.auth.user.management.table;

import de.tschuehly.easy.spring.auth.user.UserService;
import de.tschuehly.easy.spring.auth.user.management.table.row.UserRowComponent;
import de.tschuehly.spring.viewcomponent.core.component.ViewComponent;
import de.tschuehly.spring.viewcomponent.jte.ViewContext;
import java.util.List;

@ViewComponent
public class UserTableComponent {

  private final UserService userService;
  private final UserRowComponent userRowComponent;

  public UserTableComponent(UserService userService, UserRowComponent userRowComponent) {
    this.userService = userService;
    this.userRowComponent = userRowComponent;
  }

  public record UserTableContext(List<ViewContext> userTableRowList) implements ViewContext {

  }

  public static final String USER_TABLE_BODY_ID = "userTableBody";

  public ViewContext render() {
    List<ViewContext> rowList = userService.findAll()
        .stream().map(userRowComponent::render).toList();
    return new UserTableContext(rowList);
  }
}
