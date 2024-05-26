@import static de.tschuehly.easy.spring.auth.user.UserController.*
@import de.tschuehly.easy.spring.auth.user.EasyUser
@import java.util.List
@param List<EasyUser> easyUserList
<html lang="en">

<head>
    <title>Easy Spring Auth</title>
    <link rel="stylesheet" href="/css/sakura.css" type="text/css">
    <script src="/htmx_1.9.11.js"></script>
    <script src="/htmx_debug.js"></script>
    <script src="http://localhost:35729/livereload.js"></script>
</head>
<body hx-ext="debug">
<nav>
    <h1>
        Easy Spring Auth
    </h1>
</nav>
<main>
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
        <tbody id="${USER_TABLE_BODY_ID}"> <%-- (1) --%>
        @for(var user: easyUserList) <%-- (2) --%>
            @template.UserRow(easyUser = user) <%-- (3) --%>
        @endfor
        </tbody>
        <tfoot>
        </tfoot>
    </table>
</main>
</body>
<div id="${MODAL_CONTAINER_ID}"></div>
</html>