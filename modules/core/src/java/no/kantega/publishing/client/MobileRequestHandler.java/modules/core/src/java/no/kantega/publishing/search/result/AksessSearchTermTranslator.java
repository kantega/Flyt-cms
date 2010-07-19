package no.kantega.publishing.search.result;

import no.kantega.commons.log.Log;
import no.kantega.publishing.common.ao.ContentAO;
import no.kantega.publishing.common.cache.ContentTemplateCache;
import no.kantega.publishing.common.cache.DocumentTypeCache;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.ContentIdentifier;
import no.kantega.publishing.common.data.ContentTemplate;
import no.kantega.publishing.common.data.DocumentType;
import no.kantega.search.index.Fields;
import no.kantega.search.result.TermTranslator;
import no.kantega.search.result.TermTranslatorDefaultImpl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 *
 */
public class AksessSearchTermTranslator implements TermTranslator {

    private static final String SOURCE = TermTranslatorDefaultImpl.class.getName();

    private DateFormat fromFormat = new SimpleDateFormat("yyyyMMddHHmm");
    private DateFormat toFormat = new SimpleDateFormat("dd.MM.yy");

    public String fromField(String field) {
        String retVal = "search." + field;
        if (Fields.CONTENT_TEMPLATE_ID.equals(field)) {
            retVal = "search.contenttemplate";
        } else if (Fields.DOCTYPE.equals(field)) {
            retVal = "search.contenttype";
        } else if (Fields.DOCUMENT_TYPE_ID.equals(field)) {
            retVal = "search.documenttype";
        } else if (Fields.CONTENT_PARENTS.equals(field)) {
            retVal = "search.parents";
        } else if (Fields.LAST_MODIFIED.equals(field)) {
            retVal = "search.lastmodified";
        }
        return retVal;
    }


    public String fromTerm(String field, String term) {
        String retVal = null;

        if (Fields.CONTENT_PARENTS.equals(field)) {
            retVal = lookupContentTitle(term);
        } else if (Fields.DOCUMENT_TYPE_ID.equals(field)) {
            retVal = lookupDocumentType(term);
        } else if (Fields.CONTENT_TEMPLATE_ID.equals(field)) {
            retVal = lookupContentTemplate(term);
        } else if (Fields.LAST_MODIFIED.equals(field)) {
            retVal = lookupDate(term);
        }

        return retVal != null ? retVal : term;
    }

    private String lookupContentTemplate(String contentTemplateId) {
        String retVal = null;
        try {
            int id = Integer.parseInt(contentTemplateId);
            ContentTemplate contentTemplate = ContentTemplateCache.getTemplateById(id);
            if (contentTemplate != null) {
                retVal = contentTemplate.getName();
            }

        } catch (NumberFormatException nfe) {
            Log.error(SOURCE, nfe, null, null);
        }
        return retVal;
    }

    private String lookupDocumentType(String documentTypeId) {
        String retVal = null;
        try {
            int id = Integer.parseInt(documentTypeId);
            DocumentType docType = DocumentTypeCache.getDocumentTypeById(id);
            if (docType != null) {
                retVal = docType.getName();
            }

        } catch (NumberFormatException nfe) {
            Log.error(SOURCE, nfe, null, null);
        }
        return retVal;
    }

    private String lookupContentTitle(String term) {
        String retVal = null;
        ContentIdentifier cid = new ContentIdentifier();
        cid.setAssociationId(Integer.parseInt(term));
        Content content = ContentAO.getContent(cid, false);
        if (content != null) {
            return content.getTitle();
        }
        return retVal;
    }

    /**
     * Converts Lucene date term to more human readable form
     * eg  "[200801010000 TO 200901010000]" -> last year
     * @param term - term
     * @return
     */
    private String lookupDate(String term) {
        String retVal = term;
        try {
            String termTrimmed = term.replaceAll("[\\[\\]]", "");
            String[] dates = termTrimmed.split("TO");
            retVal = findMatchingDate(dates[0].trim(), dates[1].trim());
        } catch (Exception e) {
            Log.error(SOURCE, e, null, null);
        }
        return retVal;
    }

    private String findMatchingDate(String from, String to) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
        Calendar calToday = new GregorianCalendar();
        int day = calToday.get(Calendar.DAY_OF_MONTH);
        int mnt = calToday.get(Calendar.MONTH);
        int year = calToday.get(Calendar.YEAR);
        int hour = calToday.get(Calendar.HOUR);
        int minute = calToday.get(Calendar.MINUTE);

        // hack
        if (hour == 0 && minute < 10) {
            calToday = new GregorianCalendar(year, mnt, day);
            calToday.add(Calendar.MINUTE, -20);
        } else {
            calToday = new GregorianCalendar(year, mnt, day, 23, 59, 59);
        }

        Date today = calToday.getTime();

        Calendar c = Calendar.getInstance();
        c.setTime(today); // dagens dato

        String terms[] = new String[10]; // antall interval

        // sist uke
        terms[1] = format.format(c.getTime());
        c.add(Calendar.DATE, -8);
        terms[0]  = format.format(c.getTime());

        // sist mnd
        c.setTime(today);
        terms[3] = format.format(c.getTime());
        c.add(Calendar.MONTH, -1);
        terms[2] = format.format(c.getTime());

        // sist år
        c.setTime(today);
        terms[5] = format.format(c.getTime());
        c.add(Calendar.YEAR, -1);
        terms[4] = format.format(c.getTime());

        // siste 2 år
        c.setTime(today);
        c.add(Calendar.YEAR, -1);
        terms[7] = format.format(c.getTime());
        c.add(Calendar.YEAR, -2);
        terms[6] = format.format(c.getTime());

        // eldre enn 3 år
        c.setTime(today);
        c.add(Calendar.YEAR, -3);
        terms[9] = format.format(c.getTime());
        c.add(Calendar.YEAR, -(year - 1970));
        terms[8] = format.format(c.getTime());

        for (int i = 0; i < 10; i += 2) {
            if (terms[i].equalsIgnoreCase(from) && terms[i + 1].equalsIgnoreCase(to)) {
                String retVal = "";
                switch (i) {
                    case 0:
                        retVal = "search.lastweek";
                        break;
                    case 2:
                        retVal = "search.lastmonth";
                        break;
                    case 4:
                        retVal = "search.lastyear";
                        break;
                    case 6:
                        retVal = "search.lessthan3years";
                        break;
                    case 8:
                        retVal = "search.morethan3years";
                        break;
                }
                return retVal;
            }
        }

        try {
            Date fromDate = fromFormat.parse(from);
            Date toDate = fromFormat.parse(to);
            return toFormat.format(fromDate) + " - " + toFormat.format(toDate);
        } catch (Exception e) {
            return "";
        }
    }

}

