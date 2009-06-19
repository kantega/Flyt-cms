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

package no.kantega.publishing.admin.content.htmlfilter;

import org.xml.sax.helpers.XMLFilterImpl;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.XMLReader;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import org.cyberneko.html.parsers.SAXParser;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import java.util.Stack;
import java.io.*;

import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.admin.content.htmlfilter.util.HtmlFilterHelper;

/**
 *
 */
public class MSWordFilter extends XMLFilterImpl {
    private static String[] allowedTags = {"HTML", "BODY", "A", "B", "BLOCKQUOTE", "BR", "CAPTION", "COL", "DD", "DT", "EM", "DFN", "DIV",
                                            "DL", "DT", "EM", "H1", "H2", "H3", "H4", "H5", "H6", "HR", "OL", "P", "PRE",
                                            "STRONG", "SUB", "SUP", "TABLE", "TBODY", "TD", "TFOOT", "TH", "THEAD", "TR", "UL", "U", "LI", "IMG"};


    private boolean ignore(String tag) {
        boolean ignore = true;
        for (int i = 0; i < allowedTags.length; i++) {
            String allowedTag = allowedTags[i];
            if (tag.equalsIgnoreCase(allowedTag)) {
                ignore = false;
            }
        }

        return ignore;
    }

    public void startElement(String string, String localName, String name, Attributes attributes) throws SAXException {

        if (!ignore(name)) {
            // Remove css classes which starts with MS
            String cssclass = attributes.getValue("class");
            if (cssclass != null) {
                cssclass = cssclass.toLowerCase();
                if (cssclass.startsWith("ms")) {
                    attributes = HtmlFilterHelper.removeAttribute("class", attributes);
                }
            }

            // Remove all style
            attributes = HtmlFilterHelper.removeAttribute("style", attributes);            

            super.startElement(string,  localName, name, attributes);
        }
    }

    public void endElement(String string, String localname, String name) throws SAXException {
        if (!ignore(name)) {
            super.endElement(string, localname, name);
        }
    }
}
