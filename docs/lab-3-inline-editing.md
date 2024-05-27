# Lab 3: Inline Editing

In this lab, we will create a group Management Page where we can add a user to a group and a navigation bar

### Group Management

We start by creating a GroupManagementComponent ViewComponent in the `de.tschuehly.easy.spring.auth.group.management` package:

{% code title="GroupManagementComponent.java" %}
```java
@ViewComponent
public class GroupManagementComponent {
  public static final String MODAL_CONTAINER_ID = "modalContainer";
  public static final String CLOSE_MODAL_EVENT = "close-modal";

  public record GroupManagementContext() implements ViewContext {  }

  public ViewContext render() {
    return new GroupManagementContext();
  }
}
```
{% endcode %}

The template is the same as the `UserManagementComponent.jte` but we added two `<a>` links to the `<nav>` element.

<pre class="language-html" data-title="GroupManagementComponent.jte"><code class="lang-html"><strong>@import static de.tschuehly.easy.spring.auth.group.management.GroupManagementComponent.CLOSE_MODAL_EVENT
</strong>@import static de.tschuehly.easy.spring.auth.group.management.GroupManagementComponent.MODAL_CONTAINER_ID
@import de.tschuehly.easy.spring.auth.group.management.GroupManagementComponent.GroupManagementContext
@param GroupManagementContext groupManagementContext
&#x3C;html lang="en">

&#x3C;head>
    &#x3C;title>Easy Spring Auth&#x3C;/title>
    &#x3C;link rel="stylesheet" href="/css/sakura.css" type="text/css">
    &#x3C;script src="/htmx_1.9.11.js">&#x3C;/script>
    &#x3C;script src="/htmx_debug.js">&#x3C;/script>
    &#x3C;script src="http://localhost:35729/livereload.js">&#x3C;/script>
&#x3C;/head>
&#x3C;body hx-ext="debug">
&#x3C;nav>
    &#x3C;h1>
        Easy Spring Auth
    &#x3C;/h1>
    &#x3C;a href="/">UserManagement&#x3C;/a>
    &#x3C;a href="/group-management">GroupManagement&#x3C;/a>
    &#x3C;hr>
&#x3C;/nav>
&#x3C;main>

&#x3C;/main>
&#x3C;/body>
&#x3C;div id="${MODAL_CONTAINER_ID}" hx-on:$unsafe{CLOSE_MODAL_EVENT}="this.innerHTML = null">

&#x3C;/div>
&#x3C;/html>
</code></pre>

***

Next, we will create a `GroupTableComponent` in the `de.tschuehly.easy.spring.auth.group.management.table` package.

We autowire the `groupService` and create a `GROUP_TABLE_ID` constant.

{% code title="GroupTableComponent.java" %}
```java
@ViewComponent
public class GroupTableComponent {

  private final GroupService groupService;

  public final static String GROUP_TABLE_ID = "groupTable";

  public record GroupTableContext() implements ViewContext { }

  public ViewContext render() {
    return new GroupTableContext();
  }
  
  public GroupTableComponent(GroupService groupService) {
    this.groupService = groupService;
  }
}
```
{% endcode %}

We add the corresponding template `GroupTableComponent.jte` and set the `<table>` id to `${GROUP_TABLE_ID}`

{% code title="GroupTableComponent.jte" %}
```html
@import static de.tschuehly.easy.spring.auth.group.management.table.GroupTableComponent.*
@param de.tschuehly.easy.spring.auth.group.management.table.GroupTableComponent.GroupTableContext groupTableContext
<table id="${GROUP_TABLE_ID}">
    <thead>
    <tr>
        <th>
            Group Name
        </th>
        <th>
            Group Members
        </th>
        <th>

        </th>
    </tr>
    </thead>
    <tbody>
    </tbody>
</table>
```
{% endcode %}

Now back in the `GroupTableComponent.java`, we retrieve all groups with the `groupService.getAll()` method and add this List of groups to the ViewContext

{% code title="GroupTableComponent.java" %}
```java
@ViewComponent
public class GroupTableComponent {
  private final GroupService groupService;

  public record GroupTableContext(List<EasyGroup> groupList) 
    implements ViewContext{}

  public final static String GROUP_TABLE_ID = "groupTable";
  public ViewContext render(){
    List<EasyGroup> groupList = groupService.getAll();
    return new GroupTableContext(groupList);
  }

  public GroupTableComponent(GroupService groupService) {
    this.groupService = groupService;
  }
}
```
{% endcode %}

Now we need to replace the `<tbody>` element of the `GroupTableComponent.jte` with the following:

