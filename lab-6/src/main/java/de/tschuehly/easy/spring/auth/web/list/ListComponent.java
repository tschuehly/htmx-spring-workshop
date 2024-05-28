package de.tschuehly.easy.spring.auth.web.list;

import de.tschuehly.spring.viewcomponent.core.component.ViewComponent;
import de.tschuehly.spring.viewcomponent.jte.ViewContext;

import java.util.List;

@ViewComponent
public class ListComponent {
    public record ListContext(List<ViewContext> viewContextList) implements ViewContext {
    }

    public ViewContext render(List<ViewContext> viewContextList) {
        return new ListContext(viewContextList);
    }
}