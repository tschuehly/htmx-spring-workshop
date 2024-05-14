# Lab 7: Infinite Scroll

In Lab 5 we improved the perceived responsiveness of our application by lazy loading the user table.

Even without a simulated delay, the response was still quite slow as we transferred a 4.1 MB HTML table:&#x20;

<figure><img src=".gitbook/assets/image.png" alt=""><figcaption></figcaption></figure>

In this lab we will fix this by using pagination with the infinite scroll mechanism, only loading new users when the user wants to see them.

### Setup

First, we add a paginated retrieval method to the UserService:

{% code title="UserService.java" %}
```java
public List<EasyUser> getPage(int pageNumber, int pageSize) {
  var startIndex = pageNumber * pageSize;
  var endIndex = startIndex + pageSize;
  return easyUserList.subList(startIndex, endIndex);
}
```
{% endcode %}

We also add two new render methods to the `ListComponent` to allow us to combine ViewContextLists.

{% code title="ListComponent.java" %}
```java
public ViewContext render(List<ViewContext> viewContextList, ViewContext... viewContext){
  ArrayList<ViewContext> combinedList  = new ArrayList<>();
  combinedList.addAll(viewContextList);
  combinedList.addAll(List.of(viewContext));
  return new ListContext(combinedList);
}

public ViewContext render( ViewContext... viewContext){
  return new ListContext(Arrays.stream(viewContext).toList());
}
```
{% endcode %}

### InfiniteLoadComponent

We then create a `InfiniteLoadCompoent.java` ViewComponent in the `de.tschuehly.easy.spring.auth.user.management.table.infinite` package.

In the render method, we define a `nextPage` parameter that we can access in the ViewContext:

<pre class="language-java" data-title="InfiniteLoadComponent.java"><code class="lang-java">package de.tschuehly.easy.spring.auth.user.management.table.infinite;

import de.tschuehly.spring.viewcomponent.core.component.ViewComponent;
import de.tschuehly.spring.viewcomponent.jte.ViewContext;

<strong>@ViewComponent
</strong>public class InfiniteLoadComponent {

  public ViewContext render(int nextPage){
    return new InfiniteLoadContext(nextPage);
  }

  public record InfiniteLoadContext(int nextPage) 
    implements ViewContext {}
}
</code></pre>

Then we will create a `InfiniteLoadComponent.jte` template in the same package:

{% code title="InfiniteLoadComponent.jte" %}
```
@import static de.tschuehly.easy.spring.auth.user.UserController.*
@import de.tschuehly.easy.spring.auth.htmx.HtmxUtil
@import de.tschuehly.easy.spring.auth.user.management.table.infinite.InfiniteLoadComponent.InfiniteLoadContext
@param InfiniteLoadContext infiniteLoadContext

<tr hx-get="${HtmxUtil.URI(GET_USER_TABLE,infiniteLoadContext.nextPage())}" <%-- (1) --%>
    hx-trigger="intersect once" <%-- (2) --%>
    hx-swap="outerHTML"> <%-- (3) --%>
</tr>
```
{% endcode %}

**(1):** We define an `hx-get`attribute that requests the `GET_USER_TABLE` endpoint  with the `nextPage` viewContext property as URI variable.

**(2):** With `hx-trigger="intersect once"` we can tell htmx to create the request only once when the element intersects with the browser viewport.

**(3):** With `hx-swap="outerHTML"` we tell htmx to swap the whole `<tr>` element.

### UserController

We now need to change the `UserController` `GET_USER_TABLE` endpoint to accept a page `@PathVariable` by both adjusting the constant and method

{% code title="UserController.java" %}
```java
public static final String GET_USER_TABLE = "/user-table/{page}";

@HxRequest
@GetMapping(GET_USER_TABLE)
public ViewContext userTable(@PathVariable String page) {
  return userTableComponent.render(Integer.parseInt(page));
}
```
{% endcode %}

### UserTableComponent

Next, we need to adjust the `UserTableComponent` to accept a page parameter:

{% code title="UserTableComponent.java" %}
```java
public ViewContext render(int currentPage) {
  List<ViewContext> userRowList = userService.getPage(currentPage, 20) // (1)
      .stream().map(userRowComponent::render).toList(); // (2)
  ViewContext tableBody = listComponent.render(
      userRowList, // (3)
      infiniteLoadComponent.render(currentPage + 1) // (4)
  );
  if(currentPage == 0){
    return new UserTableContext(tableBody); // (5)
  }
  return tableBody; // (6)
}

public record UserTableContext(ViewContext userTableBody) // (7)
    implements ViewContext {}
```
{% endcode %}

**(1):** In the method, we first retrieve the page of users with the `userService.getPage` method.

**(2):** We then stream through the list of users and call the `userRowComponent::render` to get a ViewContext list

**(3):** Next we will call the `listComponent.render` method and pass the `userRowList`

**(4):** We also pass the`infiniteLoadComponent.render` method with the `currentPage` increased by one.

**(5):** When the user is on the first page we want to render the full `UserTableComponent`

**(6):** But for each following page, we want to render the `userRowList` and the `infiniteLoadComponent` without the whole table structure by just passing the `tableBody` variable-

**(7):** We now need to change the `UserTableContext` to have a `userTableBody` parameter

Now in the `UserTableComponent`, we need to render the `userTableBody` ViewContext property in the `<tbody>` element.

{% code title="UserTableComponent.jte" %}
```html
<tbody id="${USER_TABLE_BODY_ID}">
${userTableContext.userTableBody()}
</tbody>
```
{% endcode %}

### UserManagement

We need to adjust the `UserMangement.jte` template to pass in 0 as the parameter.

{% code title="UserManagement.jte" %}
```html
@import static de.tschuehly.easy.spring.auth.user.UserController.*
@import de.tschuehly.easy.spring.auth.htmx.HtmxUtil

<div hx-get="${HtmxUtil.URI(GET_USER_TABLE,0)}" 
     hx-trigger="load">
    <img alt="Result loading..." class="htmx-indicator" width="50" src="/spinner.svg"/>
</div>
```
{% endcode %}

### Sucess!

If we restart the application and navigate to [http://localhost:8080](http://localhost:8080/) we can see that the infinite scroll is working again!

{% embed url="https://youtu.be/lRN1peOblF0" %}

{% hint style="success" %}
Lab-7 Checkpoint 1
{% endhint %}
