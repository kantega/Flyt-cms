package no.kantega.openaksess.contentApi.transferObject;

import no.kantega.commons.media.MimeType;
import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.data.enums.MultimediaType;

/**
 * @author Tom
 * @since 10.08.15
 */
public class MultimediaTransferObject {
    private Multimedia multimedia;

    public MultimediaTransferObject(Multimedia multimedia){
        this.multimedia = multimedia;
    }

    public MimeType getMimeType(){
        return multimedia.getMimeType();
    }

    public String getUrl(){
        return multimedia.getUrl();
    }

    public String getFileType(){
        return multimedia.getFileType();
    }

    public String getFilename(){
        return multimedia.getFilename();
    }

    public String getAltname(){
        return multimedia.getAltname();
    }

    public String getName(){
        return multimedia.getName();
    }

    public int getId(){
        return multimedia.getId();
    }
}
