# Lab 7: Infinite Scroll

In this lab, we will only load new users when we are scrolling down, improving our initial page speed

### Setup

First, we add a paginated retrieval method to the UserService:

```java
// UserService.java
@Service
public class UserService {
  // ...
  public List<EasyUser> getPage(int pageNumber, int pageSize) {
    var startIndex = pageNumber * pageSize;
    var endIndex = startIndex + pageSize;
    return easyUserList.subList(startIndex, endIndex);
  }
}
```

We also adjust the utility `ListComponent` to accept multiple ViewContext lists.

<pre class="language-java"><code class="lang-java"><strong>// ListComponent.java
</strong><strong>@ViewComponent
</strong>public class ListComponent {
  
  public ViewContext render(List&#x3C;ViewContext> viewContextList, ViewContext... viewContext){
    ArrayList&#x3C;ViewContext> combinedList  = new ArrayList&#x3C;>();
    combinedList.addAll(viewContextList);
    combinedList.addAll(List.of(viewContext));
    return new ListContext(combinedList);
  }
  public ViewContext render( ViewContext... viewContext){
    return new ListContext(Arrays.stream(viewContext).toList());
  }

  public record ListContext(List&#x3C;ViewContext> viewContextList) implements ViewContext {

  }
}
</code></pre>

We then create a `InfiniteLoad` ViewComponent in the `auth.user.management.table.infinite` package.

We define a `nextPage` parameter that we can access in the ViewContext

<pre class="language-java"><code class="lang-java"><strong>// InfiniteLoadComponent.java
</strong><strong>@ViewComponent
</strong>public class InfiniteLoadComponent {

  public ViewContext render(int nextPage){
    return new InfiniteLoadContext(nextPage);
  }

  public record InfiniteLoadContext(int nextPage) 
    implements ViewContext {}
}
</code></pre>

In the `InfiniteLoadComponent.jte` template, we define an `hx-get`attribute that requests the `GET_USER_TABLE` endpoint  with `nextPage` viewContext property as URI variable.

With `hx-trigger="intersect once"` we can tell htmx to create the request once when the element intersects with the browser viewport.

With `hx-swap="outerHTML"` we tell htmx to swap the whole `<tr>` element.

```html
// InfiniteLoadComponent.jte
@import static de.tschuehly.easy.spring.auth.user.UserController.*
@import de.tschuehly.easy.spring.auth.htmx.HtmxUtil
@import de.tschuehly.easy.spring.auth.user.management.table.infinite.InfiniteLoadComponent.InfiniteLoadContext
@param InfiniteLoadContext infiniteLoadContext
<tr hx-get="${HtmxUtil.URI(GET_USER_TABLE,infiniteLoadContext.nextPage())}"
    hx-trigger="intersect once" 
    hx-swap="outerHTML">
</tr>
```

We now need to adjust the `UserController` `GET_USER_TABLE` endpoint to accept a page `@PathVariable`&#x20;

```java
// UserController.java
@Controller
public class UserController {
  // ...

  public static final String GET_USER_TABLE = "/user-table/{page}";

  @HxRequest
  @GetMapping(GET_USER_TABLE)
  public ViewContext userTable(@PathVariable String page) {
    return userTableComponent.render(Integer.parseInt(page));
  }
}
```

We now need to adjust the `UserTableComponent` to accept a page parameter,  then retrieve the page of users with the `userService.getPage` method.

We also then render the `userRowList` and call the `infiniteLoadComponent.render` method with the `currentPage` increased by one.

Then if we want to render the first page we return the whole UserTableComponent.&#x20;

But for each following page, we want to render the `userRowList` and the `infiniteLoadComponent` without the whole table structure.

```java
// UserTableComponent.java
@ViewComponent
public class UserTableComponent {
  // ...
  public record UserTableContext(ViewContext userTableBody) 
    implements ViewContext { }


  public ViewContext render(int currentPage) {
    List<ViewContext> userRowList = userService.getPage(currentPage, 20)
        .stream().map(userRowComponent::render).toList();
    ViewContext tableBody = listComponent.render(
        userRowList,
        infiniteLoadComponent.render(currentPage + 1)
    );
    if(currentPage == 0){
      return new UserTableContext(tableBody);
    }
    return tableBody;
  }
}
```

As we replaced the userTableRowList property of the UserTableContext with only the userTableBody ViewContext property we also need to replace that in the `<tbody>` element.

```html
// UserTableComponent.jte
<tbody id="${USER_TABLE_BODY_ID}">
${userTableContext.userTableBody()}
</tbody>
```

We need to adjust the `UserMangement.jte` template to pass in 0 as the parameter.

```html
// UserManagement.jte
@import static de.tschuehly.easy.spring.auth.user.UserController.*
@import de.tschuehly.easy.spring.auth.htmx.HtmxUtil

<div hx-get="${HtmxUtil.URI(GET_USER_TABLE,0)}" hx-trigger="load">
    <img alt="Result loading..." class="htmx-indicator" width="50" src="/spinner.svg"/>
</div>
```

If we restart the application we can see that the infinite scroll is working again!

{% embed url="https://youtu.be/lRN1peOblF0" %}

{% hint style="success" %}
Lab-7 Checkpoint 1
{% endhint %}
