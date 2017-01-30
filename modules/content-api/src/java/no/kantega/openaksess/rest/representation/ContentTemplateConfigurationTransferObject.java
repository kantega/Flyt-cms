package no.kantega.openaksess.rest.representation;

import no.kantega.publishing.common.data.TemplateConfiguration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.stream.Collectors;

@XmlRootElement(name = "contentTemplateConfig")
@XmlAccessorType(XmlAccessType.NONE)
public class ContentTemplateConfigurationTransferObject {

    private TemplateConfiguration templateConfiguration;

    public ContentTemplateConfigurationTransferObject(TemplateConfiguration templateConfiguration) {
        this.templateConfiguration = templateConfiguration;
    }


    @XmlElement
    public List<ContentTemplateTransferObject> getContentTemplates(){
        return this.templateConfiguration.getContentTemplates()
                .stream()
                .map(ContentTemplateTransferObject::new)
                .collect(Collectors.toList());
    }

    @XmlElement
    public List<SiteTransferObject> getSites(){
        return templateConfiguration.getSites()
                .stream()
                .map(SiteTransferObject::new)
                .collect(Collectors.toList());
    }
}
