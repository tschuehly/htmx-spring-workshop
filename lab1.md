# Lab 1: Server-side rendering with Spring Boot and JTE

This lab aims to build a simple user management application with Spring Boot and htmx.

We are using [JTE](https://jte.gg) as the server-side template language.

### Display a List of Users

We want to display a table of users like this:

<figure><img src=".gitbook/assets/image (2).png" alt=""><figcaption></figcaption></figure>

First, navigate to `UserController.java`  here we will create the endpoint to the UserManagement page and add the data necessary for view rendering:

<figure><img src=".gitbook/assets/image (4).png" alt="" width="295"><figcaption></figcaption></figure>

Create a new `@GetMapping`  and inside the method we call the `userService.findAll()` function and add it to the MVC model.

We also define a constant for the UserTable ID and an ID for a modal container.

```java
@Controller
public class UserController {
  public static final String MODAL_CONTAINER_ID = "modalContainer";
  public static final String USER_TABLE_BODY_ID = "userTableBody";
  
  @GetMapping("/")
  public String index(Model model) {
    model.addAttribute("easyUserList", userService.findAll());
    return "UserManagement";
  }
  // ...
}
```

We return the string `UserManagement` . This is the reference to the View we want to render:

<figure><img src=".gitbook/assets/image (3).png" alt="" width="295"><figcaption></figcaption></figure>

The JTE Spring Boot Starter looks for templates  in `src/main/jte`.&#x20;

You can find all the templates we need already there. We just need to fill them with life.

We start with `UserManagement.jte`, as you can see there is already a barebones html structure in place with css and htmx linked.

We can import all static variables defined in UserController with `@import`&#x20;

```crystal
<!-- UserManagement.jte -->
@import static de.tschuehly.easy.spring.auth.controller.UserController.*
```

We add the easyUserList we defined earlier in the model as a parameter to the template with `@param`.

```crystal
<!-- UserManagement.jte -->
@import de.tschuehly.easy.spring.auth.user.EasyUser
@param List<EasyUser> easyUserList
```

There is already a basic HTML Table in place we need to fill with our data.&#x20;

In the table body, we loop over the easyUserList with the `@for` JTE syntax and call the userRow.jte template with the `@template` syntax and pass the user loop variable into the template.

<pre class="language-html"><code class="lang-html">&#x3C;!-- UserManagement.jte -->
<strong>&#x3C;tbody>
</strong>@for(var user: easyUserList)
    @template.userRow(easyUser = user)
@endfor
&#x3C;/tbody>
</code></pre>

We also add the `public static final String USER_TABLE_BODY_ID` to the `tbody` element to reference it statically from other places.

We also add an empty div with the id set to `MODAL_CONTAINER_ID` to show a modal later.

```html
<!-- UserManagement.jte -->
<table>
    <!-- <thead> omitted -->
    <tbody id="${USER_TABLE_BODY_ID}">
    @for(var user: easyUserList)
        @template.userRow(easyUser = user)
    @endfor
    </tbody>
</table>
<div id="${MODAL_CONTAINER_ID}"></div>
```

#### Display one User as Table Row

The UserRow.jte template defines an EasyUser parameter and a local variable with the exclamation mark JTE expression: `!{var name = value}` .

```crystal
<!-- UserRow.jte -->
@import de.tschuehly.easy.spring.auth.domain.EasyUser
@param EasyUser easyUser
!{var uuid = easyUser.uuid.toString();}
```

We then add the uuid of the user as id to the `<tr>` element and add a `<td>`element for the uuid, username and password and display the value of the user.

```html
<!-- UserRow.jte -->
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



We can see all currently defined users if we start the application and navigate to [localhost:8080](https://localhost:8080).

### Edit users

Next, we want to edit a user that has already been created and change his username.

We create a new GetMapping endpoint in the `UserController`

```java
// UserController.java
public static final String EDIT_USER_MODAL = "/save-user/modal/{uuid}";
@GetMapping(EDIT_USER_MODAL)
public String editUserModal(Model model, @PathVariable UUID uuid) {
  return "EditUserForm";
}
```

As you can see we are using a static constant for the HTTP Endpoint. This makes it easy to understand what controller mappings htmx sends requests to.

In the `UserRow.jte` we add a new `<td>` element and create a button element.

```html
@import static de.tschuehly.easy.spring.auth.htmx.HtmxUtil.*
@import static de.tschuehly.easy.spring.auth.user.UserController.*
<td>
    <button hx-get="${URI(EDIT_USER_MODAL,uuid)}"
            hx-target="#${MODAL_CONTAINER_ID}">
        <img src="/edit.svg">
    </button>
</td>
```

`hx-get="${URI(EDIT_USER_MODAL,uuid)}` creates an HTTP get request to `/user/edit/{uuid}` when the button element is clicked. The uuid variable is interpolated with a static URI method we will define next.&#x20;

We leverage the `URI()` utility function already present in the controller. This method creates a `UriTemplate` and fills it with the variables we pass as vararg.

`hx-target="#${MODAL_CONTAINER_ID}"` tells HTMX to swap the response body with the `div` element we created earlier in the `UserManagement.jte` template

We now have to fill in the corresponding `EditUserForm.jte` template, to display the data that the user currently has.&#x20;

To make this very simple we create a UserForm record:

```
// UserController.java
public record UserForm(String uuid, String username, String password) {

}
```

We retrieve the data from the datastore and add it to the model, using the record we just defined.

```java
// UserController.java
@GetMapping(EDIT_USER_MODAL)
public String editUserModal(Model model, @PathVariable UUID uuid) {
  var user = userService.findById(uuid);
  model.addAttribute("userForm", new UserForm(user.uuid.toString(), user.username, user.password));
  return "EditUserForm";
}
```

In the corresponding `EditUserForm.jte` template we display the values in a `<form>` element:

<pre class="language-html"><code class="lang-html">&#x3C;!-- EditUserForm.jte -->
@param de.tschuehly.easy.spring.auth.user.UserController.UserForm userForm
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

Now let's restart the application, navigate to localhost:8080, and click the edit button. You should now see the modal popup and the values of the user displayed.

### Save edited User

We now want to change a value, save it to the datastore and display the updated value in the table.&#x20;

Let's create a new endpoint and call the `userService.saveUser()`  endpoint&#x20;

```java
// UserController.java
public static final String SAVE_USER = "/save-user";

@PostMapping(SAVE_USER)
public void saveUser(UUID uuid, String username, String password) {
  EasyUser user = userService.saveUser(uuid, username, password);
}
```

We can then call this endpoint by adding a button with an `hx-post` attribute that uses the `SAVE_USER` constant.

{% hint style="info" %}
We can also place the hx- attribute on the form, as the button would trigger a form submit, that htmx catches.
{% endhint %}

<pre><code>&#x3C;!-- EditUserForm.jte -->
<strong>@import static de.tschuehly.easy.spring.auth.user.UserController.SAVE_USER
</strong><strong>&#x3C;form>
</strong><strong>  &#x3C;button type="submit" hx-post="${SAVE_USER}">
</strong>    Save User
  &#x3C;/button>
&#x3C;/form>
</code></pre>

If you restart the app now you will see that the table value is not updated, but instead the label of the button disappears.\


<figure><img src=".gitbook/assets/image (5).png" alt=""><figcaption></figcaption></figure>



To fix this we need to return the table row of the updated user with the new values inserted, we add the new user value to the model and return the UserRow template.&#x20;

But this would render the returned HTML inside the button, because htmx by default swaps the innerHTML of the element that created the request.

```
// UserController.java
@PostMapping(SAVE_USER)
public String saveUser(UUID uuid, String username, String password) {
  EasyUser user = userService.saveUser(uuid, username, password);
  model.addAttribute("easyUser", user);
  return "UserRow";
}
```

To fix this, we can return htmx attributes as HTTP Response headers.&#x20;

We add `HX-Retarget = #user-${user.uuid}`to target the table row \<tr> element that contains the user we just edited.

\
With `HX-Reswap = outerHTML` we tell htmx to swap the whole table row.

We tell htmx to trigger the JavaScript event `CLOSE_MODAL_EVENT` using `HX-Trigger`

```java
// UserController.java
@PostMapping(SAVE_USER)
public String saveUser(UUID uuid, String username, String password) {
  EasyUser user = userService.saveUser(uuid, username, password);
  model.addAttribute("easyUser", user);  
  response.addHeader("HX-Retarget", "#user-" + user.uuid);
  response.addHeader("HX-Reswap", "outerHTML");
  response.addHeader("HX-Trigger", CLOSE_MODAL_EVENT);
  return "UserRow";  
}
```

With `HX-Trigger = close-modal` we tell htmx to trigger a JavaScript event `close-modal` in the browser when the HTTP response is received.

We add an `hx-on:` attribute to clear the innerHTML of the modalContainer to remove the HTML from the DOM when the event is triggered.

If we now listen to this event in the `MODAL_CONTAINER_ID`  element using `hx-on` and use the JTE `$unsafe` syntax, we can set the innerHTML to null and remove the modal.

```
// UserManagement.jte
<div id="${MODAL_CONTAINER_ID}" 
     hx-on:$unsafe{CLOSE_MODAL_EVENT}="this.innerHTML = null">

</div>
```



If we click the `Save User` button and go to Chrome DevTools we can see Hypermedia as the Engine of Application State (HATEOAS) in action.\
The new application state after saving the user is transferred via HTML to the browser, and the new row also includes the link to get the modal that it was just called from.

<figure><img src=".gitbook/assets/image (1).png" alt=""><figcaption></figcaption></figure>

### Create User

As next step we want to create a new User.&#x20;

To start we create a new endpoint `CREATE_USER_MODAL` where we return the CreateUserForm template.

```java
// UserController.java
public static final String CREATE_USER_MODAL = "/create-user/modal";

@GetMapping(CREATE_USER_MODAL)
public String getCreateUserModal() {
  return "CreateUserForm";
}
```

We add this as table footer of the UserManagement table, in a button element and target the `MODAL_CONTAINER_ID`

```html
// UserManagement.jte
@import static de.tschuehly.easy.spring.auth.user.UserController.*
<tfoot>
  <tr>
    <td colspan="4">
        <button hx-get="${CREATE_USER_MODAL}" hx-target="#${MODAL_CONTAINER_ID}">
            Create new User
        </button>
    </td>
  </tr>
</tfoot>
```

The `CreateUserForm.jte` looks like this, as you can see here, this time we have the hx-post attribute on the `<form>` element and trigger the HTTP request with the `<button type="submit">`

```
<form hx-post="${CREATE_USER}">
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

We now need to create the `CREATE_USER` endpoint.

It follows the same pattern as the `SAVE_USER` endpoint, but this time we target the `<body>` element using `HX-Retarget: #USER_TABLE_BODY_ID` and the use `HX-Reswap: afterbegin`&#x20;

`afterbegin` inserts the content of the response as the first child of the target element.

We also trigger the `CLOSE_MODAL_EVENT` and return the `UserRow.jte` template.

```java
public static final String CREATE_USER = "/create-user";
@PostMapping(CREATE_USER)
public String createUser(String username, String password, Model model, HttpServletResponse response) {
  EasyUser user = userService.createUser(username, password);
  model.addAttribute("easyUser", user);

  response.addHeader("HX-Retarget", "#" + USER_TABLE_BODY_ID);
  response.addHeader("HX-Reswap", "afterbegin");
  response.addHeader("HX-Trigger", CLOSE_MODAL_EVENT);
  return "UserRow";
}
```

After restarting the application you now should be able to create a new user and when saving the new user they should be displayed as the first item of the table:

<figure><img src=".gitbook/assets/image (1) (1).png" alt=""><figcaption></figcaption></figure>

This was Lab 1, you should now feel confident to use server-side rendering with Spring Boot and JTE and be able to create an interactive application using htmx.&#x20;
