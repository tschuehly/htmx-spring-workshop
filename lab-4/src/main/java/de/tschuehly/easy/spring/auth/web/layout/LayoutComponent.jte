@import static de.tschuehly.easy.spring.auth.web.layout.LayoutComponent.CLOSE_MODAL_EVENT
@import static de.tschuehly.easy.spring.auth.web.layout.LayoutComponent.MODAL_CONTAINER_ID
@import de.tschuehly.easy.spring.auth.web.layout.LayoutComponent.LayoutContext
@param LayoutContext layoutContext

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
    @for(var nav: layoutContext.navigationItemList())
        <a href="${nav.URI()}">${nav.displayName()}</a>
    @endfor
    <hr>
</nav>
<main>
    ${layoutContext.content()}
</main>
</body>
<div id="${MODAL_CONTAINER_ID}"
     hx-on:$unsafe{CLOSE_MODAL_EVENT}="this.innerHTML = null">
</div>
</html>