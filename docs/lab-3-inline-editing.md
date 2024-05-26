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

The template is the same as the `UserManagementComponent.jte` but we added two `<a>` links to the `<nav>` element.&#x20;

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
    &#x3C;a href="/">UserManagementComponent&#x3C;/a>
    &#x3C;a href="/group-management">GroupManagementComponent&#x3C;/a>
    &#x3C;hr>
&#x3C;/nav>
&#x3C;main>

&#x3C;/main>
&#x3C;/body>
&#x3C;div id="${MODAL_CONTAINER_ID}" hx-on:$unsafe{CLOSE_MODAL_EVENT}="this.innerHTML = null">

&#x3C;/div>
&#x3C;/html>
</code></pre>

{% hint style="danger" %}
We also need to add these two `<a>` elements to the UserManagementComponent template
{% endhint %}

***

Next, we will create a `GroupTableComponent` in  the `de.tschuehly.easy.spring.auth.group.management.table` package.

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

Now we need to replace the `<tbody>` element of the `GroupTableComponent.jte`

<pre data-title="GroupTableComponent.jte"><code><strong>&#x3C;tbody>
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

Then we render the GroupTableComponent in the GroupManagementComponent, by autowiring it and passing it into the ViewContext

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

In the `GroupManagementComponent.jte` template we can render it in the `<main>` element, by using the `viewContext`&#x20;

{% code title="GroupManagementComponent.jte" %}
```html
<main>
    ${groupManagementContext.viewContext()}
</main>
```
{% endcode %}

Now we need to add the `/group-management` endpoint to render the group management to the `GroupController`:

{% code title="GroupController.java" %}
```java
@GetMapping("/group-management")
public ViewContext groupManagementComponent(){
  return groupManagementComponent.render();
}
```
{% endcode %}

If we now run `Lab3Application.java` and navigate to [localhost:8080/group-management](http://localhost:8080/group-management) we can see the rendered groups and members.

<figure><img src="../.gitbook/assets/image (12).png" alt=""><figcaption></figcaption></figure>

{% hint style="success" %}
Lab-3 Checkpoint 1

If you are stuck you can resume at this checkpoint with:

`git checkout tags/lab-3-checkpoint-1 -b lab-3-c1`
{% endhint %}

We haven't done anything new yet, now we are going to start with the inline editing feature.

## Inline Editing

We now want to add a new User to one of the groups.&#x20;

We create an endpoint `POST_ADD_USER`  in the `GroupController` that calls the `addUserToGroup` function and then renders the groupTableComponent.

{% code title="GroupController.java" %}
```java
public final static String POST_ADD_USER = "/group/{groupName}/add-user";
public final static String USER_ID_PARAM = "userId";
@PostMapping(POST_ADD_USER)
public ViewContext addUser(@PathVariable String groupName,
    @RequestParam(USER_ID_PARAM) UUID userId){
  groupService.addUserToGroup(groupName,userId);
  return groupTableComponent.render();
}  
```
{% endcode %}

&#x20;Now we create a  `AddUserComponent` in the `de.tschuehly.easy.spring.auth.group.management.table.user` package:

<pre class="language-java" data-title="AddUserComponent.java"><code class="lang-java"><strong>@ViewComponent
</strong>public class AddUserComponent {
  private final UserService userService;

  public AddUserComponent(UserService userService) {
    this.userService = userService;
  }

  public record AddUserContext(String groupName, List&#x3C;EasyUser> easyUserList) 
      implements ViewContext{}

  public ViewContext render(String groupName){ // (1)
    return new AddUserContext(groupName,userService.findAll());
  }
}
</code></pre>

(1): The render method has a `groupName` parameter&#x20;

(2): We pass the groupName and the `userService.findAll` to the `AddUserContext`.

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

(1): We create a `<form>` element add an `hx-post` attribute that targets the `POST_ADD_USER` endpoint and inserts the `groupName` in the ViewContext into the Endpoint URI using the `HtmxUtil` &#x20;

(2): We target the `GROUP_TABLE_ID` and swap the `outerHTML` of the target element.

(3): We create a `<select>` and use the `@for` loop syntax to create an option element for each user.

{% hint style="info" %}
As you can see in contrast to the `rerender` method of the `UserRowComponent` here the htmx logic is in the template. &#x20;
{% endhint %}

***

We now add an `GET_SELECT_USER` endpoint to the `GroupController`

{% code title="GroupController.java" %}
```java
public final static String GET_SELECT_USER = "/group/{groupName}/select-user";

@GetMapping(GET_SELECT_USER)
public ViewContext selectUser(@PathVariable String groupName) {
  return addUserComponent.render(groupName);
}
```
{% endcode %}



Back to the `GroupTableComponent.jte` we add a static import to `GET_SELECT_USER` and `HtmxUtil`

{% code title="GroupTableComponent.jte" %}
```java
@import static de.tschuehly.easy.spring.auth.group.GroupController.GET_SELECT_USER
@import de.tschuehly.easy.spring.auth.htmx.HtmxUtil
```
{% endcode %}

We add a new `<td>` as last element of the `<tr>` element.

We create a `<button>` element that has a `hx-get` attribute that creates an GET request to `/group/groupName/select-user`

{% code title="GroupTableComponent.jte" %}
```html
<td>
    <button hx-get="${HtmxUtil.URI(GET_SELECT_USER,group.groupName)}"
            hx-swap="outerHTML">
        <img src="/plus.svg">
    </button>
</td>
```
{% endcode %}

We can now navigate to [localhost:8080/group-management](http://localhost:8080/group-management) and click on the plus.

You can see the selector to add a User to the group. When clicking on `Add User to group` the table is rerendered.

<figure><img src="../.gitbook/assets/image (13).png" alt=""><figcaption></figcaption></figure>

{% hint style="success" %}
Lab-3 Checkpoint 2

If you are stuck you can resume at this checkpoint with:

`git checkout tags/lab-3-checkpoint-1 -b lab-3-c2`
{% endhint %}
