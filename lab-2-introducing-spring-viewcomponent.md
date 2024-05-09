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

### Migrating the UserMangement to Spring ViewComponent

To start we need to add three dependencies to the `build.gradle.kts` file.

```
implementation("de.tschuehly:spring-view-component-jte:0.7.4")
annotationProcessor("de.tschuehly:spring-view-component-core:0.7.4")
implementation("io.github.wimdeblauwe:htmx-spring-boot:3.2.0")
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

<figure><img src=".gitbook/assets/image (6) (1).png" alt=""><figcaption></figcaption></figure>

{% hint style="info" %}
Lab 2 Checkpoint 1
{% endhint %}

### Edit User

We now need to migrate the edit user functionality to Spring ViewComponent.

We create a `EditUserComponent` next.

We autowire the userService and create a render method with a `uuid` parameter. We get the user with the `userService.findById(uuid)` method and add the uuid, username and password to the ViewContext

```java
@ViewComponent
public class EditUserComponent {

  private final UserService userService;

  public EditUserComponent(UserService userService) {
    this.userService = userService;
  }

  public ViewContext render(UUID uuid) {
    EasyUser user = userService.findById(uuid);
    return new EditUserContext(user.uuid, user.username, user.password);
  }
  
  public record EditUserContext(UUID uuid, String username, String password) 
    implements ViewContext {

  }
}
```

We then create the `EditUserComponent.jte` we can copy the content of the `EditUserForm.jte` and adjust the imports and replace the `UserForm` parameter with the `EditUserContext`&#x20;

```html
@import de.tschuehly.easy.spring.auth.user.management.edit.EditUserComponent.EditUserContext
@import static de.tschuehly.easy.spring.auth.user.UserController.POST_SAVE_USER
@param EditUserContext editUserContext

<div>
    <form>
        <label>
            UUID
            <input type="text" readonly name="uuid" value="${editUserContext.uuid().toString()}">
        </label>
        <label>
            Username
            <input type="text" name="username" value="${editUserContext.username()}">
        </label>
        <label>
            Password
            <input type="text" name="password" value="${editUserContext.password()}">
        </label>
        <button type="submit" hx-post="${POST_SAVE_USER}">
            Save User
        </button>
    </form>
</div>
```

In the `UserController.java` we can remove the UserForm and adjust the editUserModal method.

```java
@Controller
public class UserController {
  public static final String EDIT_USER_MODAL = "/save-user/modal/{uuid}";

  @GetMapping(EDIT_USER_MODAL)
  public ViewContext editUserModal(@PathVariable UUID uuid) {
    return editUserComponent.render(uuid);
  }
}
```

&#x20;We can restart the application navigate to [localhost:8080](https://localhost:8080) and the edit user modal works again.

<figure><img src=".gitbook/assets/image (2).png" alt=""><figcaption></figcaption></figure>

{% hint style="info" %}
Lab 2 Checkpoint 2
{% endhint %}

We now need to fix the save user functionality. Previously we used HX Response headers to set the swapping functionality directly in the Controller:&#x20;

```java
@PostMapping(POST_SAVE_USER)
public String saveUser(UUID uuid, String username, String password, Model model, HttpServletResponse response) {
  EasyUser user = userService.saveUser(uuid, username, password);
  model.addAttribute("easyUser", user);
  response.addHeader("HX-Retarget", "#user-" + user.uuid);
  response.addHeader("HX-Reswap", "outerHTML");
  response.addHeader("HX-Trigger", CLOSE_MODAL_EVENT);
  return "UserRow";
}
```

We now want to move this functionality to the UserRowComponent.

#### HtmxUtil

I have already created a `HtmxUtil` class that helps us set the HX Response Headers.

We are using Wim Deblauwes htmx-spring-boot library: [github.com/wimdeblauwe/htmx-spring-boo](https://github.com/wimdeblauwe/htmx-spring-boot)t. It offers a HtmxResponseHeader enum with all possible values and a HxSwapType enum.&#x20;

We will add these convenience methods:

```java
// HtmxUtil.java
public static String target(String id){
  return "#" + id;
}

public static void retarget(String cssSelector) {
  setHeader(HtmxResponseHeader.HX_RETARGET.getValue(), cssSelector);
}

public static void reswap(HxSwapType hxSwapType){
  setHeader(HtmxResponseHeader.HX_RESWAP.getValue(), hxSwapType.getValue());
}

public static void trigger(String event) {
  setHeader(HtmxResponseHeader.HX_TRIGGER.getValue(), event);
}
```

Back to the UserRowComponent we create a rerender function where we use these utility functions.

We retarget to the id of the `<tr>` element we created with the `UserRowContext.htmlUserId()` function.

We swap the whole HTML and trigger the `CLOSE_MODAL_EVENT`

Finally we return the UserRowContext with the easyUser

<pre class="language-java"><code class="lang-java"><strong>// UserRowComponent.java
</strong><strong>public ViewContext rerender(EasyUser easyUser) {
</strong>  String target = HtmxUtil.target(UserRowContext.htmlUserId(easyUser.uuid));
  HtmxUtil.retarget(target);
  HtmxUtil.reswap(HxSwapType.OUTER_HTML);
  HtmxUtil.trigger(CLOSE_MODAL_EVENT);
  return new UserRowContext(easyUser);
}
</code></pre>

In the saveUser method in the `UserController` we can just call this method:

```java
// UserController.java
@PostMapping(POST_SAVE_USER)
public ViewContext saveUser(UUID uuid, String username, String password) {
  EasyUser user = userService.saveUser(uuid, username, password);
  return userRowComponent.rerender(user);
}
```

&#x20;We can restart the application and navigate to [localhost:8080](https://localhost:8080) and the save user function works again.&#x20;

{% hint style="info" %}
Lab 2 checkpoint 3
{% endhint %}

The advantage we now have is that the Controller doesn't need to know how the UserRowComponent template looks and what needs to be swapped. The UserRowComponent just offers an API to rerender a row.

&#x20;
