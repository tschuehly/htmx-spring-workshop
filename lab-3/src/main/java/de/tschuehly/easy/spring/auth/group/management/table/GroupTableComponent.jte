@import static de.tschuehly.easy.spring.auth.group.management.table.GroupTableComponent.*
@import static de.tschuehly.easy.spring.auth.group.GroupController.GET_SELECT_USER
@import de.tschuehly.easy.spring.auth.htmx.HtmxUtil
@param de.tschuehly.easy.spring.auth.group.management.table.GroupTableComponent.GroupTableContext groupTableContext
<table id="${GROUP_TABLE_ID}">
    <thead>
    <tr>
        <th>
            Group Name
        </th>
        <th>
            Group Members
        </th>
        <th>

        </th>
    </tr>
    </thead>
    <tbody>
    @for(var group: groupTableContext.groupList()) <%-- (1) --%>
        <tr>
            <td>
                ${group.groupName} <%-- (2) --%>
            </td>
            <td>
                @for(var member: group.memberList) <%-- (3) --%>
                    <span>${member.username}</span> <%-- (4) --%>
                @else
                    <span>no member</span> <%-- (5) --%>
                @endfor
            </td>
            <td>
                <button hx-get="${HtmxUtil.URI(GET_SELECT_USER,group.groupName)}"
                        hx-swap="outerHTML">
                    <img src="/plus.svg">
                </button>
            </td>
        </tr>
    @endfor
    </tbody>
</table>