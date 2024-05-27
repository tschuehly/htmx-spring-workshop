package de.tschuehly.easy.spring.auth.group;

import de.tschuehly.easy.spring.auth.group.management.GroupManagementComponent;
import de.tschuehly.spring.viewcomponent.jte.ViewContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GroupController {
    private final GroupService groupService;
    private final GroupManagementComponent groupManagementComponent; // (1)

    public GroupController(GroupService groupService, GroupManagementComponent groupManagementComponent) {
        this.groupService = groupService;
        this.groupManagementComponent = groupManagementComponent; // (1)
    }

    @GetMapping("/group-management") // (2)
    public ViewContext groupManagementComponent() {
        return groupManagementComponent.render(); // (3)
    }
}
