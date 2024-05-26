# Lab 8: Exception Messages

One benefit of server-side rendering is leveraging server exceptions to display useful information to the user.&#x20;

In this lab, we will catch Exceptions using a Spring `@ControllerAdvice` and show them to the user in a toast message.

### Exceptions

We start by creating an InfoException base class in the `de.tschuehly.easy.spring.auth.web.exception` package:

{% code title="InfoException.java" %}
```java
package de.tschuehly.easy.spring.auth.web.exception;

public class InfoException extends RuntimeException{

  public InfoException(String message) {
    super(message);
  }
}
```
{% endcode %}

Then we create a `UserNotFoundException` that extends the InfoException in the `de.tschuehly.easy.spring.auth.user` package:

```java
package de.tschuehly.easy.spring.auth.user;

import de.tschuehly.easy.spring.auth.web.exception.InfoException;

public class UserNotFoundException extends InfoException {

  public UserNotFoundException(String message) {
    super(message);
  }
}
```

### UserService

Now we will adjust the `UserService.searchUser` method to throw the `UserNotFoundException` when no users are found.

{% code title="UserService.java" %}
```java
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
{% endcode %}

### LayoutComponent

Now we need to create a container element for the message in the `LayoutComponent`.

We first define a constant `TOAST_CONTAINER_ID`:

{% code title="LayoutComponent.java" %}
```java
public static final String TOAST_CONTAINER_ID = "toastContainer";
```
{% endcode %}

We first import the `TOAST_CONTAINER_ID` in the `LayoutComponent.jte`

{% code title="LayoutComponent.jte" %}
```java
@import static de.tschuehly.easy.spring.auth.web.layout.LayoutComponent.TOAST_CONTAINER_ID
```
{% endcode %}

Then we add a `<div>` element that has the ID set to `TOAST_CONTAINER_ID` after the `<body>` element.

{% code title="LayoutComponent.jte" %}
```html
<div id="${TOAST_CONTAINER_ID}">

</div>
```
{% endcode %}

### MessageComponent

Now we create a `MessageComponent` ViewComponent in the `de.tschuehly.easy.spring.auth.web.message` package.

We also define an `MessageType` enum that has a `severity` method.

<pre class="language-java" data-title="MessageComponent.java"><code class="lang-java">package de.tschuehly.easy.spring.auth.web.message;

import de.tschuehly.spring.viewcomponent.core.component.ViewComponent;
import de.tschuehly.spring.viewcomponent.jte.ViewContext;

<strong>@ViewComponent
</strong>public class MessageComponent {

  public ViewContext renderInfoToast(String message) {
    return new MessageContext(message,  MessageType.INFO);
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
</code></pre>

{% hint style="info" %}
In the `severity()` function we could also define conditional CSS to style the toast
{% endhint %}

Now we will create the corresponding MessageComponent.jte

{% code title="MessageComponent.jte" %}
```
@import de.tschuehly.easy.spring.auth.web.message.MessageComponent.MessageContext
@import java.util.Date
@param MessageContext messageContext

!{var id = String.valueOf(new Date().getTime());}
<div role="alert" id="${id}" <%-- (1) --%>
     style="position: fixed; margin: 2rem; top: 0; left: 0; border-radius: 1rem; background-color: antiquewhite; padding: 1rem;">
    <button onclick="document.getElementById('${id}').style.display = 'none'"> <%-- (2) --%>
        <i class="">X</i>
    </button>
    <div>
        <h2>${messageContext.type().severity()}: 
        ${messageContext.message()}</h2>  <%-- (3) --%>
    </div>
</div>

```
{% endcode %}

(1): In the template, we create an `<div>`  element where we set the `id` to a timestamp, we define as a local variable with the  `!{var}` syntax

(2): We use the `<button onclick>` attribute to hide the toast when the user clicks on the X

(3): We create a `<h2>` element in it that shows the severity and the message of the exception.

### ControllerAdvice

Now we will create an `ExceptionAdvice.java` class in the `de.tschuehly.easy.spring.auth.web.advice` package:

{% code title="ExceptionAdvice.java" %}
```java
package de.tschuehly.easy.spring.auth.web.advice;

import de.tschuehly.easy.spring.auth.htmx.HtmxUtil;
import de.tschuehly.easy.spring.auth.web.exception.InfoException;
import de.tschuehly.easy.spring.auth.web.layout.LayoutComponent;
import de.tschuehly.easy.spring.auth.web.message.MessageComponent;
import de.tschuehly.spring.viewcomponent.jte.ViewContext;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxSwapType;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice // (1)
public class ExceptionAdvice {

  private final MessageComponent messageComponent;

  public ExceptionAdvice(MessageComponent messageComponent) { // (2)
    this.messageComponent = messageComponent;
  }

  @ExceptionHandler(InfoException.class) // (3)
  public ViewContext handle(InfoException e) {
    HtmxUtil.retarget(HtmxUtil.idSelector(LayoutComponent.TOAST_CONTAINER_ID)); // (4)
    HtmxUtil.swap(HxSwapType.INNER_HTML); // (5)
    return messageComponent.renderInfoToast(e.getMessage()); // (6)
  }
}

```
{% endcode %}

**(1):** We annotate the class with the`@ControllerAdvice` annotation.

**(2):** We autowire the `MessageComponent`

**(3):** We define an `@ExceptionHandler` method that handles the `InfoException.class`&#x20;

**(4):** We retarget the response to the `<div>` we created earlier with the `TOAST_CONTAINER_ID`

**(5):** We set the Swap method to `INNER_HTML`

### Sucess!

Restart the application and navigate to [http://localhost:8080](http://localhost:8080/).

Now search for a string not present in the data, and then you will see the toast message with the exception message.

<figure><img src="../.gitbook/assets/image (2) (1) (1).png" alt=""><figcaption></figcaption></figure>

{% hint style="success" %}
Lab-8 Checkpoint 1

If you are stuck you can resume at this checkpoint with:

`git checkout tags/lab-8-checkpoint-1 -b lab-8-c1`
{% endhint %}
