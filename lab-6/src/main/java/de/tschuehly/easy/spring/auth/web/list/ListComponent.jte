@import de.tschuehly.easy.spring.auth.web.list.ListComponent.ListContext
@param ListContext listContext

@for(var context: listContext.viewContextList())
    ${context}
@endfor