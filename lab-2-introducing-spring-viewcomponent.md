# Lab 2: Introducing Spring ViewComponent

The goal of this lab is to refactor the application to use Spring ViewComponent and htmx-spring-boot to delegate rendering responsibility to the ViewComponents and remove it from the Controller

### An Introduction to Spring ViewComponent

Spring ViewComponent is a way to create server-rendered UI components using the presenter pattern, inspired by similar libraries like Ruby on Rails ViewComponent.

A ViewComponent is a Spring-managed bean that defines a rendering context for a corresponding template, this context is called `ViewContext`.&#x20;

We can create one by annotating a class with the `@ViewComponent` annotation and defining a public nested record that implements the ViewContext interface.

```java
@ViewComponent
public class SimpleViewComponent {
    public record SimpleView(String helloWorld) implements ViewContext {
    }

    public SimpleView render() {
        return new SimpleView("Hello World");
    }
}
```

A ViewComponent needs to have a template with the same name defined in the same package. In the template, we can access the properties of the record.&#x20;

```
// SimpleViewComponent.jte
@param SimpleViewComponent.SimpleView simpleView
<div>${simpleView.helloWorld()}</div>
```

{% hint style="info" %}
Spring ViewComponent wraps the underlying MVC model using Spring AOP and enables us to create the frontend in a similar way to the component-oriented JavaScript frameworks
{% endhint %}

### Using Spring ViewComponent

To start we need to add two dependencies to the `build.gradle.kts` file.

```
implementation("de.tschuehly:spring-view-component-jte:0.7.4")
annotationProcessor("de.tschuehly:spring-view-component-core:0.7.4")
```



We start by creating a `UserManagement.java` file in the `auth.user.management` package.

<pre class="language-java"><code class="lang-java"><strong>// UserManagement.java
</strong><strong>@ViewComponent
</strong>public class UserManagement {
  public static final String MODAL_CONTAINER_ID = "modalContainer";
  public static final String CLOSE_MODAL_EVENT = "close-modal";
  
  public record UserManagementContext() 
         implements ViewContext{}

  public ViewContext render(){
    return new UserManagementContext();
  }
}
</code></pre>

We remove both `MODAL_CONTAINER_ID` and `CLOSE_MODAL_EVENT` we added to the `UserManagement.java` from `UserController.java` and fix the imports there.

We then move the `UserManagement.jte`  template to `auth.user.management` &#x20;

We now need to adjust the imports.\
We add a static import to the `UserManagement` class. \
The `param` is now a `UserManagementContext` as we put all variables into this record.&#x20;

<pre class="language-java"><code class="lang-java">// UserManagement.jte
@import static de.tschuehly.easy.spring.auth.user.management.UserManagement.*
<strong>@import de.tschuehly.easy.spring.auth.user.management.UserManagement.UserManagementContext
</strong>@param UserManagementContext userManagementContext
</code></pre>

Now we will move the table into a separate component. Create a `UserTableComponent.java` in `auth.user.management.table`

```java
// UserTableComponent.jte
@ViewComponent
public class UserTableComponent {
  private final UserService userService;

  public UserTableComponent(UserService userService) {
    this.userService = userService;
  }

  public record UserTableContext() implements ViewContext{

  }
  public static final String USER_TABLE_BODY_ID = "userTableBody";

  public ViewContext render(){
    return new UserTableContext();
  }
}
```

Once again we remove the `USER_TABLE_BODY_ID` from the `UserController.java` as we now define it in the `UserTableComponent.java` .

Then we move the `<table>` element from `UserManagement.jte` into a `UserTableComponent.jte`

Then we add the imports at the top:

```java
// UserTableComponent.jte
@import de.tschuehly.easy.spring.auth.user.management.table.UserTableComponent.UserTableContext
@import static de.tschuehly.easy.spring.auth.user.UserController.CREATE_USER_MODAL
@import static de.tschuehly.easy.spring.auth.user.management.UserManagement.MODAL_CONTAINER_ID
@import static de.tschuehly.easy.spring.auth.user.management.table.UserTableComponent.USER_TABLE_BODY_ID
@param UserTableContext userTableContext
```



We create a `UserRowComponent.java` class in `auth.user.management.table.row`&#x20;

