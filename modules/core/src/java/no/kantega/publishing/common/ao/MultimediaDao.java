package no.kantega.publishing.common.ao;

import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.exception.ObjectInUseException;
import no.kantega.publishing.common.util.InputStreamHandler;

import java.util.List;

public interface MultimediaDao {
    /**
     * Delete a multimedia object
     * @param id - id of object to be deleted
     * @throws ObjectInUseException - Thrown if there exists children for this object
     */
    public void deleteMultimedia(int id) throws ObjectInUseException;

    /**
     * Get Multimedia object from database (only metadata is fetched, data is not fetched)
     * @param id - Id of object to retrieve
     * @return - Multimedia
     */
    public Multimedia getMultimedia(int id);

    /**
     * Get Multimedia object from database (only metadata is fetched, data is not fetched)
     * @param parentId - parentId of object to retrieve
     * @param name - name of object to retrieve
     * @return - Multimedia
     */
    public Multimedia getMultimediaByParentIdAndName(int parentId, String name);


    /**
     * Retrieves an image associated with the user's profile.
     * @param userId -
     * @return - Multimedia
     * @throws SystemException
     */
    public Multimedia getProfileImageForUser(String userId);

    /**
     * Stream multimedia data
     * @param id - Id of object to stream data for
     * @param ish - InputStreamHandler to receive data
     */
    public void streamMultimediaData(int id, InputStreamHandler ish);


    /**
     * Fetch all multimedia objects with specified parentid
     * @param parentId - parentId
     * @return - List<Multimedia>
     */
    public List<Multimedia> getMultimediaList(int parentId);


    /**
     * Fetch all multimedia objects with specified contentId
     * @param contentId - contentId
     * @return - List<Multimedia>
     */
    public List<Multimedia> getMultimediaWithContentId(int contentId);

    /**
     * Get number of objects in mediaarchive
     *
     * @return number of objects in mediaarchive
     */
    public int getMultimediaCount();


    /**
     * Searches the multimedia-archive for the given criteria
     *
     * @param phrase the text to search for. If this is a number it is interpreted as an ID to search for. If not,
     *               this string is searched for in names, authors, and descriptions.
     * @param site the site to limit the search by, or -1 for global.
     * @param parentId the root of the subtree of contents to limit the search by, or -1 for all
     * @return a list of Multimedia-objects matching the given criteria
     */
    public List<Multimedia> searchMultimedia(String phrase, int site, int parentId);


    /**
     * Move a multimedia object
     * @param multimediaId - Id of object to be moved
     * @param newParentId - Ny plassering for objekt
     */
    public void moveMultimedia(int multimediaId, int newParentId);



    public int setMultimedia(Multimedia multimedia) throws SystemException;
}
