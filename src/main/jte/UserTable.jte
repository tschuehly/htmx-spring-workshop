@import java.util.List
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
    <tbody>
    @for(var user: easyUserList)
        @template.userRow(easyUser = user)
    @endfor
    </tbody>
</table>