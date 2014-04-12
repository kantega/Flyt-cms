package no.kantega.publishing.event.impl;

import no.kantega.publishing.api.multimedia.event.MultimediaEvent;
import no.kantega.publishing.api.multimedia.event.MultimediaEventListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingMultimediaEventListener extends MultimediaEventListenerAdapter {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void beforeSetMultimedia(MultimediaEvent event) {
        log.debug("Before setMultimedia, {}, ({})", event.multimedia.getName(), event.multimedia.getId());
    }

    @Override
    public void afterSetMultimedia(MultimediaEvent event) {
        log.debug("After setMultimedia, {}, ({})", event.multimedia.getName(), event.multimedia.getId());

    }

    @Override
    public void beforeMoveMultimedia(MultimediaEvent event) {
        log.debug("Before moveMultimedia, {}, ({})", event.multimedia.getName(), event.multimedia.getId());

    }

    @Override
    public void afterMoveMultimedia(MultimediaEvent event) {
        log.debug("After moveMultimedia, {}, ({})", event.multimedia.getName(), event.multimedia.getId());
    }

    @Override
    public void beforeDeleteMultimedia(MultimediaEvent event) {
        log.debug("Before deleteMultimedia, {}, ({})", event.multimedia.getName(), event.multimedia.getId());
    }

    @Override
    public void afterDeleteMultimedia(MultimediaEvent event) {
        log.debug("After deleteMultimedia, {}, ({})", event.multimedia.getName(), event.multimedia.getId());
    }
}
