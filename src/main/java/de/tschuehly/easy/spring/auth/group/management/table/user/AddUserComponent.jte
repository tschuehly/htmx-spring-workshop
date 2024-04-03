@import static de.tschuehly.easy.spring.auth.group.GroupController.*
@import de.tschuehly.easy.spring.auth.group.management.table.GroupTableComponent
@import de.tschuehly.easy.spring.auth.htmx.HtmxUtil
@param de.tschuehly.easy.spring.auth.group.management.table.user.AddUserComponent.AddUserContext addUserContext
<form hx-post="${HtmxUtil.URI(POST_ADD_USER,addUserContext.groupName())}" hx-target="${HtmxUtil.target(GroupTableComponent.GROUP_TABLE_ID)}" hx-swap="outerHTML">
    <select name="${USER_ID_PARAM}">
        @for(var easyUser: addUserContext.easyUserList())
            <option value="${easyUser.uuid.toString()}">${easyUser.username}</option>
        @endfor
    </select>
    <button type="submit">Add User to group</button>
</form>