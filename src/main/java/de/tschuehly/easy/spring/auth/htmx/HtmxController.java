package de.tschuehly.easy.spring.auth.htmx;

import de.tschuehly.spring.viewcomponent.jte.ViewContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class HtmxController {

  public static final HtmxAction createUserEndpoint =
      new HtmxAction("/createUser",
          RequestMethod.POST,
          UserForm.class);
  public record UserForm(
      String userName, String password){
  }

  @HtmxEndpoint(createUserEndpoint)
  public ViewContext render(UserForm userForm){
    return null;
  }
}
