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

package no.kantega.commons.util;

import no.kantega.commons.exception.InvalidFileException;
import no.kantega.commons.exception.SystemException;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.w3c.dom.*;
import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.URL;


/**
 *
 */
public class XMLHelper {
    private static final Logger log = LoggerFactory.getLogger(XMLHelper.class);


    public static Document getDocument(String input) throws SystemException {
        Document doc = null;
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = docFactory.newDocumentBuilder();
            doc = builder.parse(new InputSource(new StringReader(input)));
        } catch (Exception e) {
            log.error("Error converting String to Document", e);
            throw new SystemException("Error converting String to Document", e);
        }

        return doc;
    }

    public static String getString(Document doc, Element element) throws SystemException {
        StringWriter stringOut = new StringWriter();
        try {
            OutputFormat format = new OutputFormat(doc);
            XMLSerializer serial = new XMLSerializer(stringOut, format);
            serial.asDOMSerializer();
            serial.serialize(element);
        } catch (IOException e) {
            log.error("Error converting Document to String", e);
            throw new SystemException("Error converting Document to String", e);
        }

        return stringOut.toString();
    }


    public static String getString(Document doc) throws SystemException {
        StringWriter stringOut = new StringWriter();
        try {
            OutputFormat format = new OutputFormat(doc);
            XMLSerializer serial = new XMLSerializer(stringOut, format);
            serial.asDOMSerializer();
            serial.serialize(doc.getDocumentElement());
        } catch (IOException e) {
            log.error("Error converting String to Document", e);
            throw new SystemException("Error converting String to Document", e);
        }

        return stringOut.toString();
    }


    public static Document newDocument() throws SystemException {
        Document doc = null;
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = docFactory.newDocumentBuilder();
            doc = builder.newDocument();
        } catch (Exception e) {
            log.error("Error creating new XML document", e);
            throw new SystemException("Error creating new XML document", e);
        }

        return doc;
    }

    public static Document openDocument(URL url) throws SystemException {
        Document doc = null;
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = docFactory.newDocumentBuilder();

            try (InputStream is = url.openStream()){
                doc = builder.parse(is);
            }
        } catch (Exception e) {
            log.error("Error opening XML document from URL", e);
            throw new SystemException("Error opening XML document from URL", e);
        }

        return doc;
    }

    public static Document openDocument(Resource resource) throws InvalidFileException {
        try {
            return openDocument(resource.getInputStream());
        } catch (IOException e) {
            throw new InvalidFileException("Error opening XML document from Resource", e);
        }
    }

    public static Document openDocument(Resource resource, EntityResolver er) throws InvalidFileException {
        try {
            return openDocument(resource.getInputStream(), er, resource.getURI().getRawPath());
        } catch (IOException e) {
            throw new InvalidFileException("Error opening XML document from Resource", e);
        }
    }

    public static Document openDocument(InputStream is) throws SystemException {
        try {
            return openDocument(is, null);
        } catch (Exception e) {
            throw new SystemException("Error opening XML document from InputStream", e);
        }
    }

    public static Document openDocument(InputStream is, EntityResolver er, String systemId) throws SystemException {
        Document doc = null;
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            docFactory.setExpandEntityReferences(false);
            DocumentBuilder builder = docFactory.newDocumentBuilder();
            if (er != null) {
                builder.setEntityResolver(er);
            }
            if (systemId != null) {
                doc = builder.parse(is, systemId);
            } else {
                doc = builder.parse(is);
            }
        } catch (Exception e) {
            throw new SystemException("Error opening XML document from InputStream", e);
        }

        return doc;
    }

    public static Document openDocument(InputStream is, EntityResolver er) throws SystemException {
        return openDocument(is, er, null);
    }


    public static Document openDocument(File file) throws InvalidFileException {
        try {
            return  openDocument(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new InvalidFileException("Error opening XML document from File", e);
        }
    }


    public static void saveDocument(Document doc, File file) throws SystemException {
        try {
            FileOutputStream fileOut = new FileOutputStream(file);
            try {
                OutputFormat format = new OutputFormat(doc);
                XMLSerializer serial = new XMLSerializer(fileOut, format);
                serial.asDOMSerializer();
                serial.serialize(doc);
            } catch (IOException e) {
                throw new SystemException("Error saving Document to File", e);
            }

        } catch (Exception e) {
            throw new SystemException("Error saving Document to File", e);
        }
    }


    public static Element getChildByName(Element parent, String name) {
        NodeList children = parent.getChildNodes();
        if (children == null) return null;

        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeName().equalsIgnoreCase(name)) {
                return (Element) child;
            }
        }

        return null;
    }


    public static void removeChild(Element parent, String name) {
        NodeList children = parent.getChildNodes();
        if (children == null) return;

        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeName().equalsIgnoreCase(name)) {
                parent.removeChild(child);
            }
        }
    }


    public static Element setChild(Document doc, Document parent, String name ) {
        Element child = doc.getDocumentElement();
        if (child == null) {
            child = doc.createElement(name);
            parent.appendChild(child);
        }
        return child;
    }


    public static Element setChild(Document doc, Element parent, String name ) {
        if (parent == null) {
            return null;
        }
        Element child = getChildByName(parent, name);
        if (child == null) {
            child = doc.createElement(name);
            parent.appendChild(child);
        }
        return child;
    }


    public static Element setChildText(Document doc, Element parent, String name, String value) {
        Element child = getChildByName(parent, name);
        if (child == null) {
            child = doc.createElement(name);
            child.appendChild(doc.createTextNode(value == null ? "" : value));
            parent.appendChild(child);
        } else {
            Node text = child.getFirstChild();
            if (text != null) {
                text.setNodeValue(value);
            } else {
                child.appendChild(doc.createTextNode(value == null ? "" : value));
            }
        }
        return child;
    }
    public static AttributesImpl getAttributesImpl(Attributes attributes) {
        AttributesImpl impl = new AttributesImpl();

        for(int i = 0; i < attributes.getLength(); i++) {
            impl.addAttribute(attributes.getURI(i), attributes.getLocalName(i), attributes.getQName(i), attributes.getType(i), attributes.getValue(i));
        }
        return impl;
    }

    public static String getText(Element element) {
        StringBuilder buffer = new StringBuilder();
        NodeList children = element.getChildNodes();
        for(int i = 0; i < children.getLength(); i++) {
            if(children.item(i) instanceof Text) {
                buffer.append(((Text)children.item(i)).getData());
            }
        }
        return buffer.toString();
    }

    public static String getText(Element element, String child) {
        String text = "";
        NodeList children = element.getElementsByTagName(child);
        if(children.getLength() > 0) {
                text = getText((Element) children.item(0));
        }

        return text;
    }

}
