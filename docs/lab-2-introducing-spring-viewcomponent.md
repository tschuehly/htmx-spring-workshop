# Lab 2: Using Spring ViewComponent

This lab aims to build the same application as Lab 1. \
But this time we will use Spring ViewComponent and htmx-spring-boot to delegate rendering responsibility to the ViewComponent.

### An Introduction to Spring ViewComponent

A ViewComponent is a Spring-managed bean that defines a rendering context for a corresponding template, this context is called `ViewContext`.

We can create a ViewComponent by annotating a class with the `@ViewComponent` annotation and defining a public nested record that implements the ViewContext interface.

{% code title="SimpleViewComponent.java" %}
```java
@ViewComponent
public class SimpleViewComponent {
    public record SimpleViewContext(String helloWorld) implements ViewContext {
    }

    public SimpleView render() {
        return new SimpleView("Hello World");
    }
}
```
{% endcode %}

A ViewComponent needs to have a template with the same name defined in the same package. In the template, we can access the properties of the record.

{% code title="SimpleViewComponent.jte" %}
```
@param SimpleViewComponent.SimpleViewContext simpleViewContext
<div>${simpleViewContext.helloWorld()}</div>
```
{% endcode %}

{% hint style="info" %}
Spring ViewComponent wraps the underlying MVC model using Spring AOP and enables us to create the frontend in a similar way to the component-oriented JavaScript frameworks
{% endhint %}

### Creating the UserMangement with Spring ViewComponent

To start we need to add three dependencies to the `build.gradle.kts` file.

{% code title="build.gradle.kts" %}
```kotlin
implementation("de.tschuehly:spring-view-component-jte:0.7.5-SNAPSHOT")
annotationProcessor("de.tschuehly:spring-view-component-core:0.7.5-SNAPSHOT")
implementation("io.github.wimdeblauwe:htmx-spring-boot:3.3.0")
```
{% endcode %}

We start by creating a `UserManagement.java` file in the `de.tschuehly.easy.spring.auth.user.management` package.

<pre class="language-java" data-title="UserManagement.java"><code class="lang-java"><strong>@ViewComponent
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

We then create a `UserManagement.jte` template in the `de.tschuehly.easy.spring.auth.user.management` package:

{% code title="UserManagement.jte" overflow="wrap" %}
```java
@import static de.tschuehly.easy.spring.auth.user.management.UserManagement.*
@import de.tschuehly.easy.spring.auth.user.management.UserManagement.UserManagementContext
@param UserManagementContext userManagementContext
<html lang="en">

<head>
    <title>Easy Spring Auth</title>
    <link rel="stylesheet" href="/css/sakura.css" type="text/css">
    <script src="/htmx_1.9.11.js"></script>
    <script src="/htmx_debug.js"></script>
    <script src="http://localhost:35729"></script>
</head>
<body hx-ext="debug">
<nav>
    <h1>
        Easy Spring Auth
    </h1>
</nav>
<main>
 
</main>
</body>
<div id="${MODAL_CONTAINER_ID}" hx-on:${CLOSE_MODAL_EVENT}="this.innerHTML = null">
</div>
</html>
```
{% endcode %}

We add a static import to the `UserManagement` class.\
The `param` is a `UserManagementContext` as we put all variables into this record.

Now we will create a separate component for the table.

#### UserTableComponent

Create a `UserTableComponent.java` in `de.tschuehly.easy.spring.auth.user.management.table`

