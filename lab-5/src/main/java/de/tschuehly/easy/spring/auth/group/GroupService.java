package de.tschuehly.easy.spring.auth.group;

import de.tschuehly.easy.spring.auth.user.EasyUser;
import de.tschuehly.easy.spring.auth.user.UserService;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class GroupService {
  private final UserService userService;
  public final static List<EasyGroup> easyGroupList = new ArrayList<>();

  public GroupService(UserService userService) {
    this.userService = userService;
  }

  public List<EasyGroup> getAll(){
    return easyGroupList;
  }

  public List<EasyUser> addUserToGroup(String groupName, UUID userId){
    EasyGroup easyGroup = easyGroupList.stream().filter(it -> it.groupName.equals(groupName)).findFirst()
        .orElseThrow(
            NoSuchElementException::new);
    EasyUser user = userService.findById(userId);
    easyGroup.memberList.add(user);
    return easyGroup.memberList;
  }

  public void createGroup(String groupName) {
    easyGroupList.add(
        new EasyGroup(groupName)
    );
  }
}
