package de.tschuehly.easy.spring.auth.web.layout;

import de.tschuehly.spring.viewcomponent.core.component.ViewComponent;
import de.tschuehly.spring.viewcomponent.jte.ViewContext;

@ViewComponent
public class LayoutComponent {
  record LayoutContext() implements ViewContext{}
  public ViewContext render(){
    return new LayoutContext();
  }
}