{% code title="UserTableComponent.java" %}
```java
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
{% endcode %}

In the last lab, we defined the `USER_TABLE_BODY_ID` in the `UserController.java`. Now define it in the `UserTableComponent.java` .

Now we will create a `UserManagement.jte` template in the same package as the `UserTableComponent.java`:

{% code title="UserTableComponent.jte" %}
```java
@import de.tschuehly.easy.spring.auth.user.management.table.UserTableComponent.UserTableContext
@import static de.tschuehly.easy.spring.auth.user.UserController.GET_CREATE_USER_MODAL
@import static de.tschuehly.easy.spring.auth.user.management.UserManagement.MODAL_CONTAINER_ID
@import static de.tschuehly.easy.spring.auth.user.management.table.UserTableComponent.USER_TABLE_BODY_ID
@param UserTableContext userTableContext
<table>
    <thead>
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
    
    </tbody>
    <tfoot>
    <tr>
        <td colspan="4">
            <button hx-get="${GET_CREATE_USER_MODAL}" 
                    hx-target="#${MODAL_CONTAINER_ID}">
                Create new User
            </button>
        </td>
    </tr>
    </tfoot>
</table>
```
{% endcode %}

We then create a `UserRowComponent.java` class in `de.tschuehly.easy.spring.auth.user.management.table.row` package:

<pre class="language-java" data-title="UserRowComponent.java"><code class="lang-java"><strong>@ViewComponent
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

We then create a `UserRowComponent.jte` template in the `auth.user.management.table.row` package.

```java
@import static de.tschuehly.easy.spring.auth.htmx.HtmxUtil.URI
@import static de.tschuehly.easy.spring.auth.user.UserController.*
@import static de.tschuehly.easy.spring.auth.user.management.UserManagement.MODAL_CONTAINER_ID
@import de.tschuehly.easy.spring.auth.user.management.table.row.UserRowComponent.UserRowContext
@param UserRowContext userRowContext

!{var uuid = userRowContext.easyUser().uuid;} <%-- (1) --%>
<tr id="${UserRowContext.htmlUserId(uuid)}"> <%-- (2) --%>
    <td>
        ${uuid.toString()}
    </td>
    <td>
        ${userRowContext.easyUser().username} <%-- (1) --%>
    </td>
    <td>
        ${userRowContext.easyUser().password} <%-- (1) --%>
    </td>
    <td>
        <button hx-get="${URI(GET_EDIT_USER_MODAL,uuid)}"
                hx-target="#${MODAL_CONTAINER_ID}">
            <img src="/edit.svg">
        </button>
    </td>
</tr>
```

(1): We use the `userRowContext.easyUser()` attribute to access the user we want to render.

(2): We set the id of the table row using the `UserRowContext.htmlUserId(uuid)` function

### UserTableComponent

We now change the `UserTableComponent.java`:

{% code title="UserTableComponent.java" %}
```java
@ViewComponent
public class UserTableComponent {
  private final UserService userService;
  private final UserRowComponent userRowComponent; // (1)

  public UserTableComponent(UserService userService, UserRowComponent userRowComponent) {
    this.userService = userService;
    this.userRowComponent = userRowComponent; // (1)
  }

  public record UserTableContext(List<ViewContext> userTableRowList) // (2)
    implements ViewContext{

  }
  public static final String USER_TABLE_BODY_ID = "userTableBody";

  public ViewContext render(){
    List<ViewContext> rowList = userService.findAll() // (3)
        .stream().map(userRowComponent::render).toList(); // (4)
    return new UserTableContext(rowList);
  }
}
```
{% endcode %}

**(1):** We autowire the userRowComponent.

**(2):** We add  a `List<ViewContext>` property to the `UserTableContext`

**(3):**  We call the `userService.findAll()` method

**(4):** Then we call the autowired `userRowComponent::render` method in the `.stream().map()` function.

Now we will render the `userTableRowList` in the `UserTableComponent.jte`:

{% code title="UserTableComponent.jte" %}
```java
@import de.tschuehly.easy.spring.auth.user.management.table.UserTableComponent.UserTableContext
@import static de.tschuehly.easy.spring.auth.user.UserController.GET_CREATE_USER_MODAL
@import static de.tschuehly.easy.spring.auth.user.management.UserManagement.MODAL_CONTAINER_ID
@import static de.tschuehly.easy.spring.auth.user.management.table.UserTableComponent.USER_TABLE_BODY_ID
@param UserTableContext userTableContext
<table>
    <thead>
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
    @for(var row: userTableContext.userTableRowList()) <%-- (1) --%>
        ${row} <%-- (2) --%>
    @endfor
    </tbody>
    <tfoot>
    <tr>
        <td colspan="4">
            <button hx-get="${GET_CREATE_USER_MODAL}" hx-target="#${MODAL_CONTAINER_ID}">
                Create new User
            </button>
        </td>
    </tr>
    </tfoot>
