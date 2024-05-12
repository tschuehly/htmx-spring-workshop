# Lab 3: Inline Editing

In this lab, we will create a group Management Page where we can add a user to a group and a navigation bar

### Group Management

We start by creating a GroupManagement ViewComponent in `auth.group.management`

```java
// GroupManagement.java
@ViewComponent
public class GroupManagement {
  public static final String MODAL_CONTAINER_ID = "modalContainer";
  public static final String CLOSE_MODAL_EVENT = "close-modal";

  public record GroupManagementContext() implements ViewContext {

  }

  public ViewContext render() {
    return new GroupManagementContext();
  }
}
```

The template is the same as the UserManagement but we added two `<a>` links to the `<nav>` element.&#x20;

{% hint style="info" %}
We also need to add these two `<a>` elements to the UserManagement template
{% endhint %}

<pre class="language-html"><code class="lang-html"><strong>// GroupManagement.jte
</strong><strong>@import static de.tschuehly.easy.spring.auth.group.management.GroupManagement.CLOSE_MODAL_EVENT
</strong>@import static de.tschuehly.easy.spring.auth.group.management.GroupManagement.MODAL_CONTAINER_ID
@import de.tschuehly.easy.spring.auth.group.management.GroupManagement.GroupManagementContext
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
    &#x3C;h2>
        Easy Spring Auth
    &#x3C;/h2>
    &#x3C;a href="/">UserManagement&#x3C;/a>
    &#x3C;a href="/groupManagement">GroupManagement&#x3C;/a>
    &#x3C;hr>
&#x3C;/nav>
&#x3C;main>

&#x3C;/main>
&#x3C;/body>
&#x3C;div id="${MODAL_CONTAINER_ID}" hx-on:$unsafe{CLOSE_MODAL_EVENT}="this.innerHTML = null">

&#x3C;/div>
&#x3C;/html>
</code></pre>



Next we will create a GroupTable ViewComponent in `auth.group.management.table.`&#x20;

We autowire the groupService and create a GROUP\_TABLE\_ID constant.

```java
// GroupTableComponent.java
@ViewComponent
public class GroupTableComponent {

  private final GroupService groupService;

  public final static String GROUP_TABLE_ID = "groupTable";

  public record GroupTableContext() implements ViewContext {

  }

  public ViewContext render() {
    return new GroupTableContext();
  }
  
  public GroupTableComponent(GroupService groupService) {
    this.groupService = groupService;
  }
}
```

We add the corresponding template `GroupTableComponent.jte` and set the `<table>` id to `${GROUP_TABLE_ID}`

```html
// GroupTableComponent.jte
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

Now back in the GroupTableComponent we retreive all groups with the `groupService.getAll()` method and add this List of groups to the ViewContext

```java
// GroupTableComponent.java
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

We can then loop over this groupList with the `@for` syntax and then show the groupName and then loop over the memberList of each group and show each member username or a `no member` message.

<pre><code>// GroupTableComponent.jte
<strong>&#x3C;tbody>
</strong>@for(var group: groupTableContext.groupList())
    &#x3C;tr>
        &#x3C;td>
            ${group.groupName}
        &#x3C;/td>
        &#x3C;td>
            @for(var member: group.memberList)
                &#x3C;span>${member.username}&#x3C;/span>
            @else
                &#x3C;span>no member&#x3C;/span>
            @endfor
        &#x3C;/td>
    &#x3C;/tr>
@endfor
&#x3C;/tbody>
</code></pre>

Then we render the GroupTableComponent in the GroupManagement, by autowiring it and passing it into the ViewContext

```java
// GroupManagement.java
@ViewComponent
public class GroupManagement {
  private final GroupTableComponent groupTableComponent;

  public static final String MODAL_CONTAINER_ID = "modalContainer";
  public static final String CLOSE_MODAL_EVENT = "close-modal";

  public GroupManagement(GroupTableComponent groupTableComponent) {
    this.groupTableComponent = groupTableComponent;
  }

  public record GroupManagementContext(ViewContext viewContext) implements ViewContext{}

  public ViewContext render(){
    return new GroupManagementContext(groupTableComponent.render());
  }

}
```

In the GroupManagement template we can render it in the `<main>` element

```html
// GroupManagement.jte
<main>
    ${groupManagementContext.viewContext()}
</main>
```

Now we need to add the path to render the GroupManagement page to the `GroupController`

