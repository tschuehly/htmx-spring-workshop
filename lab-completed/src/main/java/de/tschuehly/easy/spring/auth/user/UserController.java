package de.tschuehly.easy.spring.auth.user;

import de.tschuehly.easy.spring.auth.user.management.UserManagement;
import de.tschuehly.easy.spring.auth.user.management.create.CreateUserComponent;
import de.tschuehly.easy.spring.auth.user.management.edit.EditUserComponent;
import de.tschuehly.easy.spring.auth.user.management.table.UserTableComponent;
import de.tschuehly.easy.spring.auth.user.management.table.row.UserRowComponent;
import de.tschuehly.easy.spring.auth.web.layout.LayoutComponent;
import de.tschuehly.spring.viewcomponent.jte.ViewContext;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import java.util.UUID;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Controller
public class UserController {

  private final UserService userService;
  private final EditUserComponent editUserComponent;
  private final UserRowComponent userRowComponent;
  private final CreateUserComponent createUserComponent;
  private final LayoutComponent layoutComponent;
  private final UserTableComponent userTableComponent;
  private final UserManagement userManagement;

  public UserController(UserService userService, EditUserComponent editUserComponent,
      UserRowComponent userRowComponent, CreateUserComponent createUserComponent, LayoutComponent layoutComponent,
      UserTableComponent userTableComponent, UserManagement userManagement) {
    this.userService = userService;
    this.editUserComponent = editUserComponent;
    this.userRowComponent = userRowComponent;
    this.createUserComponent = createUserComponent;
    this.layoutComponent = layoutComponent;
    this.userTableComponent = userTableComponent;
    this.userManagement = userManagement;
  }

  public static final String USER_MANAGEMENT_PATH = "/";

  @GetMapping(USER_MANAGEMENT_PATH)
  public ViewContext userManagement() {
    return layoutComponent.render(
        userManagement.render()
    );
  }

  public static final String GET_USER_TABLE = "/user-table/{page}";

  @HxRequest
  @GetMapping(GET_USER_TABLE)
  public ViewContext userTable(@PathVariable String page) {
    return userTableComponent.render(Integer.parseInt(page));
  }

  public static final String GET_SEARCH_USER = "/search-user";
  public static final String SEARCH_PARAM = "searchQuery";

  @HxRequest
  @GetMapping(GET_SEARCH_USER)
  public ViewContext searchUser(@RequestParam(SEARCH_PARAM) String searchQuery) {
    return userTableComponent.renderSearch(searchQuery);
  }

  public static final String GET_SUBSCRIBE_USER = "/subscribe-new-user";

  @GetMapping(value = GET_SUBSCRIBE_USER, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public SseEmitter subscribeUser() {
    SseEmitter emitter = new SseEmitter();
    userTableComponent.subscribeToNewUserRow().subscribe(
        row -> {
          try {
            emitter.send(row);
          } catch (Exception e) {
            emitter.completeWithError(e);
          }
        }, emitter::completeWithError, emitter::complete
    );
    return emitter;
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


}
