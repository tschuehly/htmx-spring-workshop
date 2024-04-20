package de.tschuehly.easy.spring.auth.web.list;

import de.tschuehly.spring.viewcomponent.core.component.ViewComponent;
import de.tschuehly.spring.viewcomponent.jte.ViewContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ViewComponent
public class ListComponent {

  public ViewContext render(List<ViewContext> viewContextList){
    return new ListContext(viewContextList);
  }
  public ViewContext render(List<ViewContext> viewContextList, ViewContext... viewContext){
    ArrayList<ViewContext> combinedList  = new ArrayList<>();
    combinedList.addAll(viewContextList);
    combinedList.addAll(List.of(viewContext));
    return new ListContext(combinedList);
  }
  public ViewContext render( ViewContext... viewContext){
    return new ListContext(Arrays.stream(viewContext).toList());
  }

  public record ListContext(List<ViewContext> viewContextList) implements ViewContext {

  }
}