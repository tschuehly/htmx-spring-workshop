package de.tschuehly.easy.spring.auth.group;

import org.springframework.stereotype.Controller;

@Controller
public class GroupController {
  private final GroupService groupService;

  public GroupController(GroupService groupService) {
    this.groupService = groupService;
  }
}
