package no.kantega.publishing.templating;

import no.kantega.publishing.api.templating.TemplateRenderer;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class DefaultTemplateRenderer implements TemplateRenderer {

    private VelocityEngine velocityEngine;
    private String defaultEncoding = "iso-8859-1";

    public void setVelocityEngine(VelocityEngine velocityEngine) {
        this.velocityEngine = velocityEngine;
    }

    public Renderer template(Class clazz, String resource) {
        return new DefaultRenderer(clazz, resource);
    }

    public Renderer template(String path) {
        return new DefaultRenderer(path);
    }

    public void setDefaultEncoding(String defaultEncoding) {
        this.defaultEncoding= defaultEncoding;
    }

    class DefaultRenderer implements Renderer {
        private final Map<String, Object> attributes = new HashMap<>();
        private final String path;
        private final Class clazz;
        private final String resource;
        private String encoding = defaultEncoding;

        public DefaultRenderer(String path) {
            this.path = path;
            this.clazz = null;
            this.resource = null;
        }

        public DefaultRenderer(Class clazz, String resource) {
            this.clazz = clazz;
            this.resource = resource;
            this.path = null;
        }

        public Renderer encoding(String encoding) {
            this.encoding = encoding;
            return this;
        }

        public Renderer addAttribute(String name, Object value) {
            attributes.put(name, value);
            return this;
        }

        public String render() {
            StringWriter sw = new StringWriter();
            render(sw);
            return sw.toString();
        }

        public void render(Writer writer) {
            try {
                VelocityContext context = new VelocityContext(attributes);
                if(path != null) {
                    velocityEngine.mergeTemplate(path, encoding, context, writer);
                } else {
                    velocityEngine.evaluate(context, writer, getClass().getName(), new InputStreamReader(clazz.getResourceAsStream(resource), encoding));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
