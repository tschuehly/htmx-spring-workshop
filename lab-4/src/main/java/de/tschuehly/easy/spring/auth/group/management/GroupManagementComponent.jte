@import static de.tschuehly.easy.spring.auth.group.management.GroupManagementComponent.CLOSE_MODAL_EVENT
@import static de.tschuehly.easy.spring.auth.group.management.GroupManagementComponent.MODAL_CONTAINER_ID
@import de.tschuehly.easy.spring.auth.group.management.GroupManagementComponent.GroupManagementContext
@param GroupManagementContext groupManagementContext
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
    <h2>
        Easy Spring Auth
    </h2>
    <a href="/">UserManagement</a>
    <a href="/group-management">GroupManagement</a>
    <hr>
</nav>
<main>
    ${groupManagementContext.viewContext()}
</main>
</body>
<div id="${MODAL_CONTAINER_ID}" hx-on:$unsafe{CLOSE_MODAL_EVENT}="this.innerHTML = null">

</div>

</html>