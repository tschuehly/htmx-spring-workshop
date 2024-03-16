package de.tschuehly.easy.spring.auth.web.manager.user.table;

import de.tschuehly.spring.viewcomponent.core.component.ViewComponent;
import de.tschuehly.spring.viewcomponent.jte.ViewContext;

@ViewComponent
public class UserTableComponent {
  public ViewContext render(){
    return new UserTableContext();
  }

  private record UserTableContext() implements ViewContext {

  }
}
