@import de.tschuehly.easy.spring.auth.user.EasyUser
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
</tr>