package de.tschuehly.easy.spring.auth.web.message;

import de.tschuehly.spring.viewcomponent.core.component.ViewComponent;
import de.tschuehly.spring.viewcomponent.jte.ViewContext;

@ViewComponent
public class MessageComponent {


  public ViewContext renderSuccessToast(String message) {
    return new Message(message,  MessageType.TOAST_SUCCESS);
  }

  public ViewContext renderErrorToast(String message) {
    return new Message(message,  MessageType.TOAST_ERROR);
  }

  public ViewContext renderInfoToast(String message) {
    return new Message(message,  MessageType.INFO);
  }

  public record Message(String message, MessageType type) implements
      ViewContext {

  }

  public enum MessageType {
    TOAST_SUCCESS,
    TOAST_ERROR,
    NONE, INFO;

    public String severity() {
      return switch (this) {
        case TOAST_SUCCESS -> "success";
        case TOAST_ERROR -> "error";
        case INFO -> "info";
        default -> "";
      };
    }
  }
}
