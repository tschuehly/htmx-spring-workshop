package de.tschuehly.easy.spring.auth.web.message;

import de.tschuehly.spring.viewcomponent.core.component.ViewComponent;
import de.tschuehly.spring.viewcomponent.jte.ViewContext;

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