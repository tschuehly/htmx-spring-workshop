package de.tschuehly.easy.spring.auth.user.management.table;

import de.tschuehly.easy.spring.auth.user.UserService;
import de.tschuehly.easy.spring.auth.user.management.table.infinite.InfiniteLoadComponent;
import de.tschuehly.easy.spring.auth.user.management.table.row.UserRowComponent;
import de.tschuehly.easy.spring.auth.web.list.ListComponent;
import de.tschuehly.spring.viewcomponent.core.component.ViewComponent;
import de.tschuehly.spring.viewcomponent.jte.ViewContext;
import java.util.List;

@ViewComponent
public class UserTableComponent {

  private final UserService userService;
  private final UserRowComponent userRowComponent;
  private final ListComponent listComponent;
  private final InfiniteLoadComponent infiniteLoadComponent;

  public UserTableComponent(UserService userService, UserRowComponent userRowComponent, ListComponent listComponent,
      InfiniteLoadComponent infiniteLoadComponent) {
    this.userService = userService;
    this.userRowComponent = userRowComponent;
    this.listComponent = listComponent;
    this.infiniteLoadComponent = infiniteLoadComponent;
  }

  public record UserTableContext(ViewContext userTableBody) implements ViewContext {

  }

  public static final String USER_TABLE_BODY_ID = "userTableBody";

  public ViewContext render(String page) {
    int currentPage = Integer.parseInt(page);
    List<ViewContext> userRowList = userService.getPage(currentPage, 20)
        .stream().map(userRowComponent::render).toList();
    ViewContext tableBody = listComponent.render(
        userRowList,
        infiniteLoadComponent.render(currentPage + 1)
    );
    if(currentPage == 0){
      return new UserTableContext(tableBody);
    }
    return tableBody;
  }


  public ViewContext renderSearch(String searchQuery) {
    List<ViewContext> userRowList = userService.searchUser(searchQuery)
        .stream().map(userRowComponent::render).toList();
    return listComponent.render(userRowList);

  }
}
