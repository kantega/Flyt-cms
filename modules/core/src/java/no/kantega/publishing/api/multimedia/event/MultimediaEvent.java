package no.kantega.publishing.api.multimedia.event;

import no.kantega.publishing.common.data.Multimedia;

public class MultimediaEvent {
    public final Multimedia multimedia;

    public MultimediaEvent(Multimedia multimedia) {
        this.multimedia = multimedia;
    }

    public Multimedia getMultimedia() {
        return multimedia;
    }
}
