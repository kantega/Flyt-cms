package no.kantega.publishing.common;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

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
            sb.append("<" + t.toString() + ">");
            return; // only strip first tag
        }

        if (t.toString().equals(tag)) {
            checkedStart = true;
        } else {
            sb.append("<" + t.toString() + ">");
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
            sb.append("<" + t.toString() + ">");
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

    public static void main(String args[]) {
        String html = "<p>Dette er en test<br><ul><li>1</li><li>2</li></ul><p>paragraf</p></p>";
        StripHTML parser = new StripHTML();
        parser.tag = "p";
        parser.all = false;
        System.out.println(parser.convert(html));

        parser.clear();
        parser.all = true;
        System.out.println(parser.convert(html));

        parser.clear();
        parser.all = false;
        parser.setSkipTags(false);
        System.out.println(parser.convert(html));
    }
}
