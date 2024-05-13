# Lab 9: Server-Sent Events

In this lab, we will leverage the SSE capabilities of htmx to update our table live if a new user is created.



We start by adding the htmx `sse.js` extension to our `LayoutComponent.jte` template.

```
// LayoutComponent.jte
<head>
    // ...
    <script src="https://unpkg.com/htmx.org@1.9.11/dist/ext/sse.js"></script>
</head>
```

We simulate that a new user get's created every 5 seconds using a [Flux](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html) in the `UserService`

<pre class="language-java"><code class="lang-java">// UserService.java
@Service
public class UserService {
<strong>    public Flux&#x3C;EasyUser> subscribeToNewUserCreation() {
</strong>    Faker faker = new Faker();
    return Flux.interval(Duration.ofSeconds(5)).map(
        val -> createUser(faker.internet().username(), faker.internet().password())
    );
  }
}
</code></pre>

In the `UserTableComponent` we create a new `subscribeToNewUserRow` method where we call the `userService.subscribeToNewUserCreation()` .

In the `.map()` function we render the userRowComponent and then get the string value of the template by autowiring the `TemplateEngine` and passing the ViewContext to the `jteTemplateEngine.render` function.

This gives us a String Flux stream of the rendered `userRowComponent`

```java
@ViewComponent
public class UserTableComponent {
  @Autowired
  private final TemplateEngine jteTemplateEngine;

  public Flux<String> subscribeToNewUserRow(){
    return userService.subscribeToNewUserCreation().map(
        user -> {
          ViewContext viewContext = userRowComponent.render(user);
          StringOutput stringOutput = new StringOutput();
          jteTemplateEngine.render(
              IViewContext.Companion.getViewComponentTemplate(viewContext),
              viewContext,
              stringOutput
          );
          return stringOutput.toString();
        }
    );
  }
}
```



In the `UserController` we create a new HTTP endpoint with `text/event-stream` media type.

In the `subscribeUser()` method we create an `SseEmitter` and call the `userTableComponent.subscribeToNewUserRow()` method and subscribe to the flux and send the rendered row with the `emitter.send` method.

```java
public static final String GET_SUBSCRIBE_USER = "/subscribe-new-user";

@GetMapping(value = GET_SUBSCRIBE_USER, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public SseEmitter subscribeUser() {
  SseEmitter emitter = new SseEmitter();
  userTableComponent.subscribeToNewUserRow().subscribe(
      row -> {
        try {
          emitter.send(row);
        } catch (Exception e) {
          emitter.completeWithError(e);
        }
      }, emitter::completeWithError, emitter::complete
  );
  return emitter;
}
```

Back in the `UserTableComponent.jte` template we create a `<tr>` element in the table body.

We enable the [SSE extension](https://htmx.org/extensions/server-sent-events/) with the `hx-ext="sse"` attribute.

We can subscribe to the endpoint with the `sse-connect` attribute and tell htmx to swap the SSE message with the name `message` and swap the message content after the `<tr>` element with [`hx-swap="afterend"`](https://htmx.org/attributes/hx-swap/)

We also style the incoming user row with a CSS transition.

```html
@import static de.tschuehly.easy.spring.auth.user.UserController.GET_SUBSCRIBE_USER

<style>
  tr.htmx-added {
    opacity: 0;
  }
  tr{
    opacity: 1;
    transition: opacity 1s ease-out;
  }
</style>
<table>
    // ...
    <tbody id="${USER_TABLE_BODY_ID}">
    <tr hx-ext="sse" sse-connect="${GET_SUBSCRIBE_USER}" 
        sse-swap="message" hx-swap="afterend"></tr>
    ${userTableContext.userTableBody()}
    </tbody>
    // ...
</table>
```

If we now restart the application we can see a new user is added every 5 seconds:

{% embed url="https://youtu.be/XOUxhawvwls" %}

{% hint style="success" %}
Lab-9 Checkpoint 1
{% endhint %}
