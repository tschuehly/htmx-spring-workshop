package de.tschuehly.easy.spring.auth.group;

import de.tschuehly.easy.spring.auth.group.management.GroupManagementComponent;
import de.tschuehly.easy.spring.auth.group.management.table.GroupTableComponent;
import de.tschuehly.easy.spring.auth.group.management.table.user.AddUserComponent;
import de.tschuehly.spring.viewcomponent.jte.ViewContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Controller
public class GroupController {
    private final GroupService groupService;
    private final GroupManagementComponent groupManagementComponent; // (1)
    private final GroupTableComponent groupTableComponent;
    private final AddUserComponent addUserComponent;
    public GroupController(GroupService groupService, GroupManagementComponent groupManagementComponent, GroupTableComponent groupTableComponent, AddUserComponent addUserComponent) {
        this.groupService = groupService;
        this.groupManagementComponent = groupManagementComponent; // (1)
        this.groupTableComponent = groupTableComponent;
        this.addUserComponent = addUserComponent;
    }

    @GetMapping("/group-management") // (2)
    public ViewContext groupManagementComponent() {
        return groupManagementComponent.render(); // (3)
    }

    public final static String POST_ADD_USER = "/group/{groupName}/add-user";
    public final static String USER_ID_PARAM = "userId";
    @PostMapping(POST_ADD_USER)
    public ViewContext addUser(@PathVariable String groupName,
                               @RequestParam(USER_ID_PARAM) UUID userId){
        groupService.addUserToGroup(groupName,userId);
        return groupTableComponent.render();
    }

    public final static String GET_SELECT_USER = "/group/{groupName}/select-user";

    @GetMapping(GET_SELECT_USER)
    public ViewContext selectUser(@PathVariable String groupName) {
        return addUserComponent.render(groupName);
    }
}
