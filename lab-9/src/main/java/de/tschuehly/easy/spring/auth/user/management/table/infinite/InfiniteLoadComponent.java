package de.tschuehly.easy.spring.auth.user.management.table.infinite;

import de.tschuehly.spring.viewcomponent.core.component.ViewComponent;
import de.tschuehly.spring.viewcomponent.jte.ViewContext;

@ViewComponent
public class InfiniteLoadComponent {

  public ViewContext render(int nextPage){
    return new InfiniteLoadContext(nextPage);
  }

  public record InfiniteLoadContext(int nextPage) implements ViewContext {

  }
}
