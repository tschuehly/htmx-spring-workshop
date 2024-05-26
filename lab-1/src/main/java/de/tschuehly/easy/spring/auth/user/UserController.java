package de.tschuehly.easy.spring.auth.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
}