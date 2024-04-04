package de.tschuehly.easy.spring.auth.user.management.table.row;


import static de.tschuehly.easy.spring.auth.web.layout.LayoutComponent.CLOSE_MODAL_EVENT;

import de.tschuehly.easy.spring.auth.htmx.HtmxUtil;
import de.tschuehly.easy.spring.auth.user.EasyUser;
import de.tschuehly.easy.spring.auth.user.management.UserManagement;
import de.tschuehly.easy.spring.auth.user.management.table.UserTableComponent;
import de.tschuehly.spring.viewcomponent.core.component.ViewComponent;
import de.tschuehly.spring.viewcomponent.jte.ViewContext;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxSwapType;
import java.util.UUID;

@ViewComponent
public class UserRowComponent {

  public record UserRowContext(EasyUser easyUser) implements ViewContext {

    public static String htmlUserId(UUID uuid) {
      return "user-" + uuid;
    }
  }

  public ViewContext render(EasyUser easyUser) {
    return new UserRowContext(easyUser);
  }

  public ViewContext rerender(EasyUser easyUser) {
    String target = HtmxUtil.target(UserRowContext.htmlUserId(easyUser.uuid));
    HtmxUtil.retarget(target);
    HtmxUtil.reswap(HxSwapType.OUTER_HTML);
    HtmxUtil.trigger(CLOSE_MODAL_EVENT);
    return new UserRowContext(easyUser);
  }

  public ViewContext renderNewRow(EasyUser user) {
    String target = HtmxUtil.target(UserTableComponent.USER_TABLE_BODY_ID);
    HtmxUtil.retarget(target);
    HtmxUtil.reswap(HxSwapType.AFTER_BEGIN);
    HtmxUtil.trigger(CLOSE_MODAL_EVENT);
    return new UserRowContext(user);
  }
}
