@import static de.tschuehly.easy.spring.auth.user.UserController.GET_USER_TABLE
<div hx-get="${GET_USER_TABLE}" hx-trigger="load">
    <img alt="Result loading..." class="htmx-indicator" width="50" src="/spinner.svg"/>
</div>