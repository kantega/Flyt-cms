package no.kantega.publishing.api.templating;

import java.io.Writer;

/**
 *
 */
public interface TemplateRenderer {

    Renderer template(String path);
    Renderer template(Class clazz, String resource);

    interface Renderer {
        Renderer encoding(String encoding);
        Renderer addAttribute(String name, Object value);
        String render();
        void render(Writer writer);
    }
}
