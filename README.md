# Building server-side web applications with htmx

## Setup:

Please install the following:

* Java 21
* IntelliJ IDEA
* [https://plugins.jetbrains.com/plugin/20588-htmx-support](https://plugins.jetbrains.com/plugin/20588-htmx-support)
* [https://plugins.jetbrains.com/plugin/14521-jte](https://plugins.jetbrains.com/plugin/20588-htmx-support)
* Gradle

#### IntelliJ Code Template

To create a new ViewComponent with one click we can create a new IntelliJ Code Template.

Click on Create Template

<figure><img src=".gitbook/assets/image.png" alt=""><figcaption></figcaption></figure>

Name: ViewComponent

File Name:  `${COMPONENT_NAME}Component.java`

```
package ${PACKAGE_NAME};

import de.tschuehly.spring.viewcomponent.core.component.ViewComponent;
import de.tschuehly.spring.viewcomponent.jte.ViewContext;

@ViewComponent
public class ${COMPONENT_NAME}Component {
  public record ${COMPONENT_NAME}Context() implements ViewContext{}

  public ViewContext render(){
    return new ${COMPONENT_NAME}Context();
  }
}
```

Then click on "Create Child Template"

<figure><img src=".gitbook/assets/image (1).png" alt=""><figcaption></figcaption></figure>

File Name: `${COMPONENT_NAME}Component.jte`

Extension: `jte`

```
@import ${PACKAGE_NAME}.${COMPONENT_NAME}Component.${COMPONENT_NAME}Context
@param ${COMPONENT_NAME}Context ${COMPONENT_NAME.substring(0,1).toLowerCase()}${COMPONENT_NAME.substring(1)}Context
```

## Labs

### Lab 1: Server-side rendering with Spring Boot and JTE

This lab aims to build a simple user management application with Spring Boot, JTE and htmx.

{% content-ref url="lab1.md" %}
[lab1.md](lab1.md)
{% endcontent-ref %}

### Lab 2: Introducing Spring ViewComponent

The goal of this lab is to refactor the application to use Spring ViewComponent and htmx-spring-boot to delegate rendering responsibility to the ViewComponents and remove it from the Controller.

{% content-ref url="lab-2-introducing-spring-viewcomponent.md" %}
[lab-2-introducing-spring-viewcomponent.md](lab-2-introducing-spring-viewcomponent.md)
{% endcontent-ref %}

### Lab 3: Inline Editing.

In this lab, we will create a group Management Page where we can add a user to a group and a navigation bar

{% content-ref url="lab-3-inline-editing.md" %}
[lab-3-inline-editing.md](lab-3-inline-editing.md)
{% endcontent-ref %}

### Lab 4: Using Spring Beans to Compose the UI

In this short lab, we will use the dependency injection capabilities of Spring to show a navigation bar where we can add new items by just creating new Components

### Lab 5: Lazy Loading

In this lab, we will lazily load the user table as it now contains a lot of users

### Lab 6: Full Text Search

In this lab, we will enable our users to search the user table.

### Lab 7: Infinite Scrolling using Pageable

In this lab, we will only load new users when we are scrolling down, improving our initial page speed

### Lab 8: Exception Messages

In this lab, we will catch Exception using a Spring Controller Advice and show them to the user in a nice form

### Lab 9: Server-Sent Events

We will leverage the SSE capabilities of htmx to update our table live with the newest Users that are created.
