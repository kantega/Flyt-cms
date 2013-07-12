package no.kantega.publishing.common;

import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Enumeration;

/**
 * Created by IntelliJ IDEA.
 * User: hareve
 * Date: Oct 29, 2010
 * Time: 9:40:16 AM
 */
public class StripHTML extends HTMLEditorKit.ParserCallback {
    private StringBuffer sb;
    private String tag = null;
    private boolean skipTags = true;
    private boolean all = false;
    private boolean checkedStart = false;
    private boolean checkedEnd = false;

    public StripHTML() {
        sb = new StringBuffer();
    }

    public String convert(String html) {
        Reader in = new StringReader(html);
        try {
            this.parse(in);
        } catch (Exception e) {

        } finally {
            try {
                in.close();
            } catch (IOException e) {
                //
            }
        }

        return this.getText();
    }

    public void parse(Reader in) throws IOException {
        ParserDelegator delegator = new ParserDelegator();
        delegator.parse(in, this, Boolean.TRUE);
    }

    private boolean skipTag(HTML.Tag t) {
        if (!skipTags) return false;
        
        return (t == HTML.Tag.HTML || t == HTML.Tag.HEAD || t == HTML.Tag.BODY);
    }

    @Override
    public void handleStartTag(HTML.Tag t, MutableAttributeSet a, int pos) {
        if (skipTag(t)) return;

        if (checkedStart && !all) {
            sb.append("<").append(t.toString()).append(getAttributes(a)).append(">");
            return; // only strip first tag
        }

        if (t.toString().equals(tag)) {
            checkedStart = true;
        } else {
            sb.append("<").append(t.toString()).append(getAttributes(a)).append(">");
        }
    }

    @Override
    public void handleEndTag(HTML.Tag t, int pos) {
        if (skipTag(t)) return;

        if (checkedEnd && !all) {
            sb.append("</" + t.toString() + ">");
            return;
        }

        if (t.toString().equals(tag)) {
            checkedEnd = true;
        } else {
            sb.append("</" + t.toString() + ">");
        }

    }

    @Override
    public void handleSimpleTag(HTML.Tag t, MutableAttributeSet a, int pos) {
        if (t.toString().equals(tag)) {
            // do nothing
        } else {
            sb.append("<").append(t.toString()).append(getAttributes(a)).append(">");
        }
    }

    @Override
    public void handleText(char[] text, int pos) {
        sb.append(text);
    }

    public String getText() {
        return sb.toString();
    }

    public void clear() {
        this.checkedStart = false;
        this.checkedEnd = false;
        sb.setLength(0);
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setAll(boolean all) {
        this.all = all;
    }

    public void setSkipTags(boolean skipTags) {
        this.skipTags = skipTags;
    }

    private String getAttributes(AttributeSet attributes) {
        StringBuilder retValue = new StringBuilder();
        Enumeration e = attributes.getAttributeNames();
        while (e.hasMoreElements()) {
            Object name = e.nextElement();
            String value = (String) attributes.getAttribute(name);
            retValue.append(" ").append(name).append("=").append("\"").append(value).append("\"");
        }

        return retValue.toString();
    }
}
