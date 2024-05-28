@import de.tschuehly.easy.spring.auth.user.management.table.UserTableComponent.UserTableContext
@import static de.tschuehly.easy.spring.auth.user.UserController.GET_CREATE_USER_MODAL
@import static de.tschuehly.easy.spring.auth.user.management.table.UserTableComponent.USER_TABLE_BODY_ID
@import static de.tschuehly.easy.spring.auth.web.layout.LayoutComponent.MODAL_CONTAINER_ID
@import static de.tschuehly.easy.spring.auth.user.UserController.*
@import de.tschuehly.easy.spring.auth.htmx.HtmxUtil
@param UserTableContext userTableContext
<table>
    <thead>
    <tr>
        <th colspan="4">
            <label>
                Search Users
                <input type="text"
                       name="${SEARCH_PARAM}" <%-- (1) --%>
                       hx-get="${GET_SEARCH_USER}" <%-- (2) --%>
                       hx-trigger="input changed delay:500ms, search" <%-- (3) --%>
                       hx-target="${HtmxUtil.idSelector(USER_TABLE_BODY_ID)}"> <%-- (4) --%>
            </label>
        </th>
    </tr>
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
    <tbody id="${USER_TABLE_BODY_ID}">
    @for(var row: userTableContext.userTableRowList())
        ${row}
    @endfor
    </tbody>
    <tfoot>
    <tr>
        <td colspan="4">
            <button hx-get="${GET_CREATE_USER_MODAL}" hx-target="#${MODAL_CONTAINER_ID}">
                Create new User
            </button>
        </td>
    </tr>
    </tfoot>
</table>