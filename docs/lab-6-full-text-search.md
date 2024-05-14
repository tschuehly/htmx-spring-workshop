# Lab 6: Full Text Search

We now have a lot of users in our system. We want to enable our users to search through the list of users.

{% hint style="info" %}
If you did Lab 5 previously you can remove the `Thread.sleep(3000)` from the `UserService.findAll()` method
{% endhint %}

### Search Users

In this lab, we will stream through the list of users and filter it using the Java streams API. Create a new method `searchUser` in the `UserService`.

<pre class="language-java" data-title="UserService.java"><code class="lang-java"><strong>public List&#x3C;EasyUser> searchUser(String searchString) {
</strong>  return easyUserList.stream().filter(
      it -> it.uuid.toString().contains(searchString)
            || it.username.contains(searchString)
            || it.password.contains(
          searchString)
  ).toList();
}
</code></pre>

### ListComponent

Create a new package `de.tschuehly.easy.spring.auth.web.list` and then create a `ListComponent.java` ViewComponent.

<pre class="language-java" data-title="ListComponent.java"><code class="lang-java">package de.tschuehly.easy.spring.auth.web.list;

import de.tschuehly.spring.viewcomponent.core.component.ViewComponent;
import de.tschuehly.spring.viewcomponent.jte.ViewContext;
import java.util.List;

<strong>@ViewComponent
</strong>public class ListComponent {

  public ViewContext render(List&#x3C;ViewContext> viewContextList){ // (1)
    return new ListContext(viewContextList);
  }

  public record ListContext(List&#x3C;ViewContext> viewContextList) 
    implements ViewContext {}
}
</code></pre>

(1): The render method has a `List<ViewContext>` parameter and pass it to the `ListContext`

We then create a `ListComponent.jte` template and loop through the ViewContext List with the `@for` syntax and then render each ViewContext.

{% code title="ListComponent.jte" %}
```java
@import de.tschuehly.easy.spring.auth.web.list.ListComponent.ListContext
@param ListContext listContext

@for(var context: listContext.viewContextList())
    ${context}
@endfor
```
{% endcode %}

Next, we will autowire the `listComponent` we just created in the `UserTableComponent` and create a `renderSearch` method.

{% code title="UserTableComponent.java" %}
```java
public ViewContext renderSearch(String searchQuery) {
  List<ViewContext> userRowList = userService.searchUser(searchQuery) // (1)
      .stream().map(userRowComponent::render) // (2)
      .toList();
  return listComponent.render(userRowList); // (3)
}
```
{% endcode %}

**(1):** We will pass the `searchQuery` parameter to the `userService.searchUser` method&#x20;

**(2):** We stream through the list of users and then render each user with the `userRowComponent::render` method.

**(3):** We then pass the `userRowList` to the `listComponent.render` method and return the result.

### UserController

In the `UserController` we first need to autowire the `UserTableComponent`  .

Then we create a `GET_SEARCH_USER` and `SEARCH_PARAM`constant and a search user endpoint:

{% code title="UserController.java" %}
```java
public static final String GET_SEARCH_USER = "/search-user";
public static final String SEARCH_PARAM = "searchQuery";

@HxRequest
@GetMapping(GET_SEARCH_USER)
public ViewContext searchUser(
    @RequestParam(SEARCH_PARAM) String searchQuery // (1)
) {
  return userTableComponent.renderSearch(searchQuery); // (2)
}
```
{% endcode %}

**(1):** The endpoint has a `@RequestParam` that has `SEARCH_PARAM` as value

(2): In the method we call the `userTableComponent.renderSearch` method and return the result.

In the `UserTableComponent.jte` template we first need to add two imports:

```
@import static de.tschuehly.easy.spring.auth.user.UserController.*
@import de.tschuehly.easy.spring.auth.htmx.HtmxUtil
```

Then we will create a new `<tr>` element as the first child of the `<thead>`:

```clike
<tr>
  <th colspan="4">
    <label>
      Search Users
      <input type="text"
        name="${SEARCH_PARAM}" <%-- (1) --%>
        hx-get="${GET_SEARCH_USER}" <%-- (2) --%>
        hx-trigger="input changed delay:500ms, search" <%-- (3) --%>
        hx-target="${HtmxUtil.idSelector(USER_TABLE_BODY_ID)}"> <%-- (4) --%>
    </label>
  </th>
</tr>
```

**(1):** The `<input>` field has the `SEARCH_PARAM` constant as `name` attribute, this will map to the  `@RequestParam` we defined in the `UserController.searchUser` method.

**(2):** We then add a `hx-get` attribute and set it to `GET_SEARCH_USER` .

**(3):** We tell htmx to trigger the request when an `input` event is detected and the value has `changed`. Htmx then starts a `delay` timer of `500ms` . If the event is seen again in the 500ms it will reset the delay.

**(4):** We target the body of  the table with the `USER_TABLE_BODY_ID` and swap the innerHTML of it.

### Success!

If we restart the application and navigate to [http://localhost:8080](http://localhost:8080/) we can see that the search is working:

<figure><img src="../.gitbook/assets/image (14).png" alt=""><figcaption></figcaption></figure>

{% hint style="success" %}
Lab-6 Checkpoint 1
{% endhint %}
