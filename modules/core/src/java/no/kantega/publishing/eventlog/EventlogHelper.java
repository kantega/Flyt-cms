package no.kantega.publishing.eventlog;

import no.kantega.publishing.api.model.BaseObject;
import no.kantega.publishing.common.data.Attachment;

public class EventlogHelper {
    /**
     * Attachment does not extend BaseObject, but eventlog requires a BaseObject if
     * it should log Id. This method create a BaseObject from an attachment.
     * @param attachment to log.
     * @return BaseObject with id and title of the attachment.
     */
    public static BaseObject toDummyBaseObject(final Attachment attachment){
        return new BaseObject() {

            @Override
            public int getId() {
                return attachment.getId();
            }

            @Override
            public int getObjectType() {
                return 0;
            }

            @Override
            public String getName() {
                return attachment.getFilename();
            }

            @Override
            public String getOwner() {
                return "";
            }

            @Override
            public String getOwnerPerson() {
                return "";
            }
        };
    }
}