<pre class="language-markup" data-title="GroupTableComponent.jte"><code class="lang-markup"><strong>&#x3C;tbody>
</strong>@for(var group: groupTableContext.groupList()) &#x3C;%-- (1) --%>
    &#x3C;tr>
        &#x3C;td>
            ${group.groupName} &#x3C;%-- (2) --%>
        &#x3C;/td>
        &#x3C;td>
            @for(var member: group.memberList) &#x3C;%-- (3) --%>
                &#x3C;span>${member.username}&#x3C;/span> &#x3C;%-- (4) --%>
            @else
                &#x3C;span>no member&#x3C;/span> &#x3C;%-- (5) --%>
            @endfor
        &#x3C;/td>
    &#x3C;/tr>
@endfor
&#x3C;/tbody>
</code></pre>

**(1):** We loop over the `groupTableContext.groupList()` variable with the `@for` syntax

**(2):** We show the `groupName` in a `<td>`

**(3):** We loop over the `group.memberList`

**(4):** We show the username in a `<span>`

**(5):** If the group has no users we show a `no member` message

***

Then we render the GroupTableComponent in the GroupManagementComponent. We autowire it and pass it into the ViewContext

{% code title="GroupManagementComponent.java" %}
```java
@ViewComponent
public class GroupManagementComponent {
  private final GroupTableComponent groupTableComponent;

  public static final String MODAL_CONTAINER_ID = "modalContainer";
  public static final String CLOSE_MODAL_EVENT = "close-modal";

  public GroupManagementComponent(GroupTableComponent groupTableComponent) {
    this.groupTableComponent = groupTableComponent;
  }

  public record GroupManagementContext(ViewContext viewContext) 
      implements ViewContext{}

  public ViewContext render(){
    return new GroupManagementContext(groupTableComponent.render());
  }
}
```
{% endcode %}

In the `GroupManagementComponent.jte` template we render the `GroupTableComponent` it in the `<main>` element, by using the `viewContext`

{% code title="GroupManagementComponent.jte" %}
```html
<main>
    ${groupManagementContext.viewContext()}
</main>
```
{% endcode %}

Now we need to add the `/group-management` endpoint to the `GroupController`:

{% code title="GroupController.java" %}
```java
@Controller
public class GroupController {
    private final GroupService groupService;
    private final GroupManagementComponent groupManagementComponent; // (1)

    public GroupController(GroupService groupService, GroupManagementComponent groupManagementComponent) {
        this.groupService = groupService; 
        this.groupManagementComponent = groupManagementComponent; // (1)
    }

    @GetMapping("/group-management") // (2)
    public ViewContext groupManagementComponent() {
        return groupManagementComponent.render(); // (3)
    }
}
```
{% endcode %}

**(1):** We autowire the `GroupManagementComponent`

**(2):** We create a new `@GetMapping`

**(3):** We return the `ViewContext` retrieved by calling the `render()` method

