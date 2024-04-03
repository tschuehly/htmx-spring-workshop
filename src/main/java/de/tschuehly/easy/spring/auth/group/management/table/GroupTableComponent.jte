@import static de.tschuehly.easy.spring.auth.group.management.table.GroupTableComponent.*
@import de.tschuehly.easy.spring.auth.group.GroupController
@import de.tschuehly.easy.spring.auth.htmx.HtmxUtil
@param de.tschuehly.easy.spring.auth.group.management.table.GroupTableComponent.GroupTableContext groupTableContext
<table id="${GROUP_TABLE_ID}">
    <thead>
    <tr>
        <th>
            username
        </th>
        <th>
            password
        </th>
    </tr>
    </thead>
    <tbody>
    @for(var group: groupTableContext.groupList())
        <tr>
            <td>
                ${group.groupName}
            </td>
            <td>
                @for(var member: group.memberList)
                    <span>${member.username}</span>
                @endfor
            </td>
            <td>
                <button hx-get="${HtmxUtil.URI(GroupController.GET_SELECT_USER,group.groupName)}">
                    <img src="/plus.svg">
                </button>
            </td>
        </tr>
    @endfor
    </tbody>
</table>