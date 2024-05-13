# Lab 5: Lazy Loading

In this lab, we will lazily load the user table as it now contains a lot of users and we want to reduce the load of the system.

### Simulate a large payload and slow system

We start by creating a lot of users in the `initializeUsers` method in the `@SpringBootApplication` class with the [datafaker.net](https://www.datafaker.net/) library.

```java
Faker faker = new Faker();
for (int i = 0; i < 10000; i++) {
  userService.createUser(
      faker.internet().username(),
      faker.internet().password()
  );
}
```

We also introduce a delay in the `UserService.findAll` method to simulate a slow external system.

```java
// UserService.java
public List<EasyUser> findAll() {
  try {
    TimeUnit.SECONDS.sleep(3);
  } catch (InterruptedException e) {
    throw new RuntimeException(e);
  }
  return easyUserList;
} 
```

### Lazy Loading the user table

We first create an HTTP GET endpoint where we render the `userTable` . \
We use `@HxRequest` of the [htmx-spring-boot](https://github.com/wimdeblauwe/htmx-spring-boot) library to only accept this request when it's made from htmx.

<pre class="language-java"><code class="lang-java"><strong>// UserController.java
</strong><strong>@Controller
</strong>public class UserController {
  // ...
  public static final String GET_USER_TABLE = "/user-table";

  @HxRequest
  @GetMapping(GET_USER_TABLE)
  public ViewContext userTable() {
    return userTableComponent.render();
  }
}
</code></pre>

When you try to navigate to this route in the browser we cannot access it.

<figure><img src=".gitbook/assets/image (1) (1).png" alt=""><figcaption></figcaption></figure>

To render the user table we need to change the UserMangement back to a ViewComponent.

<pre class="language-java"><code class="lang-java"><strong>// UserManagement.java
</strong><strong>@ViewComponent
</strong>@Order(1)
public class UserManagement implements Page {

  public ViewContext render() {
    return new UserManagementContext();
  }

  public record UserManagementContext() implements ViewContext {}

  @Override
  public NavigationItem navigationItem() {
    return new NavigationItem("User Management", UserController.USER_MANAGEMENT_PATH);
  }
}
</code></pre>

In the `UserManagement.jte` we can create a `<div>` element that creates a `GET` request to the `GET_USER_TABLE` endpoint.&#x20;

We use the `hx-trigger="load"` attribute to create the request when the page loads.&#x20;

We can show a loading spinner by adding an `<img>` element. By adding the `htmx-indicator` CSS class the element is shown while the HTTP request of the parent htmx element is in flight.

```html
@import static de.tschuehly.easy.spring.auth.user.UserController.GET_USER_TABLE
<div hx-get="${GET_USER_TABLE}" hx-trigger="load">
    <img alt="Result loading..." class="htmx-indicator" width="50" src="/spinner.svg"/>
</div>
```



Now back in the `UserController` we can render the `userManagement` instead of the `UserTableComponent`

```java
// UserController.java
@Controller
public class UserController {
  // ...
  public static final String USER_MANAGEMENT_PATH = "/";

  @GetMapping(USER_MANAGEMENT_PATH)
  public ViewContext userManagement() {
    return layoutComponent.render(
        userManagement.render()
    );
  }
}
```

{% hint style="success" %}
Lab-5 Checkpoint 1
{% endhint %}
