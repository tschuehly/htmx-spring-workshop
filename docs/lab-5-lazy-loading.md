# Lab 5: Lazy Loading

In this lab, we simulate a slow system and we want to improve the user experience by giving them faster feedback.

### Simulate a large payload and slow system

We start by creating a lot of users in the `initializeUsers` method in the `Lab5Application` class with the [datafaker.net](https://www.datafaker.net/) library.

{% code title="Lab5Application.java" %}
```java

@SpringBootApplication
public class Lab5Application {

  public static void main(String[] args) {
    SpringApplication.run(Lab5Application.class, args);
  }
  @Bean
  public ApplicationRunner initializeUsers(UserService userService, GroupService groupService) {
    return (args) -> {
      EasyUser thomas = userService.createUser(
          "Thomas",
          "This is a password"
      );
      userService.createUser(
          "Cassandra",
          "Test1234"
      );
      groupService.createGroup("USER_GROUP");
      groupService.createGroup("ADMIN_GROUP");
      groupService.addUserToGroup("USER_GROUP", thomas.uuid);
      
      Faker faker = new Faker();
      for (int i = 0; i < 10000; i++) {
        userService.createUser(
                faker.internet().username(),
                faker.internet().password()
        );
      }
    };
  }
}

```
{% endcode %}

We also introduce a delay in the `UserService.findAll` method by adding a `Thread.sleep` to simulate a slow external system.

{% code title="UserService.java" %}
```java
public List<EasyUser> findAll() {
  try {
    Thread.sleep(3000);
  } catch (InterruptedException e) {
    throw new RuntimeException(e);
  }
  return easyUserList;
} 
```
{% endcode %}

### Problem

Now start the `Lab5Application` and navigate to [http://localhost:8080](http://localhost:8080/).

You will see that the request will take 3 seconds suggesting an unresponsive system to the user.

{% embed url="https://www.youtube.com/watch?v=Mbm16OtAsFY" %}

To improve the user experience we want to lazy load the slow user table after the initial page load with htmx.

### Lazy Loading the user table

We create a constant `GET_USER_TABLE` and then add an HTTP GET endpoint in the `UserController` where we render the `userTableComponent` .

{% code title="UserController.java" %}
```java
public static final String GET_USER_TABLE = "/user-table";

@HxRequest // (1)
@GetMapping(GET_USER_TABLE)
public ViewContext userTable() {
  return userTableComponent.render();
}
```
{% endcode %}

(1): We use the `@HxRequest` annotation of the [htmx-spring-boot](https://github.com/wimdeblauwe/htmx-spring-boot) library.\
The endpoint will only accept a request when it's made from htmx.

Now restart the `Lab5Application` and try to navigate to [http://localhost:8080/user-table](http://localhost:8080/user-table).

You should not be able to access it.

<figure><img src="../.gitbook/assets/image (1) (1) (1) (1) (1).png" alt=""><figcaption></figcaption></figure>

To render the user table we need to change the UserMangement back to a ViewComponent.

<pre class="language-java" data-title="UserManagementComponent.java"><code class="lang-java"><strong>@ViewComponent
</strong>@Order(1)
public class UserManagementComponent implements Page {

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

Now create a `UserManagementComponent.jte` file in the same package as the `UserManagementComponent`

```
@import static de.tschuehly.easy.spring.auth.user.UserController.GET_USER_TABLE
<div hx-get="${GET_USER_TABLE}" <%-- (1) --%>
     hx-trigger="load">  <%-- (2) --%>
    <img src="/spinner.svg" 
         class="htmx-indicator" <%-- (3) --%>
         alt="Result loading..." width="50" >
</div>
```

(1): The `hx-get` attribute creates a `GET` request to the `GET_USER_TABLE` endpoint.

(2): The `hx-trigger="load"` attribute triggers the request when the page loads.

We can show a loading spinner with an `<img>` element.\
(3): By adding the `htmx-indicator` CSS class the element is shown while the HTTP request of the parent htmx element is in flight.

### Rendering the UserMangement

Now back in the `UserController` we autowire the `userMangementComponent` by injecting it into the constructor.

Then we replace the `userTableComponent.render()` method with `userManagementComponent.render() in`&#x20;

{% code title="UserController.java" %}
```java
public static final String USER_MANAGEMENT_PATH = "/";

@GetMapping(USER_MANAGEMENT_PATH)
public ViewContext userManagementComponent() {
  return layoutComponent.render(
      userManagementComponent.render()
  );
}
```
{% endcode %}

Restart the `Lab5Application` and navigate to [http://localhost:8080](http://localhost:8080/).

You can see that the page loads instantly and only the user table rendering is delayed.

{% embed url="https://youtu.be/Q3k-p3-9YaA" %}

{% hint style="success" %}
Lab-5 Checkpoint 1

If you are stuck you can resume at this checkpoint with:

`git checkout tags/lab-5-checkpoint-1 -b lab-5-c1`
{% endhint %}
