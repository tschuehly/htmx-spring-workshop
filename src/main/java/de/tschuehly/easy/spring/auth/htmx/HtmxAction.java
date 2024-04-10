package de.tschuehly.easy.spring.auth.htmx;

import gg.jte.Content;
import gg.jte.TemplateOutput;
import java.util.List;
import org.springframework.web.bind.annotation.RequestMethod;

public class HtmxAction implements Content {
  public final String path;
  public final RequestMethod requestMethod;
  public final Class<?> parameterClass;
  public HtmxAction(String path, RequestMethod requestMethod, Class<?> parameterClass){
    this.path = path;
    this.requestMethod = requestMethod;
    this.parameterClass = parameterClass;
  }



  @Override
  public void writeTo(TemplateOutput output) {
    output.writeContent(
        "hx-post =\"" + path + "\"");
  }
}
