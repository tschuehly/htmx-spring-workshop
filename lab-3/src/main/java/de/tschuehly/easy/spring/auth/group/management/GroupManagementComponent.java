package de.tschuehly.easy.spring.auth.group.management;

import de.tschuehly.easy.spring.auth.group.management.table.GroupTableComponent;
import de.tschuehly.spring.viewcomponent.core.component.ViewComponent;
import de.tschuehly.spring.viewcomponent.jte.ViewContext;
@ViewComponent
public class GroupManagementComponent {
  public static final String MODAL_CONTAINER_ID = "modalContainer";
  public static final String CLOSE_MODAL_EVENT = "close-modal";

  private final GroupTableComponent groupTableComponent;

    public GroupManagementComponent(GroupTableComponent groupTableComponent) {
        this.groupTableComponent = groupTableComponent;
    }

    public record GroupManagementContext(ViewContext viewContext) implements ViewContext{}

  public ViewContext render(){
    return new GroupManagementContext(groupTableComponent.render());
  }
}