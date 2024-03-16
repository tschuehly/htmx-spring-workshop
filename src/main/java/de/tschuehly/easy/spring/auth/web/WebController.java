package de.tschuehly.easy.spring.auth.web;

import de.tschuehly.easy.spring.auth.web.layout.LayoutComponent;
import de.tschuehly.spring.viewcomponent.jte.ViewContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {
  private final LayoutComponent layoutComponent;

  public WebController(LayoutComponent layoutComponent) {
    this.layoutComponent = layoutComponent;
  }

  @GetMapping("/")
  public ViewContext index(){
    return layoutComponent.render();
  }
}
