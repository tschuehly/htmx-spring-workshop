@param de.tschuehly.easy.spring.auth.web.layout.LayoutComponent.LayoutContext layoutContext
<html lang="en">

<head>
    <title>Easy Spring Auth</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sakura.css/css/sakura.css" type="text/css">
    <link rel="stylesheet" href="/css/classless.css" type="text/css">
</head>
<body>
<nav>
    <ul>
        <li>
            Hello World
        </li>
    </ul>
</nav>
<main>
    ${layoutContext.content()}
</main>
</body>


</html>