<pre class="language-java"><code class="lang-java"><strong>// UserRowComponent.java
</strong><strong>@ViewComponent
</strong>public class UserRowComponent {

  public record UserRowContext(EasyUser easyUser) implements ViewContext {
    public static String htmlUserId(UUID uuid) {
      return "user-" + uuid;
    }
  }

  public ViewContext render(EasyUser easyUser) {
    return new UserRowContext(easyUser);
  }
}
</code></pre>

We then move the `UserRow.jte` template to `auth.user.management.table.row` and rename it to `UserRowComponent.jte`.

We then replace the `@import` and `@param` with the following:

<pre class="language-java"><code class="lang-java"><strong>// UserRowComponent.jte
</strong><strong>@import static de.tschuehly.easy.spring.auth.htmx.HtmxUtil.URI
</strong>@import static de.tschuehly.easy.spring.auth.user.UserController.*
@import static de.tschuehly.easy.spring.auth.user.management.UserManagement.MODAL_CONTAINER_ID
@import de.tschuehly.easy.spring.auth.user.management.table.row.UserRowComponent.UserRowContext
@param UserRowContext userRowContext
</code></pre>

Then we replace  the `easyUser` variable with `userRowContext.easyUser()` .

Then we replace the `<tr id="user-${uuid}">` with `<tr id="${UserRowContext.htmlUserId(uuid)}">`

<pre class="language-html"><code class="lang-html"><strong>&#x3C;!-- UserRowComponent.jte -->
</strong><strong>!{var uuid = userRowContext.easyUser().uuid;}
</strong>&#x3C;tr id="${UserRowContext.htmlUserId(uuid)}">
    &#x3C;td>
        ${uuid.toString()}
    &#x3C;/td>
    &#x3C;td>
        ${userRowContext.easyUser().username}
    &#x3C;/td>
    &#x3C;td>
        ${userRowContext.easyUser().password}
    &#x3C;/td>
    &#x3C;td>
        &#x3C;button hx-get="${URI(GET_EDIT_USER_MODAL,uuid)}"
                hx-target="#${MODAL_CONTAINER_ID}">
            &#x3C;img src="/edit.svg">
        &#x3C;/button>
    &#x3C;/td>
&#x3C;/tr>
</code></pre>

We now go into `UserTableComponent.java` and call the `userService.findAll()` method and call the autowired `userRowComponent::render` method in a map call.

We then add this `rowList` into the `UserTableContext`&#x20;

```java
// UserTableComponent.java
@ViewComponent
public class UserTableComponent {
  // ...
  public ViewContext render(){
    List<ViewContext> rowList = userService.findAll().stream()
        .map(userRowComponent::render).toList();
    return new UserTableContext(rowList);
  }
  
  public record UserTableContext(List<ViewContext> userTableRowList) 
    implements ViewContext{}
}
```

We will now replace the `@template` call with the ViewContext List:

```java
// UserTableComponent.jte
@for(var row: userTableContext.userTableRowList())
    ${row}
@endfor
```

Now we can render the UserManagement table using Spring ViewComponent!

We go to UserMangement.java and autowire the UserTableComponent and put the rendered Component into the UserManagementContext:

```java
@ViewComponent
public class UserManagement {
  public static final String MODAL_CONTAINER_ID = "modalContainer";
  public static final String CLOSE_MODAL_EVENT = "close-modal";
  private final UserTableComponent userTableComponent;

  public UserManagement(UserTableComponent userTableComponent) {
    this.userTableComponent = userTableComponent;
  }

  public record UserManagementContext(ViewContext userTable)
      implements ViewContext {}

  public ViewContext render(){
    return new UserManagementContext(userTableComponent.render());
  }
}
```

In the template, we can insert the rendered table:

```html
<main>
${userManagementContext.userTable()}
</main>
```

In the UserController.java we can autowire the UserManagement ViewComponent and render it in the index method:

```java
@Controller
public class UserController {

  private final UserService userService;
  private final UserManagement userManagement;

  public UserController(UserService userService, UserManagement userManagement) {
    this.userService = userService;
    this.userManagement = userManagement;
  }

  @GetMapping("/")
  public ViewContext index() {
    return userManagement.render();
  }
}
```

&#x20;We can restart the application now navigate to [localhost:8080](https://localhost:8080) and see the table rendered.&#x20;

<figure><img src=".gitbook/assets/image (6).png" alt=""><figcaption></figcaption></figure>
