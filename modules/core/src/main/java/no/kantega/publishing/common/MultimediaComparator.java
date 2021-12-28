package no.kantega.publishing.common;

import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.data.enums.MultimediaProperty;

import java.text.Collator;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * User: Terje Røstum, Kantega AS
 * Date: Jan 12, 2010
 * Time: 10:55:35 AM
 */

public class MultimediaComparator  implements Comparator {

    Map contentPages = new HashMap();

    int descending = -1;

    String fieldName = "priority";

    Collator collator = null;

    public MultimediaComparator(String fieldName, boolean descending) {

        collator = Collator.getInstance(Aksess.getDefaultLocale());
        collator.setStrength(Collator.PRIMARY);

        this.fieldName = fieldName;
        if (descending) {
            this.descending = 1;
        }
    }

    private int compareDates(Date d1, Date d2) {
        if (d1 != null && d2 != null) {
            return d2.compareTo(d1)*descending;
        }
        if (d1 != null){
            return 1;
        }
        if (d2 != null){
            return -1;
        }
        return 0;
    }

    private int compareInts(int i1, int i2) {
        if (i1 > i2) {
            return 1;
        } else if (i1 < i2) {
            return -1;
        }
        return 0;
    }

    private int compareStrings(String s1, String s2) {
        if (s1 != null && s2 != null) {
            return collator.compare(s2, s1)*descending;
        }
        return 0;
    }

    public int compare(Object v1, Object v2) {
        if (v1 instanceof Multimedia && v2 instanceof Multimedia) {
            Multimedia c1 = (Multimedia)v1;
            Multimedia c2 = (Multimedia)v2;

            if (fieldName.equalsIgnoreCase(MultimediaProperty.TITLE)) {
                return compareStrings(c1.getName(), c2.getName());
            } else if (fieldName.equalsIgnoreCase(MultimediaProperty.PUBLISH_DATE)) {
                return compareInts(c1.getId(), c2.getId());
            } else if (fieldName.equalsIgnoreCase(MultimediaProperty.LAST_MODIFIED)) {
                return compareDates(c1.getLastModified(), c2.getLastModified());
            }
        }
        return 0;
    }
}

