package de.tschuehly.easy.spring.auth.group;

import de.tschuehly.easy.spring.auth.group.management.GroupManagementComponent;
import de.tschuehly.easy.spring.auth.group.management.table.GroupTableComponent;
import de.tschuehly.easy.spring.auth.group.management.table.user.AddUserComponent;
import de.tschuehly.spring.viewcomponent.jte.ViewContext;
import java.util.UUID;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class GroupController {
  private final GroupManagementComponent groupManagementComponent;
  private final AddUserComponent addUserComponent;
  private final GroupTableComponent groupTableComponent;
  private final GroupService groupService;

  public GroupController(GroupManagementComponent groupManagementComponent, AddUserComponent addUserComponent,
      GroupTableComponent groupTableComponent, GroupService groupService) {
    this.groupManagementComponent = groupManagementComponent;
    this.addUserComponent = addUserComponent;
    this.groupTableComponent = groupTableComponent;
    this.groupService = groupService;
  }

  @GetMapping("/group-management")
  public ViewContext groupManagementComponent(){
    return groupManagementComponent.render();
  }

  public final static String GET_SELECT_USER =  "/group/{groupName}/select-user";
  @GetMapping(GET_SELECT_USER)
  public ViewContext selectUser(@PathVariable String groupName){
    return addUserComponent.render(groupName);
  }

  public final static String POST_ADD_USER = "/group/{groupName}/add-user";
  public final static String USER_ID_PARAM = "userId";
  @PostMapping(POST_ADD_USER)
  public ViewContext addUser(@PathVariable String groupName, @RequestParam(USER_ID_PARAM) UUID userId){
    groupService.addUserToGroup(groupName,userId);
    return groupTableComponent.render();
  }
}
