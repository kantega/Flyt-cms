package no.kantega.publishing.modules.mailsender;

import no.kantega.commons.configuration.Configuration;
import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.spring.RootContext;
import org.apache.commons.io.IOUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.event.ReferenceInsertionEventHandler;
import org.apache.velocity.app.event.EventCartridge;
import static org.apache.commons.lang.StringEscapeUtils.escapeHtml;
import org.apache.velocity.tools.generic.DateTool;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class VelocityTemplateRenderer {
    private String templateFile;
    private Map<String, Object> properties;
    private boolean htmlEscape;

    VelocityTemplateRenderer(String templateFile){
        this.templateFile = templateFile;
        properties = new HashMap<>();
        htmlEscape = true;
    }
    public String render()  throws SystemException {
        try {
            Velocity.init();

            ResourceLoader source = (ResourceLoader)RootContext.getInstance().getBean("emailTemplateResourceLoader");
            Resource resource = source.getResource(templateFile);
            if(!properties.containsKey("dateFormatter")){
                properties.put("dateFormatter", new DateTool());
            }
            Configuration config = Aksess.getConfiguration();

            String encoding = config.getString("mail.templates.encoding", "ISO-8859-1");
            String templateText = IOUtils.toString(resource.getInputStream(), encoding);

            VelocityContext context = new VelocityContext(properties);
            if(htmlEscape){
                EventCartridge eventCartridge = new EventCartridge();
                context.attachEventCartridge(eventCartridge);
                eventCartridge.addReferenceInsertionEventHandler(new ReferenceInsertionEventHandler() {
                    public Object referenceInsert(String reference, Object value) {
                        return escapeHtml(value.toString());
                    }
                });
            }
            StringWriter textWriter = new StringWriter();
            Velocity.evaluate(context, textWriter, "body", templateText);
            return textWriter.toString();
        } catch (Exception e) {
            throw new SystemException("Feil ved generering av mailtekst basert på Velocity. TemplateFile: " + templateFile, e);
        }
    }
    public VelocityTemplateRenderer addProperty(String key, Object obj){
        properties.put(key, obj);
        return this;
    }
    public VelocityTemplateRenderer disableHTMLEscaping(){
        htmlEscape = false;
        return this;
    }
}
