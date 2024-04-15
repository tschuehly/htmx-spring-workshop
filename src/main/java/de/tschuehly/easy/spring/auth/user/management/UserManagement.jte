@import static de.tschuehly.easy.spring.auth.user.UserController.*
@import de.tschuehly.easy.spring.auth.htmx.HtmxUtil
@param de.tschuehly.easy.spring.auth.user.management.UserManagement.UserManagementContext userManagementContext


<div hx-get="${HtmxUtil.URI(GET_USER_TABLE,0)}" hx-trigger="load">
    <img alt="Result loading..." class="htmx-indicator" width="50" src="/spinner.svg"/>
</div>

