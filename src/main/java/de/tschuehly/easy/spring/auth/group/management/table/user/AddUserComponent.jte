@import static de.tschuehly.easy.spring.auth.group.GroupController.*
@import de.tschuehly.easy.spring.auth.group.management.table.GroupTableComponent
@import de.tschuehly.easy.spring.auth.htmx.HtmxUtil
@param de.tschuehly.easy.spring.auth.group.management.table.user.AddUserComponent.AddUserContext addUserContext
<form hx-post="${POST_ADD_USER}" hx-target="${HtmxUtil.target(GroupTableComponent.GROUP_TABLE_ID)}" hx-swap="outerHTML">
    <input type="hidden" name="groupName" value="${addUserContext.groupName()}">
    <select name="easyUserId">
        @for(var easyUser: addUserContext.easyUserList())
            <option value="${easyUser.uuid.toString()}">${easyUser.username}</option>
        @endfor
    </select>
    <button type="submit">Add User to group</button>
</form>