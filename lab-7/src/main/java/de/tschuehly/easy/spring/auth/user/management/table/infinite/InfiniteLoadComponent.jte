@import static de.tschuehly.easy.spring.auth.user.UserController.*
@import de.tschuehly.easy.spring.auth.htmx.HtmxUtil
@import de.tschuehly.easy.spring.auth.user.management.table.infinite.InfiniteLoadComponent.InfiniteLoadContext
@param InfiniteLoadContext infiniteLoadContext
<tr hx-get="${HtmxUtil.URI(GET_USER_TABLE,infiniteLoadContext.nextPage())}" <%-- (1) --%>
    hx-trigger="intersect once" <%-- (2) --%>
    hx-swap="outerHTML"> <%-- (3) --%>
</tr>