package de.tschuehly.easy.spring.auth.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@Controller
public class UserController {
  public static final String MODAL_CONTAINER_ID = "modalContainer"; // (1)
  public static final String USER_TABLE_BODY_ID = "userTableBody"; // (1)

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/") // (2)
  public String index(Model model) {
    model.addAttribute("easyUserList", userService.findAll()); // (3)
    return "UserManagement";
  }

  public record UserForm(String uuid, String username, String password) {} // (1)

  public static final String GET_EDIT_USER_MODAL = "/save-user/modal/{uuid}"; // (2)

  @GetMapping(GET_EDIT_USER_MODAL)
  public String editUserModal(Model model, @PathVariable UUID uuid) {
    var user = userService.findById(uuid);
    model.addAttribute("userForm",
            new UserForm(user.uuid.toString(), user.username, user.password)); // (3)
    return "EditUserForm";
  }

}