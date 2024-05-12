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
@for(var group: groupTableContext.groupList())
    <tr>
        <td>
            ${group.groupName}
        </td>
        <td>
            @for(var member: group.memberList)
                <span>${member.username}</span>
            @else
                <span>no member</span>
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