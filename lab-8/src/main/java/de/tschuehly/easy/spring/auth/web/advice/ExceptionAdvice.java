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
