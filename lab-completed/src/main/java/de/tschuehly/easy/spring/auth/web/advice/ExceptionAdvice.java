package de.tschuehly.easy.spring.auth.web.advice;

import static de.tschuehly.easy.spring.auth.htmx.HtmxUtil.idSelector;

import de.tschuehly.easy.spring.auth.htmx.HtmxUtil;
import de.tschuehly.easy.spring.auth.web.exception.InfoException;
import de.tschuehly.easy.spring.auth.web.layout.LayoutComponent;
import de.tschuehly.easy.spring.auth.web.message.MessageComponent;
import de.tschuehly.spring.viewcomponent.jte.ViewContext;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxSwapType;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionAdvice {

  private final MessageComponent messageComponent;

  public ExceptionAdvice(MessageComponent messageComponent) {
    this.messageComponent = messageComponent;
  }

  @ExceptionHandler(InfoException.class)
  public ViewContext handle(InfoException e) {
    HtmxUtil.retarget(idSelector(LayoutComponent.TOAST_CONTAINER_ID));
    HtmxUtil.swap(HxSwapType.INNER_HTML);
    return messageComponent.renderInfoToast(e.getMessage());
  }
}
