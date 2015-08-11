package no.kantega.openaksess.rest.transferObject;

import no.kantega.commons.media.MimeType;
import no.kantega.publishing.common.data.Multimedia;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public class MultimediaTransferObject {
    private Multimedia multimedia;

    public MultimediaTransferObject(Multimedia multimedia){
        this.multimedia = multimedia;
    }

    @XmlElement
    public MimeType getMimeType(){
        return multimedia.getMimeType();
    }

    @XmlElement
    public String getUrl(){
        return multimedia.getUrl();
    }

    @XmlElement
    public String getFileType(){
        return multimedia.getFileType();
    }

    @XmlElement
    public String getFilename(){
        return multimedia.getFilename();
    }

    @XmlElement
    public String getAltname(){
        return multimedia.getAltname();
    }

    @XmlElement
    public String getName(){
        return multimedia.getName();
    }

    @XmlElement
    public int getId(){
        return multimedia.getId();
    }
}