</table>
```
{% endcode %}

(1): We loop through the `userTableRowList` and create a `row` loop variable.

(2): We render the `row` ViewContext in the `<tbody>`

Now we can render the table using Spring ViewComponent in the `UserMangement.java:`

<pre class="language-java" data-title="UserMangement.java"><code class="lang-java"><strong>@ViewComponent
</strong>public class UserManagement {
  public static final String MODAL_CONTAINER_ID = "modalContainer";
  public static final String CLOSE_MODAL_EVENT = "close-modal";
  private final UserTableComponent userTableComponent; // (1)

  public UserManagement(UserTableComponent userTableComponent) {// (1)
    this.userTableComponent = userTableComponent; 
  }

  public record UserManagementContext(ViewContext userTable)
      implements ViewContext {}

  public ViewContext render(){
    return new UserManagementContext(userTableComponent.render());
  }
}
</code></pre>

(1): We autowire the `UserTableComponent`

(2): We add a `userTable` ViewContext field to the `UserManagementContext`

(3): In the `UserManagement.render` method we call the `userTableComponent.render` and pass it into the `UserManagementContext` constructor.

In the `UserManagement.jte` template, we can insert the rendered table:

{% code title="UserManagement.jte" %}
```html
@import static de.tschuehly.easy.spring.auth.user.management.UserManagement.*
@import de.tschuehly.easy.spring.auth.user.management.UserManagement.UserManagementContext
@param UserManagementContext userManagementContext
<html lang="en">

<head>
    <title>Easy Spring Auth</title>
    <link rel="stylesheet" href="/css/sakura.css" type="text/css">
    <script src="/htmx_1.9.11.js"></script>
    <script src="/htmx_debug.js"></script>
    <script src="http://localhost:35729"></script>
</head>
<body hx-ext="debug">
<nav>
    <h1>
        Easy Spring Auth
    </h1>
</nav>
<main>
    ${userManagementContext.userTable()}
</main>
</body>
<div id="${MODAL_CONTAINER_ID}" hx-on:$unsafe{CLOSE_MODAL_EVENT}="this.innerHTML = null">

</div>

</html>
```
{% endcode %}

In the UserController.java we can autowire the `UserManagement` ViewComponent and render it in the index method:

<pre class="language-java" data-title="UserController.java"><code class="lang-java"><strong>@Controller
</strong>public class UserController {

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
</code></pre>

We can restart the application, navigate to [http://localhost:8080/](http://localhost:8080/) and see the table rendered.

<figure><img src="../.gitbook/assets/image (6) (1) (1).png" alt=""><figcaption></figcaption></figure>

{% hint style="success" %}
Lab 2 Checkpoint 1

If you are stuck you can resume at this checkpoint with:&#x20;

`git checkout tags/lab-2-checkpoint-1 -b current_lab`
{% endhint %}

### Edit User

We now create the edit user functionality with Spring ViewComponent.

We create an `EditUserComponent.java` in the `de.tschuehly.easy.spring.auth.user.management.edit` package:

{% code title="EditUserComponent.java" %}
```java
@ViewComponent
public class EditUserComponent {

  private final UserService userService;

  public EditUserComponent(UserService userService) { // (1)
    this.userService = userService;
  }

  public ViewContext render(UUID uuid) { // (2)
    EasyUser user = userService.findById(uuid); // (3)
    return new EditUserContext(user.uuid, user.username, user.password); // (4)
  }
  
