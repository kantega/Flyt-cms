package no.kantega.publishing.api.multimedia;

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
    void deleteMultimedia(int id) throws ObjectInUseException;

    /**
     * Get Multimedia object from database (only metadata is fetched, data is not fetched)
     * @param id - Id of object to retrieve
     * @return - Multimedia
     */
    Multimedia getMultimedia(int id);

    /**
     * Get Multimedia object from database (only metadata is fetched, data is not fetched)
     * @param parentId - parentId of object to retrieve
     * @param name - name of object to retrieve
     * @return - Multimedia
     */
    Multimedia getMultimediaByParentIdAndName(int parentId, String name);


    /**
     * Retrieves an image associated with the user's profile.
     * @param userId -
     * @return - Multimedia
     * @throws SystemException
     */
    Multimedia getProfileImageForUser(String userId);

    /**
     * Stream multimedia data
     * @param id - Id of object to stream data for
     * @param ish - InputStreamHandler to receive data
     */
    void streamMultimediaData(int id, InputStreamHandler ish);


    /**
     * Fetch all multimedia objects with specified parentid
     * @param parentId - parentId
     * @return - List<Multimedia>
     */
    List<Multimedia> getMultimediaList(int parentId);


    /**
     * Fetch all multimedia objects with specified contentId
     * @param contentId - contentId
     * @return - List<Multimedia>
     */
    List<Multimedia> getMultimediaWithContentId(int contentId);

    /**
     * Get number of objects in mediaarchive
     *
     * @return number of objects in mediaarchive
     */
    int getMultimediaCount();


    /**
     * Move a multimedia object
     * @param multimediaId - Id of object to be moved
     * @param newParentId - Ny plassering for objekt
     */
    void moveMultimedia(int multimediaId, int newParentId);



    int setMultimedia(Multimedia multimedia) throws SystemException;
}
