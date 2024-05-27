@import static de.tschuehly.easy.spring.auth.group.management.table.GroupTableComponent.*
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
        </tr>
    @endfor
    </tbody>
</table>