@import static de.tschuehly.easy.spring.auth.user.UserController.GET_USER_TABLE
<div hx-get="${GET_USER_TABLE}" <%-- (1) --%>
     hx-trigger="load">  <%-- (2) --%>
    <img src="/spinner.svg"
         class="htmx-indicator" <%-- (3) --%>
         alt="Result loading..." width="50" >
</div>