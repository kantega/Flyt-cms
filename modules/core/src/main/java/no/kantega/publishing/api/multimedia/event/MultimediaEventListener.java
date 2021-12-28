package no.kantega.publishing.api.multimedia.event;

public interface MultimediaEventListener {

    /**
     * Called before a new Multimedia is saved
     * @param event with reference to the new Multimedia object.
     */
    public void beforeSetMultimedia(MultimediaEvent event);

    /**
     * Called after a new Multimedia has been saved
     * @param event with reference to the new Multimedia object.
     */
    public void afterSetMultimedia(MultimediaEvent event);

    /**
     * Called before a Multimedia object is moved
     * @param event with reference to the Multimedia object.
     */
    public void beforeMoveMultimedia(MultimediaEvent event);

    /**
     * Called after a new Multimedia has been moved
     * @param event with reference to the Multimedia object.
     */
    public void afterMoveMultimedia(MultimediaEvent event);

    /**
     * Called before a Multimedia object is deleted
     * @param event with reference to the Multimedia object.
     */
    public void beforeDeleteMultimedia(MultimediaEvent event);

    /**
     * Called after a Multimedia object is deleted
     * @param event with reference to the Multimedia object.
     */
    public void afterDeleteMultimedia(MultimediaEvent event);
}
