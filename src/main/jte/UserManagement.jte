@import static de.tschuehly.easy.spring.auth.controller.UserController.*
@param de.tschuehly.easy.spring.auth.controller.UserController.UserManagementModel userManagementModel

<html lang="en">

<head>
    <title>Easy Spring Auth</title>
    <link rel="stylesheet" href="/css/sakura.css" type="text/css">
    <link rel="stylesheet" href="/css/classless.css" type="text/css">
    <script src="/htmx.1.9.11.js"></script>
    <script src="https://unpkg.com/htmx.org@1.9.11/dist/ext/debug.js"></script>
    <script src="http://localhost:35729/livereload.js">
    </script>
</head>
<body hx-ext="debug">
<nav>
    <h1>
        Easy Spring Auth
    </h1>
    <a href="/">
        User Management
    </a>
    <hr>
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
        <tbody id="${USER_TABLE_BODY_ID}">
        @for(var user: userManagementModel.easyUserList())
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
</main>
<div ${HTMX} >

</div>

<div id="${MODAL_CONTAINER_ID}" hx-on:$unsafe{CLOSE_MODAL_EVENT}="this.innerHTML = null">
</div>
</body>

</html>