If we now run `Lab3Application.java` and navigate to [localhost:8080/group-management](http://localhost:8080/group-management) to see the rendered groups and members.

<figure><img src="../.gitbook/assets/image (12).png" alt=""><figcaption></figcaption></figure>

{% hint style="success" %}
Lab-3 Checkpoint 1

If you are stuck you can resume at this checkpoint with:

`git checkout tags/lab-3-checkpoint-1 -b lab-3-c1`
{% endhint %}

We haven't done anything new yet, now we are going to start with the inline editing feature.

## Inline Editing

We now want to add a new User to one of the groups.

We autowire the `GroupTableComponent` and create an endpoint in the `GroupController`:&#x20;

{% code title="GroupController.java" %}
```java
public final static String POST_ADD_USER = "/group/{groupName}/add-user"; // (1)
public final static String USER_ID_PARAM = "userId"; // (2)
@PostMapping(POST_ADD_USER)
public ViewContext addUser(@PathVariable String groupName, // (3)
    @RequestParam(USER_ID_PARAM) UUID userId){ // (4)
  groupService.addUserToGroup(groupName,userId); // (5)
  return groupTableComponent.render(); // (6)
}  
```
{% endcode %}

(1): We define a `POST_ADD_USER` constant

(2): We define a `USER_ID_PARAM` constant

(3): We capture the `groupName` via `@PathVariable`

(4): We capture the `userId` via `@RequestParam`&#x20;

(5): We add the user to the group via the `groupService`

(6): We return the `GroupTableComponent.render` which will render the group table with the new user added

Next, we create a `AddUserComponent` in the `de.tschuehly.easy.spring.auth.group.management.table.user` package:

<pre class="language-java" data-title="AddUserComponent.java"><code class="lang-java"><strong>@ViewComponent
</strong>public class AddUserComponent {
  private final UserService userService;

  public AddUserComponent(UserService userService) { // (1)
    this.userService = userService;
  }

  public record AddUserContext(String groupName, List&#x3C;EasyUser> easyUserList) 
      implements ViewContext{}

  public ViewContext render(String groupName){ // (2)
    return new AddUserContext(groupName,userService.findAll()); // (3)
  }
}
</code></pre>

**(1):** We autowire the `UserService`

**(2):** The render method has a `groupName` parameter

**(3):** We pass the groupName and the `userService.findAll` to the `AddUserContext`.

***

Now we create a `AddUserComponent.jte` template in the same package as the `AddUserComponent.java`

{% code title="AddUserComponent.jte" %}
```html
@import static de.tschuehly.easy.spring.auth.group.GroupController.*
@import de.tschuehly.easy.spring.auth.group.management.table.GroupTableComponent
@import de.tschuehly.easy.spring.auth.htmx.HtmxUtil
@param de.tschuehly.easy.spring.auth.group.management.table.user.AddUserComponent.AddUserContext addUserContext
<form hx-post="${HtmxUtil.URI(POST_ADD_USER,addUserContext.groupName())}" <%-- (1) --%>
      hx-target="${HtmxUtil.target(GroupTableComponent.GROUP_TABLE_ID)}" <%-- (2) --%>
      hx-swap="outerHTML"> <%-- (2) --%>
    <select name="${USER_ID_PARAM}">
        @for(var easyUser: addUserContext.easyUserList()) <%-- (3) --%>
            <option value="${easyUser.uuid.toString()}">
                ${easyUser.username}
            </option>
        @endfor
    </select>
    <button type="submit">Add User to group</button>
</form>
```
{% endcode %}

**(1):** We create a `<form>` element add an `hx-post` attribute that targets the `POST_ADD_USER` endpoint and inserts the `groupName` in the ViewContext into the Endpoint URI using the `HtmxUtil`

**(2):** We target the `GROUP_TABLE_ID` and swap the `outerHTML` of the target element.

**(3):** We create a `<select>` and use the `@for` loop syntax to create an option element for each user.

{% hint style="info" %}
As you can see in contrast to the `rerender` method of the `UserRowComponent` here the htmx logic is in the template.
{% endhint %}

***

We now autowire the `AddUserComponent` and create a `GET_SELECT_USER` endpoint in the `GroupController`

{% code title="GroupController.java" %}
```java
public final static String GET_SELECT_USER = "/group/{groupName}/select-user";

@GetMapping(GET_SELECT_USER)
public ViewContext selectUser(@PathVariable String groupName) {
  return addUserComponent.render(groupName);
}
```
{% endcode %}

Back to the `GroupTableComponent.jte` we add a static import to `GET_SELECT_USER` and `HtmxUtil`and add a new `<td>` in the `@for` loop.

{% code title="GroupTableComponent.jte" lineNumbers="true" fullWidth="true" %}
```html
@import static de.tschuehly.easy.spring.auth.group.management.table.GroupTableComponent.*
@import static de.tschuehly.easy.spring.auth.group.GroupController.GET_SELECT_USER
@import de.tschuehly.easy.spring.auth.htmx.HtmxUtil
@param de.tschuehly.easy.spring.auth.group.management.table.GroupTableComponent.GroupTableContext groupTableContext
<table id="${GROUP_TABLE_ID}">
    <thead>
    <tr>
        <th>
            Group Name
        </th>
        <th>
            Group Members
        </th>
        <th>

        </th>
    </tr>
    </thead>
    <tbody>
    @for(var group: groupTableContext.groupList())
        <tr>
            <td>
                ${group.groupName}
            </td>
            <td>
                @for(var member: group.memberList)
                    <span>${member.username}</span>
                @else
                    <span>no member</span>
                @endfor
            </td>
            <td>
                <button hx-get="${HtmxUtil.URI(GET_SELECT_USER,group.groupName)}" <%-- (1) --%>
                        hx-swap="outerHTML"> <%-- (2) --%>
                    <img src="/plus.svg">
                </button>
            </td>
        </tr>
    @endfor
    </tbody>
</table>
```
{% endcode %}

**(1):** We create a `<button>` element that has a `hx-get` attribute that creates a GET request to `/group/groupName/select-user`

**(2):** We swap the outerHTML of the target element. As we didn't set the `hx-target`we replace the `<button>` element.

Now restart the application and navigate to [localhost:8080/group-management](http://localhost:8080/group-management).

We can click on the plus and see the selector to add a User to the group. When clicking on `Add User to group` the table is rerendered with the updated value.

<figure><img src="../.gitbook/assets/image (13).png" alt=""><figcaption></figcaption></figure>

{% hint style="success" %}
Lab-3 Checkpoint 2

If you are stuck you can resume at this checkpoint with:

`git checkout tags/lab-3-checkpoint-1 -b lab-3-c2`
{% endhint %}
