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



public static final String USER_TABLE_BODY_ID = "userTableBody";

public ViewContext render(int currentPage) {
  List<ViewContext> userRowList = userService.getPage(currentPage, 20) // (1)
      .stream().map(userRowComponent::render).toList(); // (2)
  ViewContext tableBody = listComponent.render(
      userRowList, // (3)
      infiniteLoadComponent.render(currentPage + 1) // (4)
  );
  if(currentPage == 0){
    return new UserTableContext(tableBody); // (5)
  }
  return tableBody; // (6)
}

public record UserTableContext(ViewContext userTableBody) // (8)
    implements ViewContext {}

  public ViewContext renderSearch(String searchQuery) {
    List<ViewContext> userRowList = userService.searchUser(searchQuery)
        .stream().map(userRowComponent::render).toList();
    return listComponent.render(userRowList);

  }
}
