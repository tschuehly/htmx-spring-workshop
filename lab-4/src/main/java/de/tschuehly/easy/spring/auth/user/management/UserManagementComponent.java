package de.tschuehly.easy.spring.auth.user.management;

import de.tschuehly.easy.spring.auth.user.UserController;
import de.tschuehly.easy.spring.auth.web.Page;
import org.springframework.stereotype.Component;

@Component
public class UserManagementComponent implements Page {

    @Override
    public NavigationItem navigationItem() {
        return new NavigationItem("User Management", UserController.USER_MANAGEMENT_PATH);
    }
}