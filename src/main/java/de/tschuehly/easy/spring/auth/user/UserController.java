package de.tschuehly.easy.spring.auth.user;

import jakarta.servlet.http.HttpServletResponse;
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

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
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
    EasyUser user = null;
    if (uuid.isEmpty()) {
      user = userService.createUser(
          username,
          password
      );
    } else {
      user = userService.saveUser(
          uuid.get(),
          username,
          password
      );
      response.addHeader("HX-Retarget", "#" + user.uuid);
      response.addHeader("HX-Reswap", "outerHTML");
    }
    response.addHeader("HX-Trigger","closeModal");
    model.addAttribute("easyUser", user);
    return "userRow";
  }

}