  public record EditUserContext(UUID uuid, String username, String password) 
    implements ViewContext {

  }
}
```
{% endcode %}

**(1):** We first autowire the `userService` in the constructor&#x20;

**(2):** Then we create a render method with a `uuid` parameter.

**(3):** We get the user with the `userService.findById(uuid)` method

**(4):** We add the uuid, username and password of the user to the `EditUserContext` ViewContext

***

We then create the `EditUserComponent.jte` template in the same package as the `EditUserComponent.java`

{% code title="EditUserComponent.jte" %}
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
{% endcode %}

In the `UserController.java` we remove the `UserForm` record, autowire the `EditUserComponent` and then change the `editUserModal` method.

{% code title="UserController.java" %}
```java
public static final String GET_EDIT_USER_MODAL = "/save-user/modal/{uuid}";

@GetMapping(GET_EDIT_USER_MODAL)
public ViewContext editUserModal(@PathVariable UUID uuid) {
  return editUserComponent.render(uuid);
}
```
{% endcode %}

We can restart the application navigate to [http://localhost:8080/](http://localhost:8080/) and the edit user modal works again.

<figure><img src="../.gitbook/assets/image (2) (2).png" alt=""><figcaption></figcaption></figure>

{% hint style="success" %}
Lab 2 Checkpoint 2

If you are stuck you can resume at this checkpoint with:&#x20;

`git checkout tags/lab-2-checkpoint-2 -b current_lab`
{% endhint %}

### Fix the Save User functionality&#x20;

In Lab1 we used HX Response headers to set the swapping functionality directly in the `UserController.java`:

{% code title="UserController.java" %}
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
{% endcode %}

We now want to move this functionality to the UserRowComponent.

### HtmxUtil

I have already created a `HtmxUtil` class in the `de.tschuehly.easy.spring.auth.htmx` package that helps us set the HX Response Headers.

We are using Wim Deblauwes htmx-spring-boot library: [github.com/wimdeblauwe/htmx-spring-boot](https://github.com/wimdeblauwe/htmx-spring-boot). It offers a HtmxResponseHeader enum with all possible values and a HxSwapType enum.

We will add these convenience methods to the `HtmxUtil.java` class:&#x20;

{% code title="HtmxUtil.java" %}
```java
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
{% endcode %}

Back to the `UserRowComponent` we create a `rerender` function where we use these utility functions:

<pre class="language-java" data-title="UserRowComponent.java"><code class="lang-java"><strong>public ViewContext rerender(EasyUser easyUser) {
</strong>  String target = HtmxUtil.target(UserRowContext.htmlUserId(easyUser.uuid)); // (1)
  HtmxUtil.retarget(target); 
  HtmxUtil.reswap(HxSwapType.OUTER_HTML); // (2)
  HtmxUtil.trigger(CLOSE_MODAL_EVENT); // (3)
  return new UserRowContext(easyUser); // (4)
}
</code></pre>

**(1):** We retarget to the id of the `<tr>` element we created with the `UserRowContext.htmlUserId()` function.

**(2):** We swap the outerHTML of the target element &#x20;

**(3).** And we trigger the `CLOSE_MODAL_EVENT`

**(4):** Finally, we return the UserRowContext with the easyUser

***

In the `UserController.saveUser` method we can call the `userRowComponent.rerender` method&#x20;

{% code title="UserController.java" %}
```java
@PostMapping(POST_SAVE_USER)
public ViewContext saveUser(UUID uuid, String username, String password) {
  EasyUser user = userService.saveUser(uuid, username, password);
  return userRowComponent.rerender(user);
}
```
{% endcode %}