```java
// GroupController.java
@Controller
public class GroupController {
  //...
  @GetMapping("/groupManagement")
  public ViewContext groupManagement(){
    return groupManagement.render();  
  }
}
```

We can now navigate to [localhost:8080/groupManagement](http://localhost:8080/groupManagement) and see the rendered groups and members.

<figure><img src=".gitbook/assets/image (12).png" alt=""><figcaption></figcaption></figure>

{% hint style="info" %}
Lab-3 Checkpoint 1
{% endhint %}

We didn't do anything new yet, now we are going to start with the inline editing feature.

### Inline Editing

We want to add a new User to one of the groups.&#x20;

We create an endpoint `POST_ADD_USER` that calls the `addUserToGroup` function and then renders the groupTableComponent.

```java
// GroupController.java
@Controller
public class GroupController {
  // ...
  public final static String POST_ADD_USER = "/group/{groupName}/add-user";
  public final static String USER_ID_PARAM = "userId";
  @PostMapping(POST_ADD_USER)
  public ViewContext addUser(@PathVariable String groupName,
      @RequestParam(USER_ID_PARAM) UUID userId){
    groupService.addUserToGroup(groupName,userId);
    return groupTableComponent.render();
  }
}
```

&#x20;Then we create a  `AddUserComponent` .&#x20;

In the render method, we have a `groupName` parameter and pass it and the List of all users into the ViewContext.

<pre class="language-java"><code class="lang-java"><strong>// AddUserComponent.java
</strong><strong>@ViewComponent
</strong>public class AddUserComponent {
  private final UserService userService;

  public AddUserComponent(UserService userService) {
    this.userService = userService;
  }

  public record AddUserContext(String groupName, List&#x3C;EasyUser> easyUserList)
      implements ViewContext{}

  public ViewContext render(String groupName){
    return new AddUserContext(groupName,userService.findAll());
  }
}
</code></pre>

In the template, we create a `<form>` and `<select>` and use the `@for` loop syntax to create an option element for each user.

We create an `hx-post` attribute that targets the `POST_ADD_USER` endpoint and inserts the `groupName` in the ViewContext into the Endpoint URI using the `HtmxUtil`

We target the `GROUP_TABLE_ID` and swap the `outerHTML` of the target element.

```html
@import static de.tschuehly.easy.spring.auth.group.GroupController.*
@param de.tschuehly.easy.spring.auth.group.management.table.user.AddUserComponent.AddUserContext addUserContext
<form hx-post="${HtmxUtil.URI(POST_ADD_USER,addUserContext.groupName())}"
      hx-target="${HtmxUtil.target(GroupTableComponent.GROUP_TABLE_ID)}"
      hx-swap="outerHTML">
    <select>
        @for(var easyUser: addUserContext.easyUserList())
            <option value="${easyUser.uuid.toString()}">
                ${easyUser.username}
            </option>
        @endfor
    </select>
    <button type="submit">Add User to group</button>
</form>
```

{% hint style="info" %}
As you can see in contrast to the `rerender` method of the `UserRowComponent` here the htmx logic is in the template. &#x20;
{% endhint %}

We now add a `GET_SELECT_USER` endpoint to the GroupController

```java
// GroupController.java
@Controller
public class GroupController {
  // ...
  public final static String GET_SELECT_USER = "/group/{groupName}/select-user";

  @GetMapping(GET_SELECT_USER)
  public ViewContext selectUser(@PathVariable String groupName) {
    return addUserComponent.render(groupName);
  }
}
```



Back to the `GroupTableComponent` we add a new `<td>` element with a `<button>` that has a `hx-get` attribute that creates an HTTP GET request to `/group/groupName/select-user`

We also add a static import to `GET_SELECT_USER` and `HtmxUtil`

```html
@import static de.tschuehly.easy.spring.auth.group.GroupController.GET_SELECT_USER
@import de.tschuehly.easy.spring.auth.htmx.HtmxUtil
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
            <button hx-get="${HtmxUtil.URI(GET_SELECT_USER,group.groupName)}" 
                    hx-swap="outerHTML">
                <img src="/plus.svg">
            </button>
        </td>
    </tr>
@endfor
</tbody>
```

We can now navigate to [localhost:8080/groupManagement](http://localhost:8080/groupManagement) and click on the plus, you can see the selector to add a User to the group. When clicking on `Add User to group` the table is rerendered.

<figure><img src=".gitbook/assets/image (13).png" alt=""><figcaption></figcaption></figure>

{% hint style="info" %}
Lab-3 Checkpoint 2
{% endhint %}
