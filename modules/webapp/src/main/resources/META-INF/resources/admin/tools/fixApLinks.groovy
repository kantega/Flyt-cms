import no.kantega.publishing.admin.content.util.AttributeHelper
import no.kantega.publishing.api.attachment.ao.AttachmentAO
import no.kantega.publishing.api.content.ContentAO
import no.kantega.publishing.api.multimedia.MultimediaAO
import no.kantega.publishing.api.services.ContentManagementService
import no.kantega.publishing.common.data.ContentQuery
import no.kantega.publishing.common.data.attributes.Attribute

def attachmentAo = beans.attachmentAO as AttachmentAO
def contentAo = beans.contentAO as ContentAO
def multimediaAO = beans.multimediaAO as MultimediaAO

ContentManagementService cms = beans.contentManagmentService
def query = new ContentQuery()
query.contentTemplates = [1, 2, 8, 11, 12]
def contents = cms.getContentList(query)
contents.each {
    def changed = false;
    def attributes = it.getContentAttributes()
    for (Attribute a : attributes.values()) {
        def value = a.getValue()
        String result = AttributeHelper.replaceApUrls(value, contentAo, attachmentAo, multimediaAO)

        def attributeChanged = value != result
        if (attributeChanged) {
            a.value = result
            changed = true
        }
    }
    if(changed) {
        cms.checkInContent(it, it.status)
    }
}

'DONE'
