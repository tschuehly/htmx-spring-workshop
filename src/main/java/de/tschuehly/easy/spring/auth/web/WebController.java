package de.tschuehly.easy.spring.auth.web;

import de.tschuehly.easy.spring.auth.web.layout.LayoutComponent;
import de.tschuehly.easy.spring.auth.web.manager.user.table.UserTableComponent;
import de.tschuehly.spring.viewcomponent.jte.ViewContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {
  private final LayoutComponent layoutComponent;
  private final UserTableComponent userTableComponent;
  public WebController(LayoutComponent layoutComponent, UserTableComponent userTableComponent) {
    this.layoutComponent = layoutComponent;
    this.userTableComponent = userTableComponent;
  }

  @GetMapping("/")
  public ViewContext index(){
    return layoutComponent.render(
        userTableComponent.render()
    );
  }
}
