package de.tschuehly.easy.spring.auth.group.management;

import de.tschuehly.easy.spring.auth.group.GroupController;
import de.tschuehly.easy.spring.auth.group.management.table.GroupTableComponent;
import de.tschuehly.easy.spring.auth.web.Page;
import de.tschuehly.spring.viewcomponent.core.component.ViewComponent;
import de.tschuehly.spring.viewcomponent.jte.ViewContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class GroupManagementComponent implements Page {
    @Override
    public NavigationItem navigationItem() {
        return new NavigationItem("Group Management", GroupController.GROUP_MANAGEMENT);
    }
}