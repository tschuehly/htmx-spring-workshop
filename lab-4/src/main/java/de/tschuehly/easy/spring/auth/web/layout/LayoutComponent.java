package de.tschuehly.easy.spring.auth.web.layout;

import de.tschuehly.spring.viewcomponent.core.component.ViewComponent;
import de.tschuehly.spring.viewcomponent.jte.ViewContext;

@ViewComponent
public class LayoutComponent {
    public static final String MODAL_CONTAINER_ID = "modalContainer";
    public static final String CLOSE_MODAL_EVENT = "close-modal";
    public record LayoutContext(ViewContext content
    ) implements ViewContext {
    }

    public ViewContext render(ViewContext content) {
        return new LayoutContext(content);
    }
}