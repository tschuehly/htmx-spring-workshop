# Lab 8: Exception Messages

The benefit of server-side rendering is that we can leverage Exceptions to display useful information to the user. In this lab, we will catch Exceptions using a Spring Controller Advice and show them to the user in a toast.

We start by creating an InfoException base class in the `auth.web.exception` package:

```java
// InfoException.java
public class InfoException extends RuntimeException{

  public InfoException(String message) {
    super(message);
  }
}
```

Then we create a `UserNotFoundException` that extends the InfoException in the `auth.user` package:

```java
public class UserNotFoundException extends InfoException {

  public UserNotFoundException(String message) {
    super(message);
  }
}
```

This Exception will be thrown when the searchUser methold doesn't find any users.

```java
// UserService.java
public List<EasyUser> searchUser(String searchString) {
  List<EasyUser> easyUsers = easyUserList.stream().filter(
      it -> it.uuid.toString().contains(searchString)
            || it.username.contains(searchString)
            || it.password.contains(
          searchString)
  ).toList();
  if (easyUsers.isEmpty()) {
    throw new UserNotFoundException("No user found with the searchString: \"" + searchString + "\"");
  }
  return easyUsers;
}
```

Now we need to create a container in the `LayoutComponent`. We define a constant TOAST\_CONTAINER\_ID.

```java
// LayoutComponent.java
@ViewComponent
public class LayoutComponent {
  public static final String TOAST_CONTAINER_ID = "toastContainer";
}
```

Then we add a `<div>` element that has the ID set to `TOAST_CONTAINER_ID`&#x20;

```html
// LayoutComponent.jte
@import static de.tschuehly.easy.spring.auth.web.layout.LayoutComponent.TOAST_CONTAINER_ID
</body>
<div id="${TOAST_CONTAINER_ID}">

</div>
</html>
```

Now we create a `MessageComponent` ViewComponent in the `auth.web.message` package.

We also define a `MessageType` enum that has a `severity` method.

```java
// MessageComponent.java
@ViewComponent
public class MessageComponent {

  public ViewContext renderInfoToast(String message) {
    return new MessageContext(message,  MessageType.INFO);
  }

  public ViewContext renderErrorToast(String message) {
    return new MessageContext(message,  MessageType.TOAST_ERROR);
  }

  public record MessageContext(String message, MessageType type) implements
      ViewContext {}

  public enum MessageType {
    TOAST_ERROR,
    NONE,
    INFO;

    public String severity() {
      return switch (this) {
        case TOAST_ERROR -> "error";
        case INFO -> "info";
        default -> "";
      };
    }
  }
}
```

{% hint style="info" %}
In the `severity()` function we could also define conditional CSS to style the toast
{% endhint %}

In the template, we create a `<div>` element that has a `<h2>` element in it that shows the severity and the message of the exception.

We also use the `<button onclick="">` attribute to hide the toast when the user clicks on the X

<pre class="language-html"><code class="lang-html"><strong>// MessageComponent.jte
</strong>@import de.tschuehly.easy.spring.auth.web.message.MessageComponent.MessageContext
@import java.util.Date
@param MessageContext messageContext

!{var id = String.valueOf(new Date().getTime());}
&#x3C;div role="alert" id="${id}" style="position: fixed; margin: 2rem; top: 0; left: 0; border-radius: 1rem; background-color: antiquewhite; padding: 1rem;">
    &#x3C;button onclick="document.getElementById('${id}').style.display = 'none'">
        &#x3C;i class="">X&#x3C;/i>
    &#x3C;/button>
    &#x3C;div>
        &#x3C;h2>${messageContext.type().severity()}: ${messageContext.message()}&#x3C;/h2>
    &#x3C;/div>
&#x3C;/div>
</code></pre>

Now we will create a `@ControllerAdvice` where we catch the `InfoException.class` and then render the `MessageComponent`

```java
// ExceptionAdvice.java
@ControllerAdvice
public class ExceptionAdvice {

  private final MessageComponent messageComponent;

  public ExceptionAdvice(MessageComponent messageComponent) {
    this.messageComponent = messageComponent;
  }

  @ExceptionHandler(InfoException.class)
  public ViewContext handle(InfoException e) {
    HtmxUtil.retarget(HtmxUtil.idSelector(LayoutComponent.TOAST_CONTAINER_ID));
    HtmxUtil.swap(HxSwapType.INNER_HTML);
    return messageComponent.renderInfoToast(e.getMessage());
  }
}
```

If we restart the application and search for a string that is not present in the data we will be shown the toast.

<figure><img src=".gitbook/assets/image (2) (1) (1).png" alt=""><figcaption></figcaption></figure>

{% hint style="success" %}
Lab-8 Checkpoint 1
{% endhint %}
