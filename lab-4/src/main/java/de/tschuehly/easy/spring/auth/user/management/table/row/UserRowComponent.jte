@import static de.tschuehly.easy.spring.auth.htmx.HtmxUtil.URI
@import static de.tschuehly.easy.spring.auth.user.UserController.*
@import static de.tschuehly.easy.spring.auth.user.management.UserManagementComponent.MODAL_CONTAINER_ID
@import de.tschuehly.easy.spring.auth.user.management.table.row.UserRowComponent.UserRowContext
@param de.tschuehly.easy.spring.auth.user.management.table.row.UserRowComponent.UserRowContext userRowContext

!{var uuid = userRowContext.easyUser().uuid;}
<tr id="${UserRowContext.htmlUserId(uuid)}">
    <td>
        ${uuid.toString()}
    </td>
    <td>
        ${userRowContext.easyUser().username}
    </td>
    <td>
        ${userRowContext.easyUser().password}
    </td>
    <td>
        <button hx-get="${URI(GET_EDIT_USER_MODAL,uuid)}"
                hx-target="#${MODAL_CONTAINER_ID}">
            <img src="/edit.svg">
        </button>
    </td>
</tr>