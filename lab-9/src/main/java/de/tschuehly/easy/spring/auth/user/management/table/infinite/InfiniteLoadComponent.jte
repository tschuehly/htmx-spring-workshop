@import static de.tschuehly.easy.spring.auth.user.UserController.*
@import de.tschuehly.easy.spring.auth.htmx.HtmxUtil
@import de.tschuehly.easy.spring.auth.user.management.table.infinite.InfiniteLoadComponent.InfiniteLoadContext
@param InfiniteLoadContext infiniteLoadContext
<tr hx-get="${HtmxUtil.URI(GET_USER_TABLE,infiniteLoadContext.nextPage())}"
     hx-trigger="intersect once" hx-swap="outerHTML">
    
</tr>