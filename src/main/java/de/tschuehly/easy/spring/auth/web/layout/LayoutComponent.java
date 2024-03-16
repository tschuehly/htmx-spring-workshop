package de.tschuehly.easy.spring.auth.web.layout;

import de.tschuehly.spring.viewcomponent.core.component.ViewComponent;
import de.tschuehly.spring.viewcomponent.jte.ViewContext;

@ViewComponent
public class LayoutComponent {
  public record LayoutContext(
      ViewContext content
  ) implements ViewContext{}
  public ViewContext render(ViewContext content){
    return new LayoutContext(content);
  }
}
