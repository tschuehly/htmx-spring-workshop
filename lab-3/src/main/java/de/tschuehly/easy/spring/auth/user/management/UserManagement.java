package de.tschuehly.easy.spring.auth.user.management;

import de.tschuehly.easy.spring.auth.user.management.table.UserTableComponent;
import de.tschuehly.spring.viewcomponent.core.component.ViewComponent;
import de.tschuehly.spring.viewcomponent.jte.ViewContext;

@ViewComponent
public class UserManagement {
  private final UserTableComponent userTableComponent;

  public static final String MODAL_CONTAINER_ID = "modalContainer";
  public static final String CLOSE_MODAL_EVENT = "close-modal";

  public UserManagement(UserTableComponent userTableComponent) {
    this.userTableComponent = userTableComponent;
  }

  public record UserManagementContext(ViewContext userTable) implements ViewContext{}

  public ViewContext render(){
    return new UserManagementContext(userTableComponent.render());
  }
}
