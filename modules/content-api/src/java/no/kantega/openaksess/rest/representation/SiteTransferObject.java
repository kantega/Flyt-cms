package no.kantega.openaksess.rest.representation;

import no.kantega.publishing.api.model.Site;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

@XmlAccessorType(XmlAccessType.NONE)
public class SiteTransferObject {
    private Site site;

    public SiteTransferObject(Site site) {
        this.site = site;
    }

    @XmlElement
    public int getId(){
        return site.getId();
    }

    @XmlElement
    public String getName(){
        return site.getName();
    }

    @XmlElement
    public List<String> getHostames(){
        return site.getHostnames();
    }




}
