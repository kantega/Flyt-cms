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

package no.kantega.publishing.admin.util;

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

/**
 *
 */
public class HtmlFilter extends XMLFilterImpl {

    Stack stack = new Stack();

    public void startElement(String string, String localName, String string2, Attributes attributes) throws SAXException {

        if(localName.equalsIgnoreCase("img")) {
            AttributesImpl attr = new AttributesImpl();
            //attr.addAttribute("", "name", "name", "CDATA", attributes.getValue("name"));
            super.startElement("", "a", "a", attr);
            super.endElement("", "a", "a");
        } else {
            super.startElement(string,  localName, string2, attributes);
        }
    }

    public void endElement(String string, String localname, String string2) throws SAXException {
        if(localname.equalsIgnoreCase("img")) {

        } else {
            super.endElement(string, localname, string2);    //To change body of overridden methods use File | Settings | File Templates.
        }
    }

//    public static void main(String[] args) throws TransformerConfigurationException, SAXException, IOException {
//
//
//        //System.setProperty("org.xml.sax.driver", "org.apache.xerces.parsers.SAXParser");
//        SAXParser parser = new SAXParser();
//        parser.setFeature("http://cyberneko.org/html/features/balance-tags/document-fragment", true);
//
//
//
//        TransformerFactory tFactory = TransformerFactory.newInstance();
//        SAXTransformerFactory factory = (SAXTransformerFactory) tFactory;
//
//        final TransformerHandler mainTransformer = factory.newTransformerHandler();
//
//        HtmlFilter htmlFilter = new HtmlFilter(parser);
//        htmlFilter.setContentHandler(mainTransformer);
//
//
//
//        mainTransformer.setResult(new StreamResult(System.out));
//
//        parser.setContentHandler(htmlFilter);
//        parser.parse(new InputSource(new StringReader("<html><img src=\"ii\" ></html>")));
//
//    }
}
