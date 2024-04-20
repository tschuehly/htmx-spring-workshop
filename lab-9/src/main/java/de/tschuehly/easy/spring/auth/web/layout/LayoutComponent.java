package de.tschuehly.easy.spring.auth.web.layout;

import de.tschuehly.easy.spring.auth.web.Page;
import de.tschuehly.easy.spring.auth.web.Page.NavigationItem;
import de.tschuehly.spring.viewcomponent.core.component.ViewComponent;
import de.tschuehly.spring.viewcomponent.jte.ViewContext;
import java.util.List;

@ViewComponent
public class LayoutComponent {

  public static final String MODAL_CONTAINER_ID = "modalContainer";
  public static final String CLOSE_MODAL_EVENT = "close-modal";
  public static final String TOAST_CONTAINER_ID = "toastContainer";

  private final List<Page> pageList;

  public LayoutComponent(List<Page> pageList) {
    this.pageList = pageList;
  }

  public record LayoutContext(ViewContext content, List<NavigationItem> navigationItemList) implements ViewContext {

  }

  public ViewContext render(ViewContext content) {
    List<NavigationItem> navigationItemList = pageList.stream().map(Page::navigationItem).toList();
    return new LayoutContext(content, navigationItemList);
  }
}
