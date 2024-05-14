# Lab 1: Server-side rendering with Spring Boot and JTE

This lab aims to build a simple user management application with Spring Boot and htmx.

We are using [JTE](https://jte.gg) as the server-side template language.

## Display Users

We want to display a table of users like this:

<figure><img src="../.gitbook/assets/image (2) (1).png" alt=""><figcaption></figcaption></figure>

### UserController

First, navigate to the `UserController` in `de.tschuehly.easy.spring.auth.user`

We create a new `@GetMapping` and inside the method, we call the `userService.findAll()` function and add it to the MVC model as `easyUserList` attribute.

We also define a constant for the `USER_TABLE_BODY_ID` and the `MODAL_CONTAINER_ID`

{% code title="UserController.java" %}
```java
package de.tschuehly.easy.spring.auth.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {
  public static final String MODAL_CONTAINER_ID = "modalContainer";
  public static final String USER_TABLE_BODY_ID = "userTableBody";

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/") 
  public String index(Model model) {
    model.addAttribute("easyUserList", userService.findAll());
    return "UserManagement";
  }  
}
```
{% endcode %}

We return the string `UserManagement` . This is the reference to the View we want to render.

Spring MVC will look for templates in the `src/main/jte` directory as we use the `gg.jte:jte-spring-boot-starter-3`&#x20;

You can find all the templates we need already there. We now need to fill them with life.

### UserMangament

We start with `UserManagement.jte` template. As you can see, a barebones html structure is already in place with CSS and htmx already referenced.

We need to import all static variables defined in UserController with the `@import` JTE syntax

{% code title="UserManagement.jte " %}
```crystal
@import static de.tschuehly.easy.spring.auth.user.UserController.*
@import de.tschuehly.easy.spring.auth.user.EasyUser
```
{% endcode %}

Then wee add the easyUserList we defined earlier in the model as a parameter to the template with `@param`.

{% code title="UserManagement.jte " %}
```crystal
@param List<EasyUser> easyUserList
```
{% endcode %}

There is already a basic HTML Table in place we need to fill with our data.

In the table body, we loop over the easyUserList with the `@for` JTE syntax and call the userRow.jte template with the `@template` syntax and pass the user loop variable into the template.

{% code title="UserManagement.jte " %}
```html
<tbody id="${USER_TABLE_BODY_ID}">
@for(var user: easyUserList)
    @template.userRow(easyUser = user)
@endfor
</tbody>
```
{% endcode %}

We also add the `public static final String USER_TABLE_BODY_ID` to the `tbody` element to reference it statically from other places.

We also add an empty div with the id set to `MODAL_CONTAINER_ID` to show a modal later.

{% code title="UserManagement.jte" %}
```html
<div id="${MODAL_CONTAINER_ID}"></div>
```
{% endcode %}

Your UserManagement.jte template should now look like this:&#x20;

```html
@import static de.tschuehly.easy.spring.auth.user.UserController.*
@import de.tschuehly.easy.spring.auth.user.EasyUser
@param java.util.List<EasyUser> easyUserList
<html lang="en">

<head>
    <title>Easy Spring Auth</title>
    <link rel="stylesheet" href="/css/sakura.css" type="text/css">
    <script src="/htmx_1.9.11.js"></script>
    <script src="/htmx_debug.js"></script>
</head>
<body hx-ext="debug">
<nav>
    <h1>
        Easy Spring Auth
    </h1>
</nav>
<main>
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
        <tbody>
        @for(var user: easyUserList)
            @template.userRow(easyUser = user)
        @endfor
        </tbody>
        <tfoot>
        </tfoot>
    </table>
</main>
</body>
<div id="${MODAL_CONTAINER_ID}"></div>
</html>
```

### UserRow

The UserRow.jte template defines an EasyUser parameter and a local variable with the exclamation mark JTE expression: `!{var name = value}` .

{% code title="UserRow.jte" %}
```crystal
@import de.tschuehly.easy.spring.auth.user.EasyUser
@param EasyUser easyUser
!{var uuid = easyUser.uuid.toString();}
```
{% endcode %}

We then add the uuid of the user as id to the `<tr>` element and add a `<td>`element for the uuid, username and password and display the value of the user.

{% code title="UserRow.jte" %}
```html
<tr id="user-${uuid}">
    <td>
        ${uuid}
    </td>
    <td>
        ${easyUser.username}
    </td>
    <td>
        ${easyUser.password}
    </td
</tr>
```
{% endcode %}

We can see all currently defined users if we start the application and navigate to [http://localhost:8080](http://localhost:8080/).

<figure><img src="../.gitbook/assets/image (4).png" alt=""><figcaption></figcaption></figure>

{% hint style="success" %}
Lab-1 Checkpoint 1

If you are stuck you can resume at this checkpoint with:&#x20;

`git checkout tags/lab-1-checkpoint-1 -b current_lab`
{% endhint %}

### Edit users

Next, we want to edit a user that has already been created and change his username.

We create a new GetMapping endpoint in the `UserController`

{% code title="UserController.java" %}
```java
public static final String EDIT_USER_MODAL = "/save-user/modal/{uuid}";
@GetMapping(EDIT_USER_MODAL)
public String editUserModal(Model model, @PathVariable UUID uuid) {
  return "EditUserForm";
}
```
{% endcode %}

As you can see we are using a static constant for the HTTP Endpoint. This makes it easy to understand what controller mappings htmx sends requests to.

#### UserRow

In the `UserRow.jte` we add a new `<td>` element and create a button element.

{% code title="UserRow.jte" %}
```html
@import de.tschuehly.easy.spring.auth.htmx.HtmxUtil
@import static de.tschuehly.easy.spring.auth.user.UserController.*
<td>
    <button hx-get="${HtmxUtil.URI(EDIT_USER_MODAL,uuid)}" <%-- (1) --%>
            hx-target="#${MODAL_CONTAINER_ID}">  <%-- (2) --%>
        <img src="/edit.svg">
    </button>
</td>
```
{% endcode %}

(1): `hx-get="${URI(EDIT_USER_MODAL,uuid)}` creates an HTTP get request to `/user/edit/{uuid}` when the button element is clicked.&#x20;

{% hint style="info" %}
We use the `HtmxUtil.URI()` method that creates a `UriTemplate` and fills it with the variables we pass.
{% endhint %}

(2): `hx-target="#${MODAL_CONTAINER_ID}"` tells HTMX to swap the response body with the element where the id equals `modalContainer` . We created this earlier in the `UserManagement.jte` template



To make this very simple we create a UserForm record:

{% code title="UserController.java" %}
```
public record UserForm(String uuid, String username, String password) {

}
```
{% endcode %}

We retrieve the data from the datastore and add it to the model, using the record we just defined.

{% code title="UserController.java" %}
```java
@GetMapping(EDIT_USER_MODAL)
public String editUserModal(Model model, @PathVariable UUID uuid) {
  var user = userService.findById(uuid);
  model.addAttribute("userForm", new UserForm(user.uuid.toString(), user.username, user.password));
  return "EditUserForm";
}
```
{% endcode %}

In the corresponding `EditUserForm.jte` template we display the values in a `<form>` element:

<pre class="language-html" data-title="EditUserForm.jte"><code class="lang-html">@param de.tschuehly.easy.spring.auth.user.UserController.UserForm userForm
<strong>&#x3C;form>
</strong>    &#x3C;label>
        UUID
        &#x3C;input type="text" readonly name="uuid" value="${userForm.uuid()}">
    &#x3C;/label>
    &#x3C;label>
        Username
        &#x3C;input type="text" name="username" value="${userForm.username()}">
    &#x3C;/label>
    &#x3C;label>
        Password
        &#x3C;input type="text" name="password" value="${userForm.password()}">
    &#x3C;/label>
&#x3C;/form>
</code></pre>

Now, restart the application, navigate to [http://localhost:8080](http://localhost:8080/), and click the edit button. You should now see the modal popup and the values of the user displayed.

<figure><img src="../.gitbook/assets/image (5).png" alt=""><figcaption></figcaption></figure>

{% hint style="success" %}
Lab-1 Checkpoint 2

If you are stuck you can resume at this checkpoint with:&#x20;

`git checkout tags/lab-1-checkpoint-2 -b current_lab`
{% endhint %}

### Save edited User

We now want to change a value, save it to the datastore and display the updated value in the table.

Let's create a new endpoint and call the `userService.saveUser()` endpoint, then we add the new user value to the model and return the UserRow template.

{% code title="UserController.java" %}
```java
public static final String SAVE_USER = "/save-user";

@PostMapping(SAVE_USER)
public String saveUser(UUID uuid, String username, String password, Model model) {
  EasyUser user = userService.saveUser(uuid, username, password);
  model.addAttribute("easyUser", user);
  return "UserRow";
}
```
{% endcode %}

We can then call this endpoint by adding a button with an `hx-post` attribute that uses the `SAVE_USER` constant.

{% hint style="info" %}
We can also place the hx- attribute on the form, as the button would trigger a form submit, that htmx catches.
{% endhint %}

<pre class="language-html" data-title="EditUserForm.jte"><code class="lang-html"><strong>@import static de.tschuehly.easy.spring.auth.user.UserController.SAVE_USER
</strong><strong>&#x3C;form>
</strong><strong>  &#x3C;button type="submit" hx-post="${POST_SAVE_USER}">
</strong>    Save User
  &#x3C;/button>
&#x3C;/form>
</code></pre>

If you restart the app now you will see that the table value is not updated, but instead the table row is rendered in the button, because htmx by default swaps the innerHTML of the element that created the request.

<figure><img src="../.gitbook/assets/image (6).png" alt=""><figcaption></figcaption></figure>

To fix this, we can return htmx attributes as HTTP Response headers.

We add `HX-Retarget = #user-${user.uuid}`to target the table row \<tr> element that contains the user we just edited.\
With `HX-Reswap = outerHTML` we tell htmx to swap the whole table row.

{% code title="UserController.java" %}
```java
public static final String POST_SAVE_USER = "/save-user";
public static final String CLOSE_MODAL_EVENT = "close-modal";
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

With `HX-Trigger = close-modal` we tell htmx to trigger a JavaScript event `close-modal` in the browser when the HTTP response is received.

If we now listen to this event in the `MODAL_CONTAINER_ID` element using `hx-on` and use the JTE `$unsafe` syntax, we can set the innerHTML to null and remove the modal.

{% code title="UserManagement.jte" %}
```html
<div id="${MODAL_CONTAINER_ID}" 
     hx-on:$unsafe{CLOSE_MODAL_EVENT}="this.innerHTML = null">
</div>
```
{% endcode %}

If we click the `Save User` button and go to Chrome DevTools we see Hypermedia as the Engine of Application State (HATEOAS) in action.\
The new application state after saving the user is transferred via HTML to the browser, and the new row also includes the link to get the modal it was just called from.

<figure><img src="../.gitbook/assets/image (7).png" alt=""><figcaption></figcaption></figure>

{% hint style="success" %}
Lab-1 Checkpoint 3

If you are stuck you can resume at this checkpoint with:&#x20;

`git checkout tags/lab-1-checkpoint-3 -b current_lab`
{% endhint %}

### Create User

As the next step, we want to create a new User.

To start we create a new endpoint `CREATE_USER_MODAL` where we return the CreateUserForm template.

{% code title="UserController.java" %}
```java
public static final String CREATE_USER_MODAL = "/create-user/modal";

@GetMapping(CREATE_USER_MODAL)
public String getCreateUserModal() {
  return "CreateUserForm";
}
```
{% endcode %}

We add this as a table footer of the UserManagement table, in a button element and target the `MODAL_CONTAINER_ID`

{% code title="UserManagement.jte" %}
```html
<tfoot>
  <tr>
    <td colspan="4">
        <button hx-get="${CREATE_USER_MODAL}" 
                hx-target="#${MODAL_CONTAINER_ID}">
            Create new User
        </button>
    </td>
  </tr>
</tfoot>
```
{% endcode %}

The `CreateUserForm.jte` looks like this, as you can see here, this time we have the hx-post attribute on the `<form>` element and trigger the HTTP request with the `<button type="submit">`

{% code title="CreateUserForm.jte" %}
```html
@import static de.tschuehly.easy.spring.auth.user.UserController.POST_CREATE_USER
<form hx-post="${POST_CREATE_USER}">
    <label>
        Username
        <input type="text" name="username">
    </label>
    <label>
        Password
        <input type="text" name="password">
    </label>
    <button type="submit">
        Save User
    </button>
</form>
```
{% endcode %}

We now need to create the `POST_CREATE_USER` endpoint.

It follows the same pattern as the `POST_SAVE_USER` endpoint, but this time we target the `<body>` element using `HX-Retarget: #USER_TABLE_BODY_ID` and the use `HX-Reswap: afterbegin`

`afterbegin` inserts the content of the response as the first child of the target element.

We also trigger the `CLOSE_MODAL_EVENT` and return the `UserRow.jte` template.

```java
  public static final String POST_CREATE_USER = "/create-user";
  
  @PostMapping(POST_CREATE_USER)
  public String createUser(String username, String password, Model model, HttpServletResponse response) {
    EasyUser user = userService.createUser(username, password);
    model.addAttribute("easyUser", user);

    response.addHeader("HX-Retarget", "#" + USER_TABLE_BODY_ID);
    response.addHeader("HX-Reswap", "afterbegin");
    response.addHeader("HX-Trigger", CLOSE_MODAL_EVENT);
    return "UserRow";
  }
```

After restarting the application you should be able to create a new user and when saving the new user they should be displayed as the first item of the table:

<figure><img src="../.gitbook/assets/image (8).png" alt=""><figcaption></figcaption></figure>

{% hint style="success" %}
Lab-1 Checkpoint 4\
If you are stuck you can resume at this checkpoint with:&#x20;

`git checkout tags/lab-1-checkpoint-4 -b current_lab`
{% endhint %}

This was Lab 1, you should now feel confident to use server-side rendering with Spring Boot and JTE and be able to create an interactive application using htmx.
