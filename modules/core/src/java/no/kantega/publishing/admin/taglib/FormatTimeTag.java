package no.kantega.publishing.admin.taglib;

import no.kantega.publishing.common.Aksess;
import no.kantega.commons.util.LocaleLabels;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Date;

/**
 *
 */
public class FormatTimeTag extends TagSupport {
    Date date = null;

    public void setDate(Date date) {
        this.date = date;
    }

    public int doStartTag() throws JspException {
        JspWriter out;
        try {
            out = pageContext.getOut();

            String dateFormat = Aksess.getDefaultTimeFormat();

            if (date == null) {
                String defaultDate = dateFormat;
                defaultDate = defaultDate.replaceAll("H", LocaleLabels.getLabel("aksess.dateformat.character.hour", Aksess.getDefaultAdminLocale()));
                defaultDate = defaultDate.replaceAll("m", LocaleLabels.getLabel("aksess.dateformat.character.minute", Aksess.getDefaultAdminLocale()));
                out.write(defaultDate);
            } else {
                DateFormat df = new SimpleDateFormat(dateFormat);
                out.write(df.format(date));
            }

        } catch (IOException e) {
            throw new JspException("ERROR: ButtonTag", e);
        }

        date = null;

        return SKIP_BODY;
    }

    public int doEndTag() throws JspException {
        return EVAL_PAGE;
    }

}

