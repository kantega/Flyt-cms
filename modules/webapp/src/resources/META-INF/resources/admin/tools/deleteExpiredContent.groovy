import com.google.common.collect.Lists
import no.kantega.publishing.api.content.ContentIdentifier
import no.kantega.publishing.common.ao.ContentHandler
import no.kantega.publishing.common.data.Content
import no.kantega.publishing.common.data.ContentQuery
import no.kantega.publishing.common.util.database.dbConnectionFactory
import no.kantega.publishing.content.api.ContentAO
import no.kantega.publishing.event.ContentEventListener
// Delete content with expire action delete that are expired
def template = dbConnectionFactory.jdbcTemplate
ContentAO contentAO = beans.contentAO
ContentEventListener listener = beans.contentListenerNotifier
def toDelete = template.queryForList('select contentid from content where expireaction = \'DELETE\' and visibilitystatus = 20', Integer.class)

Lists.partition(toDelete, 1000).each {
    def cq = new ContentQuery()
    def cids = it.collect({
        ContentIdentifier.fromContentId(it)
    })
    cq.setContentList(cids)
    cq.showExpired = true
    contentAO.doForEachInContentList(cq, -1, null, new ContentHandler() {
        @Override
        void handleContent(Content content) {
            /*listener.contentExpired(new ContentEvent().setContent(content))*/
            println content.modifiedBy
        }
    })
}

'OK'
