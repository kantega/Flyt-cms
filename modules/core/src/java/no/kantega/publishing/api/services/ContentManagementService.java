
package no.kantega.publishing.api.services;

import no.kantega.commons.exception.NotAuthorizedException;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.api.content.ContentStatus;
import no.kantega.publishing.api.path.PathEntry;
import no.kantega.publishing.common.data.*;
import no.kantega.publishing.common.data.enums.ContentVisibilityStatus;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.publishing.common.exception.ObjectInUseException;
import no.kantega.publishing.common.exception.ObjectLockedException;
import no.kantega.publishing.common.util.InputStreamHandler;

import java.util.Date;
import java.util.List;

public interface ContentManagementService {

    public Content getContentDoNotLog(ContentIdentifier id) throws NotAuthorizedException, ContentNotFoundException;

    public Content getContent(ContentIdentifier id) throws NotAuthorizedException, ContentNotFoundException;

    /**
     * Check out an content object. The difference from getContent is that checking out
     * indicates that we want to change the object, so a Lock is created.
     * @param id - ContentIdentifier for the Content object
     * @return Content object associated with the ContentIdentifier or null if the content does not exist.
     * @throws no.kantega.commons.exception.SystemException - System error
     * @throws NotAuthorizedException - if the user is not authorized to update the content.
     * @throws no.kantega.publishing.common.exception.ObjectLockedException - if the Content object is already checked out.
     */
    public Content checkOutContent(ContentIdentifier id) throws NotAuthorizedException, ObjectLockedException, ContentNotFoundException;

    public Content getLastVersionOfContent(ContentIdentifier id) throws ContentNotFoundException, NotAuthorizedException;

    public List<Content> getAllContentVersions(ContentIdentifier id) throws ContentNotFoundException, NotAuthorizedException;

    public Content checkInContent(Content content, ContentStatus newStatus) throws NotAuthorizedException;

    public Content createNewContent(ContentCreateParameters parameters) throws NotAuthorizedException;

    /**
     * Create a copy of the source Content and put it under the given target.
     * @param sourceContent - The Content that is to be copied.
     * @param target - The association under which the copy is to be placed.
     * @param category - The AssociationCategory to put the copy into.
     * @param copyChildren - indicate whether the operation should be recursive, and copy the source's children as well.
     * @return The new copy of sourceContent.
     * @throws NotAuthorizedException
     */
    public Content copyContent(Content sourceContent, Association target, AssociationCategory category, boolean copyChildren) throws NotAuthorizedException;

    /**
     * Updates the visibility status of a content object
     * @param content to set visibility status for
     * @param newVisibilityStatus the new status
     */
    public void setContentVisibilityStatus(Content content, ContentVisibilityStatus newVisibilityStatus);

    /**
     * Setter ny status på et objekt, f.eks ved godkjenning av en side.
     * @param cid - ContentIdenfier for nytt objekt
     * @param newStatus - Ny status
     * @param note - melding
     * @return the content object identified by the given ContentIdenfier with the new status.
     * @throws NotAuthorizedException
     */
    public Content setContentStatus(ContentIdentifier cid, ContentStatus newStatus, String note) throws NotAuthorizedException;

    /**
     * Delete all versions of the Content object.
     * @param id - ContentIdenfier of one of the ContentVersions
     * @throws no.kantega.publishing.common.exception.ObjectInUseException
     * @throws NotAuthorizedException
     */
    public void deleteContent(ContentIdentifier id) throws ObjectInUseException, NotAuthorizedException;

    /**
     * Sletter en bestemt versjon av et innholdsobjekt.  Dersom objektversjonen er aktiv blir den ikke slettet.
     * @param id - Innholdsid
     * @throws NotAuthorizedException
     */
    public void deleteContentVersion(ContentIdentifier id) throws NotAuthorizedException;

    /**
     * Henter en liste med innholdsobjekter fra basen
     * @param query - Søk som angir hva som skal hentes
     * @param sort - Sorteringsrekkefølge
     * @param getAttributes - Hent attributter (true) for en side eller bare basisdata (false)
     * @param getTopics - Hent topics (true) for en side eller ikke (false)
     * @return Liste med innholdsobjekter
     */
    public List<Content> getContentList(ContentQuery query, SortOrder sort, boolean getAttributes, boolean getTopics);

    /**
     * Henter en liste med innholdsobjekter fra basen med innholdsattributter
     * @param query - Søk som angir hva som skal hentes
     * @param sort - Sorteringsrekkefølge
     * @return the Content object matching contentQuery, that the user have access privilegies for.
     */
    public List<Content> getContentList(ContentQuery query, SortOrder sort) ;

