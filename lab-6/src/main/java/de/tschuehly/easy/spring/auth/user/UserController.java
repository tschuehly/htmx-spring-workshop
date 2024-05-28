package de.tschuehly.easy.spring.auth.user;

import de.tschuehly.easy.spring.auth.user.management.UserManagementComponent;
import de.tschuehly.easy.spring.auth.user.management.create.CreateUserComponent;
import de.tschuehly.easy.spring.auth.user.management.edit.EditUserComponent;
import de.tschuehly.easy.spring.auth.user.management.table.UserTableComponent;
import de.tschuehly.easy.spring.auth.user.management.table.row.UserRowComponent;
import de.tschuehly.easy.spring.auth.web.layout.LayoutComponent;
import de.tschuehly.spring.viewcomponent.jte.ViewContext;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import java.util.UUID;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {

  private final UserService userService;
  private final EditUserComponent editUserComponent;
  private final UserRowComponent userRowComponent;
  private final CreateUserComponent createUserComponent;
  private final LayoutComponent layoutComponent;
  private final UserTableComponent userTableComponent;
  private final UserManagementComponent userManagementComponent;

  public UserController(UserService userService, EditUserComponent editUserComponent,
      UserRowComponent userRowComponent, CreateUserComponent createUserComponent, LayoutComponent layoutComponent,
      UserTableComponent userTableComponent, UserManagementComponent userManagementComponent) {
    this.userService = userService;
    this.editUserComponent = editUserComponent;
    this.userRowComponent = userRowComponent;
    this.createUserComponent = createUserComponent;
    this.layoutComponent = layoutComponent;
    this.userTableComponent = userTableComponent;
    this.userManagementComponent = userManagementComponent;
  }

  public static final String USER_MANAGEMENT_PATH = "/";

  @GetMapping(USER_MANAGEMENT_PATH)
  public ViewContext userManagementComponent() {
    return layoutComponent.render(
        userManagementComponent.render()
    );
  }

  public static final String GET_USER_TABLE = "/user-table";

  @HxRequest
  @GetMapping(GET_USER_TABLE)
  public ViewContext userTable() {
    return userTableComponent.render();
  }

  public static final String GET_EDIT_USER_MODAL = "/save-user/modal/{uuid}";

  @GetMapping(GET_EDIT_USER_MODAL)
  public ViewContext editUserModal(@PathVariable UUID uuid) {
    return editUserComponent.render(uuid);
  }

  public static final String POST_SAVE_USER = "/save-user";

  @PostMapping(POST_SAVE_USER)
  public ViewContext saveUser(UUID uuid, String username, String password) {
    EasyUser user = userService.saveUser(uuid, username, password);
    return userRowComponent.rerender(user);
  }

  public static final String GET_CREATE_USER_MODAL = "/create-user/modal";

  @GetMapping(GET_CREATE_USER_MODAL)
  public ViewContext createUserModal() {
    return createUserComponent.render();
  }


  public static final String POST_CREATE_USER = "/create-user";

  @PostMapping(POST_CREATE_USER)
  public ViewContext createUser(String username, String password) {
    EasyUser user = userService.createUser(username, password);
    return userRowComponent.renderNewRow(user);
  }

  public static final String GET_SEARCH_USER = "/search-user";
  public static final String SEARCH_PARAM = "searchQuery";

  @HxRequest
  @GetMapping(GET_SEARCH_USER)
  public ViewContext searchUser(
          @RequestParam(SEARCH_PARAM) String searchQuery // (1)
  ) {
    return userTableComponent.renderSearch(searchQuery); // (2)
  }
}
