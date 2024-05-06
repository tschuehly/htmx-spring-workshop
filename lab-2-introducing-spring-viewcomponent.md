# Lab 2: Introducing Spring ViewComponent

The goal of this lab is to refactor the application to use Spring ViewComponent and htmx-spring-boot to delegate rendering responsibility to the ViewComponents and remove it from the Controller

### An Introduction to Spring ViewComponent

Spring ViewComponent is a way to create server-rendered UI components using the presenter pattern, inspired by similar libraries like Ruby on Rails ViewComponent.

A ViewComponent is a Spring-managed bean that defines a rendering context for a corresponding template, this context is called `ViewContext`.&#x20;

We can create one by annotating a class with the `@ViewComponent` annotation and defining a public nested record that implements the ViewContext interface.

```java
@ViewComponent
public class SimpleViewComponent {
    public record SimpleView(String helloWorld) implements ViewContext {
    }

    public SimpleView render() {
        return new SimpleView("Hello World");
    }
}
```

A ViewComponent needs to have a template with the same name defined in the same package. In the template, we can access the properties of the record.&#x20;

```
// SimpleViewComponent.jte
@param SimpleViewComponent.SimpleView simpleView
<div>${simpleView.helloWorld()}</div>
```

{% hint style="info" %}
Spring ViewComponent wraps the underlying MVC model using Spring AOP and enables us to create the frontend in a similar way to the component-oriented JavaScript frameworks
{% endhint %}

### Using Spring ViewComponent

To start we need to add two dependencies to the `build.gradle.kts` file:&#x20;

```
implementation("de.tschuehly:spring-view-component-jte:0.7.2")
annotationProcessor("de.tschuehly:spring-view-component-core:0.7.2")
```
