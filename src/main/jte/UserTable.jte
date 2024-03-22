@import de.tschuehly.easy.spring.auth.user.UserController
@import de.tschuehly.easy.spring.auth.web.WebController
@import java.util.List
@import static de.tschuehly.easy.spring.auth.web.WebController.MODAL_CONTAINER_ID
@import static de.tschuehly.easy.spring.auth.user.UserController.EDIT_USER_MODAL
@import de.tschuehly.easy.spring.auth.user.EasyUser
@param List<EasyUser> easyUserList
<table>
    <thead>
        <tr>
            <th>
                uuid
            </th>
            <th>
                username
            </th>
            <th>
                password
            </th>
        </tr>
    </thead>
    <tbody id="${UserController.USER_TABLE_BODY_ID}">
    @for(var user: easyUserList)
        @template.userRow(easyUser = user)
    @endfor
    </tbody>
    <tfoot>
        <tr>
            <td colspan="4">
                <button hx-get="${EDIT_USER_MODAL}" hx-target="#${MODAL_CONTAINER_ID}">
                    Create new User
                </button>
            </td>
        </tr>
    </tfoot>
</table>
