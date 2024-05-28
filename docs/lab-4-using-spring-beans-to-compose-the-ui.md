# Lab 4: Using Spring Beans to Compose the UI

As you have seen in Lab 3 we have a lot of duplication between the `UserManagementComponent` and the `GroupManagementComponent` ViewComponent.\\

We start by creating an`LayoutComponent` in `de.tschuehly.easy.spring.auth.web.layout`. We have a ViewContext parameter and the modal-related constants.

{% code title="LayoutComponent.java" %}
```java
@ViewComponent
public class LayoutComponent {

  public static final String MODAL_CONTAINER_ID = "modalContainer";
  public static final String CLOSE_MODAL_EVENT = "close-modal";

  public record LayoutContext(ViewContext content) 
    implements ViewContext {}

  public ViewContext render(ViewContext content) {
    return new LayoutContext(content);
  }
}
```
{% endcode %}

In the template, we define the shared HTML between the pages.

{% code title="LayoutComponent.jte" %}
```html
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
{% endcode %}

Now we can use the LayoutComponent in the `GroupController` and `UserController`.

We autowire the `LayoutComponent` and the `GroupTableComponent` and call their render method in the endpoint method:

{% code title="GroupController.java" %}
```java
public static final String GROUP_MANAGEMENT =  "/group-management";

@GetMapping(GROUP_MANAGEMENT)
public ViewContext groupManagementComponent(){
  return layoutComponent.render(groupTableComponent.render());
}
```
{% endcode %}

In the `UserController` we autowire the `LayoutComponent` and the `UserTableComponent` and call their render methods in the endpoint method.

{% code title="UserController.java" %}
```java
public static final String USER_MANAGEMENT_PATH = "/";

@GetMapping(USER_MANAGEMENT_PATH)
public ViewContext userManagementComponent() {
  return layoutComponent.render(userTableComponent.render());
}

```
{% endcode %}

We can now navigate to [localhost:8080](http://localhost:8080/) and [localhost:8080/group-management](http://localhost:8080/group-management) and both pages still work.

{% hint style="success" %}
Lab-4 Checkpoint 1

If you are stuck you can resume at this checkpoint with:

`git checkout tags/lab-4-checkpoint-1 -b lab-4-c1`
{% endhint %}

But as we now see, there is no longer a navigation bar. \
The `UserManagementComponent` and the `GroupManagementComponent` are not used anymore. Instead, we can use them to define the page links displayed in the Navigation Bar.

We start by creating an `Page` interface in `de.tschuehly.easy.spring.auth.web`. We define a NavigationItem record and a navigationItem method.

{% code title="Page.java" %}
```java
public interface Page {
  record NavigationItem(String displayName, String URI){}

  NavigationItem navigationItem();
}
```
{% endcode %}

We can now slim down the `UserManagementComponent` that defines the NavigationItem and the path to the Endpoint. We can also delete the `UserManagementComponent.jte` template.

{% code title="UserManagementComponent.java" %}
```java
@Component
public class UserManagementComponent implements Page {

  @Override
  public NavigationItem navigationItem() {
    return new NavigationItem("User Management", UserController.USER_MANAGEMENT_PATH);
  }
}
```
{% endcode %}

{% hint style="warning" %}
We now need to fix `CLOSE_MODAL_EVENT` and the `MODAL_CONTAINER_ID` in the `UserRowComponent` and `UserTableComponent` .

We replace the incorrect imports with imports to the `LayoutComponent`&#x20;
{% endhint %}

Now in the `LayoutComponent`, we can use Autowiring to get all Pages as a List and aggregate all `NavigationItems` into a List and pass it into the ViewContext

{% code title="LayoutComponent.java" %}
```java
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
{% endcode %}

In the `LayoutComponent.jte` template we can now show a link for each page defined in the Spring ApplicationContext.\
Replace the `<nav>` element with the following.

{% code title="LayoutComponent.jte" %}
```html
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
{% endcode %}

We can now navigate to [localhost:8080](http://localhost:8080/) and see that the navigation works again!

<figure><img src="../.gitbook/assets/image (1) (1) (1) (1) (1) (1).png" alt=""><figcaption></figcaption></figure>

{% hint style="success" %}
Lab-4 Checkpoint 2

If you are stuck you can resume at this checkpoint with:

`git checkout tags/lab-4-checkpoint-2 -b lab-4-c2`
{% endhint %}

***

Now we need to add the `GroupManagementComponent` page back to our navigation.&#x20;

We delete the `GroupMangament.jte` template and change the `GroupManagementComponent.java` :

{% code title="GroupManagementComponent.java" %}
```java
@Component
public class GroupManagementComponent implements Page {

  @Override
  public NavigationItem navigationItem() {
    return new NavigationItem("Group Management", GroupController.GROUP_MANAGEMENT);
  }
}
```
{% endcode %}

And now the GroupManagementComponent is back!

<figure><img src="../.gitbook/assets/image (2) (1) (1) (1).png" alt=""><figcaption></figcaption></figure>

The nice thing is that the navigation bar doesn't know that Page exists. If we change the URL of any page it all still works.

***

But what if we want to show the GroupManagementComponent as the first element? Well, we can use a native Spring Framework Annotation!

With the `@Order` annotation we can define where the navigation element is shown:

{% code title="GroupManagementComponent.java" %}
```java
@Component
@Order(1)
public class GroupManagementComponent implements Page {

  @Override
  public NavigationItem navigationItem() {
    return new NavigationItem("Group Management", GroupController.GROUP_MANAGEMENT);
  }
}
```
{% endcode %}

Group Management is now the first Navigation Item!

<figure><img src="../.gitbook/assets/image (3) (1).png" alt=""><figcaption></figcaption></figure>

{% hint style="success" %}
Lab-4 Checkpoint 3

If you are stuck you can resume at this checkpoint with:

`git checkout tags/lab-4-checkpoint-3 -b lab-4-c3`
{% endhint %}