    /**
     * Henter en liste med innholdsobjekter fra basen uten attributter
     * @param query - Søk som angir hva som skal hentes
     * @param sort - Sorteringsrekkefølge
     * @return Liste med innholdsobjekter
     */
    public List<Content> getContentSummaryList(ContentQuery query, SortOrder sort);

    /**
     * Hent innhold som er mitt (dvs min arbeidsliste)
     * @return Liste med innholdsobjekter
     */
    public List<WorkList<Content>> getMyContentList();

    /**
     * Henter alle innholdsobjekter som kan godkjennes av deg
     * @return Liste med innholdsobjekter
     */
    public List<Content> getContentListForApproval();

    /**
     * Get parent content identifier
     * @param cid of the child we want to get parent identifier for
     * @return ContentIdenfier for the parent of the content identified by cid
     */
    public ContentIdentifier getParent(ContentIdentifier cid);

    /**
     * Updates publish date and expire date on a content object and all child objects
     * @param cid - ContentIdentifier to content object
     * @param publishDate - new publish date
     * @param expireDate - new expire date
     * @param updateChildren - true = update children / false = dont update children
     */
    public void updateDisplayPeriodForContent(ContentIdentifier cid, Date publishDate, Date expireDate, boolean updateChildren) throws NotAuthorizedException;

    /**
     * Henter en liste over antall endringer gjort av brukere
     * @return Liste med innholdsobjekter
     */
    public List<UserContentChanges> getNoChangesPerUser(int months);

    /**
     * Hent sti basert på kopling
     * @param association - Kopling til innholdsobjekt
     * @return Liste med PathEntry objekter
     */
    public List<PathEntry> getPathByAssociation(Association association);

    /**
     * Kopierer en struktur fra et sted til et annet, dvs krysspubliserer.
     *
     * @param source - Punktet som skal publiseres
     * @param target - Punktes det skal publiseres under
     * @param category - Spalte det skal publiseres til
     * @param copyChildren - Skal barn også kopieres
     */
    public void copyAssociations(Association source, Association target, AssociationCategory category, boolean copyChildren);

    /**
     * Legger til en kopling i basen
     *
     * @param association  - Kopling som skal legges til
     */
    public void addAssociation(Association association);

    /**
     * Sletter de angitte koplinger fra basen, dvs markerer dem som slettet. Legger innslag i deleteditems
     * slik at brukeren kan gjenopprette dem senere.
     *
     * Dersom deleteMultiple = false og det finnes underobjekter vil ikke sletting bli utført, men
     * man før en liste med hva som blir slettet, som kan vises for brukeren
     *
     * @param associationIds - Koplinger som skal slettes
     * @param deleteMultiple - Må være satt til true for å utføre sletting hvis det finnes underobjekter
     * @return The content objects to which the associations deleted pointed to.
     */
    public List<Content> deleteAssociationsById(List<Integer> associationIds, boolean deleteMultiple);

    /**
     * Endrer en kopling i systemet.  F.eks når en bruker flytter et punkt i strukturen. Oppdaterer
     * alle underliggende koplinger.
     *
     * @param association - Kopling som skal oppdateres
     */
    public void modifyAssociation(Association association);

    /**
     * Henter en liste med innhold som er slettet av brukeren, slik at han kan angre på dette senere
     *
     * @return - Liste med DeletedItem
     */
    public List<DeletedItem> getDeletedItems();

    /**
     * Restore a deleted item from deleted items
     * @param id from trashcan
     * @return id of restored item (not trashcan id)
     */
    public int restoreDeletedItem(int id);

    /**
     * Henter et vedlegg fra databasen med angitt id. NB! Henter ikke data i objektet, må streames
     *
     * @param id - id til vedlegg som skal hentes
     * @return - Attachment objekt
     * @throws NotAuthorizedException - Brukeren har ikke rettighet til å lese vedlegg
     */
    public Attachment getAttachment(int id, int siteId) throws NotAuthorizedException;

    /**
     * Streamer et vedlegg fra databasen til en stream ved hjelp av en callback
     * @param id - Id til vedlegg som skal streames
     * @param ish - Callback for å streame data
     */
    public void streamAttachmentData(int id, InputStreamHandler ish);

    /**
     * Lagrer et vedlegg i basen
     * @param attachment - Vedlegg som skal lagres
     * @return - id of saved attachment
     * @throws NotAuthorizedException
     */
    public int setAttachment(Attachment attachment) throws NotAuthorizedException;

    /**
     * Sletter et vedlegg fra basen
     * @param id - id til vedlegg som skal slettes
     * @throws NotAuthorizedException
     */
    public void deleteAttachment(int id) throws NotAuthorizedException;

    /**
     * Henter en liste med alle vedlegg til et innholdsobjekt
     * @param id - Id til innholdsobjekt
     * @return - liste med Attachment objekt
     */
    public List<Attachment> getAttachmentList(ContentIdentifier id);

}
