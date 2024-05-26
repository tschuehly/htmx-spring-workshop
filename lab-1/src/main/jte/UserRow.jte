@import de.tschuehly.easy.spring.auth.user.EasyUser
@import static de.tschuehly.easy.spring.auth.htmx.HtmxUtil.*
@import static de.tschuehly.easy.spring.auth.user.UserController.*

@param EasyUser easyUser
!{var uuid = easyUser.uuid.toString();}
<tr id="user-${uuid}">
    <td>
        ${uuid}
    </td>
    <td>
        ${easyUser.username}
    </td>
    <td>
        ${easyUser.password}
    </td>
    <td>
        <button hx-get="${URI(GET_EDIT_USER_MODAL,uuid)}" <%-- (1) --%>
                hx-target="#${MODAL_CONTAINER_ID}"> <%-- (2) --%>
            <img src="/edit.svg">
        </button>
    </td>
</tr>