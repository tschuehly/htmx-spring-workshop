package de.tschuehly.easy.spring.auth.controller;

import de.tschuehly.easy.spring.auth.domain.EasyUser;
import de.tschuehly.easy.spring.auth.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {

  public static final String SAVE_USER = "/user";
  public static final String EDIT_USER_MODAL = "/user/edit";
  public static final String EDIT_USER_PARAMETER = "uuid";
  public static final String USER_TABLE_BODY_ID = "userTableBody";

  public static final String MODAL_CONTAINER_ID = "modalContainer";
  public static final String CLOSE_MODAL_EVENT = "close-modal";
  public static final String HTMX = "hx-get=/helloWorld hx-swap=outerHTML";
  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }


  public record UserManagementModel(
      List<EasyUser> easyUserList
  ) {

  }

  @GetMapping("/")
  public String userManagement(Model model) {
    List<EasyUser> easyUserList = userService.findAll();
    model.addAttribute("userManagementModel", new UserManagementModel(easyUserList));
    return "UserManagement";
  }


  public record UserForm(String uuid, String username, String password) {

  }

  @GetMapping(EDIT_USER_MODAL)
  public String editUserModal(@RequestParam(EDIT_USER_PARAMETER) Optional<UUID> userUuid, Model model) {
    if (userUuid.isPresent()) {
      var user = userService.findById(userUuid.get());
      model.addAttribute("userForm", new UserForm(
          user.uuid.toString(), user.username, user.password
      ));
    } else {
      model.addAttribute("userForm", new UserForm(null, null, null));
    }
    return "EditUserForm";
  }

  @PostMapping(SAVE_USER)
  public String saveUser(Optional<UUID> uuid, String username, String password, Model model,
      HttpServletResponse response) {
    EasyUser user;
    if (uuid.isEmpty()) {
      user = userService.createUser(
          username,
          password
      );
      response.addHeader("HX-Retarget", "#" + USER_TABLE_BODY_ID);
      response.addHeader("HX-Reswap", "afterbegin");
    } else {
      user = userService.saveUser(
          uuid.get(),
          username,
          password
      );
      response.addHeader("HX-Retarget", "#user-" + user.uuid);
      response.addHeader("HX-Reswap", "outerHTML");
    }
    response.addHeader("HX-Trigger", CLOSE_MODAL_EVENT);
    model.addAttribute("easyUser", user);
    return "userRow";
  }

}
