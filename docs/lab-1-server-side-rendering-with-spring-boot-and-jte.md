# Lab 1: Server-side rendering with Spring Boot and JTE

This lab aims to build a simple user management application with Spring Boot and htmx.

We are using [JTE](https://jte.gg) as the server-side template language.

## Display Users

We want to display a table of users like this:

<figure><img src="../.gitbook/assets/image (2) (1).png" alt=""><figcaption></figcaption></figure>

### UserController

First, navigate to the `UserController` in `de.tschuehly.easy.spring.auth.user` in the `lab-1` folder and change it to the following.

{% code title="UserController.java" %}
```java
package de.tschuehly.easy.spring.auth.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {
  public static final String MODAL_CONTAINER_ID = "modalContainer"; // (1)
  public static final String USER_TABLE_BODY_ID = "userTableBody"; // (1)

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/") // (2)
  public String index(Model model) {
    model.addAttribute("easyUserList", userService.findAll()); // (3)
    return "UserManagement";
  }  
}
```
{% endcode %}

**(1):** We define a constant for the `USER_TABLE_BODY_ID` and the `MODAL_CONTAINER_ID`

**(2):** We create a new `@GetMapping` to the `/` path.

**(3):** Inside the method, we call the `userService.findAll()` function and add it to the MVC model as `easyUserList` attribute.

**(4):** We return the string `UserManagement` . This is the reference to the View we want to render.

{% hint style="info" %}
Spring MVC will look for templates in the `src/main/jte` directory as we use the `jte-spring-boot-starter-3.`

You can find all the templates we need already there. We now need to fill them with life.
{% endhint %}

### UserMangament

We start with the `UserManagement.jte` template.&#x20;

As you can see, a barebones html structure is already in place with CSS and htmx referenced.

We need to import all static variables defined in UserController with the `@import` JTE syntax at the top of the file and we add the easyUserList we defined earlier in the model as a parameter to the template with `@param` after the imports.

{% code title="UserManagement.jte " %}
```crystal
@import static de.tschuehly.easy.spring.auth.user.UserController.*
@import de.tschuehly.easy.spring.auth.user.EasyUser
@import java.util.List
@param List<EasyUser> easyUserList
```
{% endcode %}

There is already a basic HTML Table in place, that we need to fill with our data.

We replace the table body element with the following:

{% code title="UserManagement.jte " %}
```html
<tbody id="${USER_TABLE_BODY_ID}"> <%-- (1) --%>
@for(var user: easyUserList) <%-- (2) --%>
    @template.UserRow(easyUser = user) <%-- (3) --%>
@endfor
</tbody>
```
{% endcode %}

**(1):** We set the id of the `tbody` to  `USER_TABLE_BODY_ID` to reference it statically from other places.

&#x20;**(2):** We loop over the easyUserList with the `@for` JTE syntax

**(3):** Then in the loop body we call the userRow.jte template with the `@template` syntax and pass the `user` loop variable into the template.

We also add an empty `<div>` after the `</body>` element with the `id` set to `MODAL_CONTAINER_ID` to show a modal&#x20;

{% code title="UserManagement.jte" %}
```html
<div id="${MODAL_CONTAINER_ID}"></div>
```
{% endcode %}

Your `UserManagement.jte` template should now look like this:&#x20;

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
        <tbody id="${USER_TABLE_BODY_ID}">
        @for(var user: easyUserList)
            @template.UserRow(easyUser = user)
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

In the `UserRow.jte` template we define an EasyUser parameter and a local variable with the exclamation mark JTE expression: `!{var name = value}`  at the top of the file.

{% code title="UserRow.jte" %}
```crystal
@import de.tschuehly.easy.spring.auth.user.EasyUser
@param EasyUser easyUser
!{var uuid = easyUser.uuid.toString();}
```
{% endcode %}

We then add the `uuid` of the user as id to the `<tr>` element and add a `<td>`element for the uuid, username and password and display the value of the user.

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
    </td>
</tr>
```
{% endcode %}

We can see all currently defined users if we start the application and navigate to [http://localhost:8080](http://localhost:8080/).

<figure><img src="../.gitbook/assets/image (4).png" alt=""><figcaption></figcaption></figure>

{% hint style="success" %}
Lab-1 Checkpoint 1

If you are stuck you can resume at this checkpoint with:&#x20;

`git checkout tags/lab-1-checkpoint-1 -b lab-1-c1`
{% endhint %}

## Edit users

Next, we want to edit a user that has already been created and change his username.

### UserController

First, we need to create an HTTP endpoint that will display the `EditUserForm`. Add this to the `UserController`:

{% code title="UserController.java" %}
```java
public record UserForm(String uuid, String username, String password) {} // (1)
  
public static final String GET_EDIT_USER_MODAL = "/save-user/modal/{uuid}"; // (2)
  
@GetMapping(GET_EDIT_USER_MODAL)
public String editUserModal(Model model, @PathVariable UUID uuid) {
  var user = userService.findById(uuid);
  model.addAttribute("userForm", 
    new UserForm(user.uuid.toString(), user.username, user.password)); // (3)
  return "EditUserForm";
}
```
{% endcode %}

**(1):** We create a UserForm record that represents the user.&#x20;

**(2):** We create a static constant `GET_EDIT_USER_MODAL` for the HTTP Endpoint. This makes it easy to understand what controller mappings htmx sends requests to.

**(3):** We retrieve the data from the datastore and add it to the model, using the record we just defined.

### UserRow

In the `UserRow.jte` we add a new `<td>` element and create a button element.

{% code title="UserRow.jte" %}
```html
@import de.tschuehly.easy.spring.auth.user.EasyUser
@import static de.tschuehly.easy.spring.auth.htmx.HtmxUtil.*
@import static de.tschuehly.easy.spring.auth.user.UserController.*

@param EasyUser easyUser
!{var uuid = easyUser.uuid.toString();}
<tr id="user-${uuid}">
    <td>
        ${uuid}
    </td>
    <td>
        ${easyUser.username}
    </td>
    <td>
        ${easyUser.password}
    </td>
    <td>
        <button hx-get="${URI(GET_EDIT_USER_MODAL,uuid)}" <%-- (1) --%>
                hx-target="#${MODAL_CONTAINER_ID}"> <%-- (2) --%>
            <img src="/edit.svg">
        </button>
    </td>
</tr>
```
{% endcode %}

**(1):** `hx-get="${URI(GET_EDIT_USER_MODAL,uuid)}` creates an HTTP get request to `/user/edit/{uuid}` when the button element is clicked.&#x20;

{% hint style="info" %}
We use the `HtmxUtil.URI()` method, that creates a `UriTemplate` and fills it with the variables we pass.
{% endhint %}

**(2):** `hx-target="#${MODAL_CONTAINER_ID}"` tells HTMX to swap the response body with the element where the id equals `modalContainer` . We created this earlier in the `UserManagement.jte` template

### EditUserForm

In the corresponding `EditUserForm.jte` template we display the values in a `<form>` element:

{% code title="EditUserForm.jte" %}
```html
@param de.tschuehly.easy.spring.auth.user.UserController.UserForm userForm

<div style="width: 100dvw; height: 100dvh; position: fixed; top: 0;left: 0; background-color: rgba(128,128,128,0.69); display: flex; justify-content: center; align-items: center;">
    <form style="background-color: whitesmoke; padding: 2rem;">
        <label>
            UUID
            <input type="text" readonly name="uuid" value="${userForm.uuid()}">
        </label>
        <label>
            Username
            <input type="text" name="username" value="${userForm.username()}">
        </label>
        <label>
            Password
            <input type="text" name="password" value="${userForm.password()}">
        </label>
    </form>
</div>
```
{% endcode %}

Now, restart the application, navigate to [http://localhost:8080](http://localhost:8080/), and click the edit button. You should now see the modal popup and the values of the user displayed.

<figure><img src="../.gitbook/assets/image (5).png" alt=""><figcaption></figcaption></figure>

{% hint style="success" %}
Lab-1 Checkpoint 2

If you are stuck you can resume at this checkpoint with:&#x20;

`git checkout tags/lab-1-checkpoint-2 -b lab-1-c2`
{% endhint %}

## Save the user

We now want to change a value, save it to the datastore and display the updated value in the table. We create a new endpoint in the `UserController.java`.

We create a new endpoint and call the `userService.saveUser()` endpoint, then we add the new user value to the model and return the UserRow template:

{% code title="UserController.java" %}
```java
public static final String POST_SAVE_USER = "/save-user"; // (1)

@PostMapping(POST_SAVE_USER) // (2)
public String saveUser(UUID uuid, String username, String password, Model model) {
  EasyUser user = userService.saveUser(uuid, username, password); // (3)
  model.addAttribute("easyUser", user);
  return "UserRow"; // (4)
}
```
{% endcode %}

(1): We create a constant `POST_SAVE_USER`

(2): We create a new `@PostMapping` endpoint

(3): We save the user and add it to the model

(4): We return the `UserRow.jte` template

We then change the `EditUserForm.jte` template:

{% code title="EditUserForm.jte" %}
```html
@import static de.tschuehly.easy.spring.auth.user.UserController.POST_SAVE_USER 
@param de.tschuehly.easy.spring.auth.user.UserController.UserForm userForm

<div style="width: 100dvw; height: 100dvh; position: fixed; top: 0;left: 0; background-color: rgba(128,128,128,0.69); display: flex; justify-content: center; align-items: center;">
    <form style="background-color: whitesmoke; padding: 2rem;">
        <label>
            UUID
            <input type="text" readonly name="uuid" value="${userForm.uuid()}">
        </label>
        <label>
            Username
            <input type="text" name="username" value="${userForm.username()}">
        </label>
        <label>
            Password
            <input type="text" name="password" value="${userForm.password()}">
        </label>
        <button type="submit" hx-post="${POST_SAVE_USER}"> <%-- (1) --%>
            Save User
        </button>
    </form>
</div>
```
{% endcode %}

We import the `POST_SAVE_USER` constant at the top of the file with `@import`

(1): We create an `HTTP POST` request to the `POST_SAVE_USER`endpoint by adding a button with an `hx-post` attribute.

{% hint style="info" %}
We can also place the `hx-post` attribute on the form, as the button would trigger a form submit, that htmx catches.
{% endhint %}

If you restart the app now you will see that the table value is not updated.

Instead, the table row is rendered inside the button, because htmx by default swaps the innerHTML of the element that created the request.

<figure><img src="../.gitbook/assets/image (6).png" alt=""><figcaption></figcaption></figure>

### UserController

To fix this, we can return htmx attributes as HTTP Response headers in the `UserController`

{% code title="UserController.java" %}
```java
public static final String CLOSE_MODAL_EVENT = "close-modal";

@PostMapping(POST_SAVE_USER)
public String saveUser(UUID uuid, String username, String password, Model model, HttpServletResponse response) {
  EasyUser user = userService.saveUser(uuid, username, password);
  model.addAttribute("easyUser", user);
  response.addHeader("HX-Retarget", "#user-" + user.uuid); // (1)
  response.addHeader("HX-Reswap", "outerHTML");
  response.addHeader("HX-Trigger", CLOSE_MODAL_EVENT);
  return "UserRow";
}
```
{% endcode %}

**(1):** We add `HX-Retarget = #user-${user.uuid}`to target the table row \<tr> element that contains the user we just edited.\
**(2):** `HX-Reswap = outerHTML` tells htmx to swap the whole table row.

**(3):** `HX-Trigger = close-modal` tells htmx to trigger a JavaScript event `close-modal` in the browser when the HTTP response is received.

### UserMangement

We now listen to the `MODAL_CONTAINER_ID` event  in the`UserManagement.jte`template.

{% code title="UserManagement.jte" %}
```html
<div id="${MODAL_CONTAINER_ID}" 
     hx-on:$unsafe{CLOSE_MODAL_EVENT}="this.innerHTML = null">  <%-- (1) --%>
</div>
```
{% endcode %}

**(1):** We can use `hx-on` and the JTE `$unsafe` syntax to set the innerHTML to null and remove the modal.

### We can save a user!

If we click the `Save User` button and go to Chrome DevTools we see Hypermedia as the Engine of Application State (HATEOAS) in action.\
The new application state after saving the user is transferred via HTML to the browser, and the new row also includes the link to get the modal it was just called from.

<figure><img src="../.gitbook/assets/image (7).png" alt=""><figcaption></figcaption></figure>

{% hint style="success" %}
Lab-1 Checkpoint 3

If you are stuck you can resume at this checkpoint with:&#x20;

`git checkout tags/lab-1-checkpoint-3 -b lab-1-c3`
{% endhint %}

**This is the end of lab 1.** If you are ahead of the others you can do the bonus exercise below

## Bonus Exercise

### Create User

As the next step, we want to create a new User.

To start we create a new endpoint `GET_CREATE_USER_MODAL` where we return the `CreateUserForm` template in the `UserController`

{% code title="UserController.java" %}
```java
public static final String GET_CREATE_USER_MODAL = "/create-user/modal";

@GetMapping(GET_CREATE_USER_MODAL)
public String getCreateUserModal() {
  return "CreateUserForm";
}
```
{% endcode %}

We replace the `<tfoot>` element of the `UserManagement`, adding a button element that creates a GET request to the `GET_CREATE_USER_MODAL` endpoint and targeting the `MODAL_CONTAINER_ID`

{% code title="UserManagement.jte" %}
```html
<tfoot>
  <tr>
    <td colspan="4">
        <button hx-get="${GET_CREATE_USER_MODAL}" <%-- (1) --%>
                hx-target="#${MODAL_CONTAINER_ID}"> <%-- (2) --%>
            Create new User
        </button>
    </td>
  </tr>
</tfoot>
```
{% endcode %}

We now need to create a `POST_CREATE_USER` endpoint in the `UserController`:

{% code title="UserController.java" %}
```java
public static final String POST_CREATE_USER = "/create-user";

@PostMapping(POST_CREATE_USER)
public String createUser(String username, String password, Model model, HttpServletResponse response) {
  EasyUser user = userService.createUser(username, password);
  model.addAttribute("easyUser", user);

  response.addHeader("HX-Retarget", "#" + USER_TABLE_BODY_ID); // (1)
  response.addHeader("HX-Reswap", "afterbegin"); // (2)
  response.addHeader("HX-Trigger", CLOSE_MODAL_EVENT); // (3)
  return "UserRow"; // (4)
}
```
{% endcode %}

It follows the same pattern as the `POST_SAVE_USER` endpoint.

**(1):** We target the `<body>` element using `HX-Retarget: #USER_TABLE_BODY_ID`&#x20;

**(2):** We use `HX-Reswap: afterbegin` to insert the response's content as the target element's first child.

**(3):** We trigger the `CLOSE_MODAL_EVENT`&#x20;

**(4):** Finally we return the `UserRow.jte` template.

### CreateUserForm

Then we create a `<form>` element in the `CreateUserForm.jte` template: &#x20;

{% code title="CreateUserForm.jte" %}
```html
@import static de.tschuehly.easy.spring.auth.user.UserController.POST_CREATE_USER
<div style="width: 100dvw; height: 100dvh; position: fixed; top: 0;left: 0; background-color: rgba(128,128,128,0.69); display: flex; justify-content: center; align-items: center;">
    <form style="background-color: whitesmoke; padding: 2rem;"
          hx-post="${POST_CREATE_USER}"> <%-- (1) --%>
        <label>
            Username
            <input type="text" name="username">
        </label>
        <label>
            Password
            <input type="text" name="password">
        </label>
        <button type="submit"> <%-- (2) --%>
            Save User
        </button>
    </form>
</div>
```
{% endcode %}

(1): This time we have the hx-post attribute on the `<form>` element&#x20;

(2): We trigger the HTTP request with the `<button type="submit">`

### Sucess!

After restarting the application you should be able to create a new user and when saving the new user they should be displayed as the first item of the table:

<figure><img src="../.gitbook/assets/image (8).png" alt=""><figcaption></figcaption></figure>

{% hint style="success" %}
Lab-1 Checkpoint 4\
If you are stuck you can resume at this checkpoint with:&#x20;

`git checkout tags/lab-1-checkpoint-4 -b lab-1-c4`
{% endhint %}

This was Lab 1, you should now feel confident to use server-side rendering with Spring Boot and JTE and be able to create an interactive application using htmx.
