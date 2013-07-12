/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.kantega.publishing.common;

import no.kantega.commons.exception.SystemException;
import no.kantega.commons.log.Log;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.common.ao.ContentAO;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.attributes.Attribute;
import no.kantega.publishing.common.data.attributes.ContentlistAttribute;
import no.kantega.publishing.common.data.attributes.DateAttribute;
import no.kantega.publishing.common.data.attributes.NumberAttribute;
import no.kantega.publishing.common.data.enums.AttributeDataType;
import no.kantega.publishing.common.data.enums.ContentProperty;

import java.text.Collator;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Compare content objects based on a given fieldname.
 */
public class ContentComparator implements Comparator<Content> {
    private static final String SOURCE = "aksess.ContentComparator";

    Map contentPages = new HashMap();

    int descending = -1;

    String fieldName = "priority";

    Collator collator = null;

    public ContentComparator(String fieldName, boolean descending) {

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

    private String getContentTitle(String id) {
        if (id == null || id.length() < 1){
            return "";
        }
        Content cp = (Content)contentPages.get(id);
        if (cp == null) {
            id = (id.contains(",")) ? id.substring(0,id.indexOf(",")) : id;

            ContentIdentifier cid = new ContentIdentifier();
            try {
                cid.setAssociationId(Integer.parseInt(id, 10));
                cp = ContentAO.getContent(cid, false);
                contentPages.put(id, cp);
            } catch (SystemException e) {
                Log.error(SOURCE, e, null, null);
            } catch (NumberFormatException e) {
                Log.error(SOURCE, e, null, null);
            }
        }

        return (cp == null) ? "" : cp.getTitle();
    }

    public int compare(Content c1, Content c2) {
            if (fieldName.equalsIgnoreCase(ContentProperty.TITLE)) {
                return compareStrings(c1.getTitle(), c2.getTitle());
            } else if (fieldName.equalsIgnoreCase(ContentProperty.MODIFIED_BY)) {
                return compareStrings(c1.getModifiedBy(), c2.getModifiedBy());
            } else if (fieldName.equalsIgnoreCase(ContentProperty.PRIORITY)) {
                int p1 = c1.getAssociation().getPriority();
                int p2 = c2.getAssociation().getPriority();
                if (p1 > p2) {
                    return 1;
                } else if (p1 < p2) {
                    return -1;
                }
            } else if (fieldName.equalsIgnoreCase(ContentProperty.EXPIRE_DATE)) {
                return compareDates(c1.getExpireDate(), c2.getExpireDate());
            } else if (fieldName.equalsIgnoreCase(ContentProperty.PUBLISH_DATE)) {
                return compareDates(c1.getPublishDate(), c2.getPublishDate());
            } else if (fieldName.equalsIgnoreCase(ContentProperty.LAST_MODIFIED)) {
                return compareDates(c1.getLastModified(), c2.getLastModified());
            } else if (fieldName.equalsIgnoreCase(ContentProperty.LAST_MAJOR_CHANGE)) {
                return compareDates(c1.getLastModified(), c2.getLastMajorChange());
            } else if (fieldName.equalsIgnoreCase(ContentProperty.REVISION_DATE)) {
                return compareDates(c1.getRevisionDate(), c2.getRevisionDate());
            } else {
                Attribute a1 = c1.getAttribute(fieldName, AttributeDataType.CONTENT_DATA);
                Attribute a2 = c2.getAttribute(fieldName, AttributeDataType.CONTENT_DATA);

                if (a1 != null && a2 != null) {
                    if (a1 instanceof DateAttribute) {
                        Date d1 = ((DateAttribute)a1).getValueAsDate();
                        Date d2 = ((DateAttribute)a2).getValueAsDate();
                        return compareDates(d1, d2);
                    } else if (a1 instanceof ContentlistAttribute) {
                        //orders by the page title and not by its id
                        String title1 = getContentTitle(a1.getValue());
                        String title2 = getContentTitle(a2.getValue());
                        return compareStrings(title1, title2);
                    } else if (a1 instanceof NumberAttribute) {
                        try {
                            int i1 = Integer.parseInt(a1.getValue());
                            int i2 = Integer.parseInt(a2.getValue());
                            return compareInts(i1, i2);
                        } catch (NumberFormatException e) {
                            // Returner 0
                        }
                    } else {
                        return compareStrings(a1.getValue(), a2.getValue());
                    }
                } else {
                    if(a1 == null && a2 == null) {
                        return 0;
                    } else if (a1 == null) {
                        return -1;
                    } else {
                        return 1;
                    }
                }
            }
        return 0;
    }
}
