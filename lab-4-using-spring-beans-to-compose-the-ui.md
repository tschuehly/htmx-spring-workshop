# Lab 4: Using Spring Beans to Compose the UI

As you have seen in Lab 3 we have a lot of duplication between the `UserManagement` and the `GroupManagement` ViewComponent.\


We start by creating a shared `LayoutComponent` in `auth.web.layout`. We have a ViewContext parameter and the modal-related constants.

```java
// LayoutComponent.java
@ViewComponent
public class LayoutComponent {

  public static final String MODAL_CONTAINER_ID = "modalContainer";
  public static final String CLOSE_MODAL_EVENT = "close-modal";

  public record LayoutContext(ViewContext content) implements ViewContext {

  }

  public ViewContext render(ViewContext content) {
    return new LayoutContext(content);
  }
}
```

&#x20;In the template, we define the shared HTML between the pages.

```html
// LayoutComponent.jte
@import static de.tschuehly.easy.spring.auth.web.layout.LayoutComponent.CLOSE_MODAL_EVENT
@import static de.tschuehly.easy.spring.auth.web.layout.LayoutComponent.MODAL_CONTAINER_ID
@import de.tschuehly.easy.spring.auth.web.layout.LayoutComponent.LayoutContext
@param LayoutContext layoutContext

<html lang="en">
<head>
    <title>Easy Spring Auth</title>
    <link rel="stylesheet" href="/css/sakura.css" type="text/css">
    <script src="/htmx_1.9.11.js"></script>
    <script src="/htmx_debug.js"></script>
    <script src="http://localhost:35729/livereload.js"></script>
</head>
<body hx-ext="debug">
<nav>
    <h2>
        Easy Spring Auth
    </h2>
</nav>
<main>
    ${layoutContext.content()}
</main>
</body>
<div id="${MODAL_CONTAINER_ID}"
     hx-on:$unsafe{CLOSE_MODAL_EVENT}="this.innerHTML = null">
</div>
</html>
```

Now we can use the LayoutComponent in both the GroupController and UserController:

```java
// GroupController.java
@Controller
public class GroupController {
  // ...
  public static final String GROUP_MANAGEMENT =  "/group-management";
  @GetMapping(GROUP_MANAGEMENT)
  public ViewContext groupManagement(){
    return layoutComponent.render(groupTableComponent.render());
  }
}
```

```java
// UserController.java
@Controller
public class UserController {
  // ...
  public static final String USER_MANAGEMENT_PATH = "/";
  @GetMapping(USER_MANAGEMENT_PATH)
  public ViewContext userManagement() {
    return layoutComponent.render(userTableComponent.render());
  }
}
```

We can now navigate to [localhost:8080](http://localhost:8080/) and [localhost:8080/group-management](http://localhost:8080/group-management) and both pages still work.

{% hint style="success" %}
Lab-4 Checkpoint 1
{% endhint %}

But now we don't have a Navigation Bar anymore and the `UserManagement` and the `GroupManagement` is not used anymore. We can now use them to define the Pages that are displayed in the Navigation Bar.



We start by creating a `Page` interface in `auth.web`. We define a NavigationItem record and a navigationItem method.

```java
// Page.java
public interface Page {
  record NavigationItem(String displayName, String URI){}

  NavigationItem navigationItem();
}
```

We can now slim down the `UserManagement` that defines the NavigationItem and the path to the Endpoint. We can also delete the `UserManagement.jte` template.

```java
@Component
public class UserManagement implements Page {

  @Override
  public NavigationItem navigationItem() {
    return new NavigationItem("User Management", UserController.USER_MANAGEMENT_PATH);
  }
}
```

{% hint style="info" %}
We now need to fix `CLOSE_MODAL_EVENT` and the `MODAL_CONTAINER_ID` in the UserRowComponent and UserTableComponent
{% endhint %}

Now in the `LayoutComponent`, we can use Autowiring to get all Pages as a List and aggregate all `NavigationItems` into a List and pass it into the ViewContext

```java
// LayoutComponent.java
@ViewComponent
public class LayoutComponent {

  private final List<Page> pageList;

  public LayoutComponent(List<Page> pageList) {
    this.pageList = pageList;
  }

  public ViewContext render(ViewContext content) {
    List<NavigationItem> navigationItemList = pageList.stream()
        .map(Page::navigationItem).toList();
    return new LayoutContext(content, navigationItemList);
  }
  
  public record LayoutContext(ViewContext content, List<NavigationItem> navigationItemList) implements ViewContext {

  }
}
```

In the `LayoutComponent.jte` template we can now show a link for each page defined in the Spring ApplicationContext.

```html
// LayoutComponent.jte
<nav>
    <h2>
        Easy Spring Auth
    </h2>
    @for(var nav: layoutContext.navigationItemList())
        <a href="${nav.URI()}">${nav.displayName()}</a>
    @endfor
    <hr>
</nav>
```

We can now navigate to [localhost:8080](http://localhost:8080/) and see that we have our Navigation again!

<figure><img src=".gitbook/assets/image (1) (1) (1) (1) (1).png" alt=""><figcaption></figcaption></figure>

{% hint style="success" %}
Lab-4 Checkpoint 2
{% endhint %}



Now we just need to add the `GroupManagement` page back to our navigation. We delete the GroupMangament.jte template and change the `GroupManagement.java` :

```
// GroupManagement.java
@Component
public class GroupManagement implements Page {

  @Override
  public NavigationItem navigationItem() {
    return new NavigationItem("Group Management", GroupController.GROUP_MANAGEMENT);
  }
}
```

And now the GroupManagement is back!

<figure><img src=".gitbook/assets/image (2) (1) (1) (1).png" alt=""><figcaption></figcaption></figure>

The nice thing is that the Navigation Bar doesn't know that Page even exists. If we change the URL of any page it all still works.



But what if we want to show the GroupManagement as the first element? Well we can use a native Spring Framework Annotation!&#x20;

With the `@Order` annotation we can define where the Navigation element is shown:

```java
@Component
@Order(1)
public class GroupManagement implements Page {

  @Override
  public NavigationItem navigationItem() {
    return new NavigationItem("Group Management", GroupController.GROUP_MANAGEMENT);
  }
}
```

Group Management is now the first Navigation Item!

<figure><img src=".gitbook/assets/image (3) (1).png" alt=""><figcaption></figcaption></figure>

{% hint style="success" %}
Lab-4 Checkpoint 3
{% endhint %}
