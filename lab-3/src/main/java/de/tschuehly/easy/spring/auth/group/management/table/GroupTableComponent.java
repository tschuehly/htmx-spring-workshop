package de.tschuehly.easy.spring.auth.group.management.table;

import de.tschuehly.easy.spring.auth.group.EasyGroup;
import de.tschuehly.easy.spring.auth.group.GroupService;
import de.tschuehly.spring.viewcomponent.core.component.ViewComponent;
import de.tschuehly.spring.viewcomponent.jte.ViewContext;

import java.util.List;

@ViewComponent
public class GroupTableComponent {

  private final GroupService groupService;

  public final static String GROUP_TABLE_ID = "groupTable";

  public record GroupTableContext(List<EasyGroup> groupList) implements ViewContext { }

  public ViewContext render() {
    List<EasyGroup> groupList = groupService.getAll();
    return new GroupTableContext(groupList);
  }

  public GroupTableComponent(GroupService groupService) {
    this.groupService = groupService;
  }
}