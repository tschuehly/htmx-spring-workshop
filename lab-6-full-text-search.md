# Lab 6: Full Text Search

As we now have a lot of users in our system we want to be able to search through the list of users.

{% hint style="info" %}
If you did Lab 5 previously you can remove the `TimeUnit.SECONDS.sleep(3);` from the UserService
{% endhint %}

For this workshop, we will stream through the list of users and filter it using the Java streams API.

<pre class="language-java"><code class="lang-java"><strong>// UserService.java
</strong><strong>public List&#x3C;EasyUser> searchUser(String searchString) {
</strong>  return easyUserList.stream().filter(
      it -> it.uuid.toString().contains(searchString)
            || it.username.contains(searchString)
            || it.password.contains(
          searchString)
  ).toList();
}
</code></pre>

Now we will create a utility List ViewComponent in `auth.web.list`, that receives a `List<ViewContext>` as a parameter and passes it to the `ListContext` &#x20;

```java
// ListComponent.java
@ViewComponent
public class ListComponent {

  public ViewContext render(List<ViewContext> viewContextList){
    return new ListContext(viewContextList);
  }

  public record ListContext(List<ViewContext> viewContextList) 
    implements ViewContext {}
}
```

In the template, we loop through the ViewContext List and render each ViewContext.

```java
// ListComponent.jte
@param de.tschuehly.easy.spring.auth.web.list.ListComponent.ListContext listContext

@for(var context: listContext.viewContextList())
    ${context}
@endfor
```



Next, we will create a `renderSearch` method in the UserTableComponent, here we will pass the `searchQuery` parameter to the `userService` and then render each user with the `userRowComponent`.

We then pass this `userRowList` to the `listComponent.render` method and return the result.

```java
// UserTableComponent.java
@ViewComponent
public class UserTableComponent {
  // ...
  public ViewContext renderSearch(String searchQuery) {
    List<ViewContext> userRowList = userService.searchUser(searchQuery)
        .stream().map(userRowComponent::render).toList();
    return listComponent.render(userRowList);
  }
}
```

In the UserController we create a `/search-user` endpoint that has a `searchQuery` `@RequestParam` .\
We then call the `userTableComponent.renderSearch` method and return the result.

```java
// UserController.java
@Controller
public class UserController {

  public static final String GET_SEARCH_USER = "/search-user";
  public static final String SEARCH_PARAM = "searchQuery";
  @HxRequest
  @GetMapping(GET_SEARCH_USER)
  public ViewContext searchUser(
                     @RequestParam(SEARCH_PARAM) String searchQuery) {
    return userTableComponent.renderSearch(searchQuery);
  }
}
```

In the `UserTableComponent.jte` template we will create a new `<tr>` element in the table head and create an `<input>` field with the `SEARCH_PARAM` as name attribute.

We then add a `hx-get` attribute and set it to `GET_SEARCH_USER` .\
We tell htmx to trigger the Request when an `input` event was detected and the value has `changed` and starts a `delay` timer of `500ms` . If the event is seen again in the 500ms it will reset the delay.

We target the body of  the table with the `USER_TABLE_BODY_ID` and swap the innerHTML of it.

```html
// UserTableComponent.jte
@import de.tschuehly.easy.spring.auth.htmx.HtmxUtil
@import static de.tschuehly.easy.spring.auth.user.UserController.*
@import static de.tschuehly.easy.spring.auth.user.management.table.UserTableComponent.USER_TABLE_BODY_ID
<table>
    <thead>
    <tr>
       <th colspan="4">
            <label>
                Search Users
                <input type="text"
                       name="${SEARCH_PARAM}"
                       hx-get="${GET_SEARCH_USER}"
                       hx-trigger="input changed delay:500ms, search"
                       hx-target="${HtmxUtil.idSelector(USER_TABLE_BODY_ID)}">
            </label>
       </th>
    </tr>
    <tr>
        <th>
            uuid
        </th>
        <th>
            username
        </th>
        <th>
            password
        </th>
    </tr>
    </thead>
    <tbody id="${USER_TABLE_BODY_ID}">
    <!-- ... -->
```

We can now restart the application and see that the search is working:

<figure><img src=".gitbook/assets/image (14).png" alt=""><figcaption></figcaption></figure>

{% hint style="success" %}
Lab-6 Checkpoint 1
{% endhint %}