We can restart the application and navigate to [http://localhost:8080/](http://localhost:8080/) and the save user function works again!

{% hint style="success" %}
Lab 2 Checkpoint 3

If you are stuck you can resume at this checkpoint with:&#x20;

`git checkout tags/lab-2-checkpoint-3 -b current_lab`
{% endhint %}

We have the advantage that the Controller doesn't need to know how the UserRowComponent template looks and what needs to be swapped. \
The UserRowComponent offers an API to rerender a row.

### Create User

Finally, we need to migrate the create user functionality to Spring ViewComponent.\
We start by creating a `CreateUserComponent` in the `de.tschuehly.easy.spring.auth.user.management.create` package:

<pre class="language-java" data-title="CreateUserComponent.java"><code class="lang-java"><strong>@ViewComponent
</strong>public class CreateUserComponent {

  public record CreateUserContext() implements ViewContext{}

  public ViewContext render(){
    return new CreateUserContext();
  }
}
</code></pre>

We now need to create a `CreateUserComponent.jte` in the same package as the `CreateUserComponent.java`

{% code title="CreateUserComponent.jte" %}
```html
@import static de.tschuehly.easy.spring.auth.user.UserController.POST_CREATE_USER
<div style="width: 100dvw; height: 100dvh; position: fixed; top: 0;left: 0; background-color: rgba(128,128,128,0.69); display: flex; justify-content: center; align-items: center;">
    <form style="background-color: whitesmoke; padding: 2rem;">
        <label>
            Username
            <input type="text" name="username">
        </label>
        <label>
            Password
            <input type="text" name="password">
        </label>
        <button type="submit" hx-post="${POST_CREATE_USER}">
            Save User
        </button>
    </form>
</div>
```
{% endcode %}

We can now call the `createUserComponent.render` method in the `UserController.getCreateUserModal` method:

{% code title="UserController.java" %}
```java
public static final String CREATE_USER_MODAL = "/create-user/modal";

@GetMapping(CREATE_USER_MODAL)
public ViewContext getCreateUserModal() {
  return createUserComponent.render();
}
```
{% endcode %}

We can restart the application and navigate to [http://localhost:8080/](http://localhost:8080/) and the create user modal is shown when we click on `Create User`

<figure><img src="../.gitbook/assets/image (10).png" alt=""><figcaption></figcaption></figure>

Finally, we need to migrate the `UserController.createUser` method. \
Currently, it looks like this:

<pre class="language-java" data-title="UserController.java"><code class="lang-java"><strong>@PostMapping(POST_CREATE_USER)
</strong>public String createUser(String username, String password, Model model, HttpServletResponse response) {
  EasyUser user = userService.createUser(username, password);
  model.addAttribute("easyUser", user);

  response.addHeader("HX-Retarget", "#" + USER_TABLE_BODY_ID);
  response.addHeader("HX-Reswap", "afterbegin");
  response.addHeader("HX-Trigger", CLOSE_MODAL_EVENT);
  return "UserRow";
}
</code></pre>

As before we want to move this code into the UserRow component, by creating a new `renderNewRow` function:

{% code title="UserRowComponent.java" %}
```java
public ViewContext renderNewRow(EasyUser user) {
  String target = HtmxUtil.target(UserTableComponent.USER_TABLE_BODY_ID);
  HtmxUtil.retarget(target);
  HtmxUtil.reswap(HxSwapType.AFTER_BEGIN);
  HtmxUtil.trigger(UserManagement.CLOSE_MODAL_EVENT);
  return new UserRowContext(user);
}
```
{% endcode %}

We can now simplify the `UserController.createUser` function:

{% code title="UserController.java" %}
```java
@PostMapping(POST_CREATE_USER)
public ViewContext createUser(String username, String password) {
  EasyUser user = userService.createUser(username, password);
  return userRowComponent.renderNewRow(user);
}
```
{% endcode %}

Now if we restart the application we can save a new user and they are inserted at the start of the table.

<figure><img src="../.gitbook/assets/image (11).png" alt=""><figcaption></figcaption></figure>

{% hint style="success" %}
Lab 2 Checkpoint 4

If you are stuck you can resume at this checkpoint with:&#x20;

`git checkout tags/lab-2-checkpoint-4 -b current_lab`
{% endhint %}
