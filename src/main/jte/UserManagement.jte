@import static de.tschuehly.easy.spring.auth.web.WebController.MODAL_CONTAINER
@import de.tschuehly.easy.spring.auth.web.WebController
@param de.tschuehly.easy.spring.auth.web.WebController.UserManagementModel userManagementModel

<html lang="en">

<head>
    <title>Easy Spring Auth</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sakura.css/css/sakura.css" type="text/css">
    <link rel="stylesheet" href="/css/classless.css" type="text/css">
    <script src="https://unpkg.com/htmx.org@1.9.11/dist/htmx.js"></script>
    <script src="https://unpkg.com/htmx.org@1.9.11/dist/ext/debug.js"></script>
    <script src="http://localhost:35729/livereload.js">
    </script>
</head>
<body hx-ext="debug">
<nav>
    <h1>
        Easy Spring Auth
    </h1>
</nav>
<main>
    @template.UserTable(easyUserList = userManagementModel.easyUserList())
</main>
</body>
<div id="${MODAL_CONTAINER}" hx-on:$unsafe{WebController.CLOSE_MODAL_EVENT}="this.innerHTML = null">

</div>

</html>