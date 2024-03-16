package de.tschuehly.easy.spring.auth.web;

import de.tschuehly.easy.spring.auth.user.EasyUser;
import de.tschuehly.easy.spring.auth.user.UserService;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

  public static final String MODAL_CONTAINER = "modalContainer";
  public static final String CLOSE_MODAL_EVENT = "close-modal";
  public WebController(UserService userService) {
    this.userService = userService;
  }

  public record UserManagementModel(
      List<EasyUser> easyUserList
  ) {

  }

  private final UserService userService;

  @GetMapping("/")
  public String index(Model model) {
    List<EasyUser> easyUserList = userService.findAll();
    model.addAttribute("userManagementModel", new UserManagementModel(easyUserList));
    return "UserManagement";
  }
}
