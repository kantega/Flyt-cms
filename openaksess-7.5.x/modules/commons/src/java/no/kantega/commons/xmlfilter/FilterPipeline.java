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

package no.kantega.commons.xmlfilter;

import no.kantega.commons.exception.SystemException;
import no.kantega.commons.log.Log;
import org.apache.xalan.processor.TransformerFactoryImpl;
import org.apache.xml.serializer.OutputPropertiesFactory;
import org.cyberneko.html.parsers.SAXParser;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLFilter;
import org.xml.sax.helpers.XMLFilterImpl;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FilterPipeline extends XMLFilterImpl {
    private static final String SOURCE = "FilterPipeline";

    List filters = new ArrayList();

    public void addFilter(XMLFilterImpl filter) {
        if (filters.size() == 0) {
            setContentHandler(filter);
        } else {
            XMLFilter parent = (XMLFilter)filters.get(filters.size() - 1);
            parent.setContentHandler(filter);
            filter.setParent(parent);
        }
        filters.add(filter);
    }

    public void setEnd(ContentHandler end) {
        if (filters.size() > 0) {
            XMLFilter parent = (XMLFilter)filters.get(filters.size() - 1);
            parent.setContentHandler(end);
        }
    }

    public void filter(Reader reader, Writer writer) throws SystemException {
        filter(reader, writer, "html");
    }

    public void filter(Reader reader, Writer writer, String method) throws SystemException {

        try {

            System.setProperty("org.xml.sax.driver", "org.apache.xerces.parsers.SAXParser");
            SAXParser parser = new SAXParser();
            parser.setFeature("http://cyberneko.org/html/features/balance-tags/document-fragment", true);
            parser.setProperty("http://cyberneko.org/html/properties/names/elems", "match");


            final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();

            URL resourceUrl = contextClassLoader.getResource("no/kantega/xml/serializer/XMLEntities.properties");

            SAXTransformerFactory factory = (SAXTransformerFactory) TransformerFactoryImpl.newInstance();;

            final TransformerHandler mainTransformer = factory.newTransformerHandler();
            mainTransformer.getTransformer().setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            mainTransformer.getTransformer().setOutputProperty(OutputKeys.METHOD, method);
            mainTransformer.getTransformer().setOutputProperty(OutputKeys.INDENT, "no");
            mainTransformer.getTransformer().setOutputProperty(OutputPropertiesFactory.S_KEY_ENTITIES, resourceUrl.toString());
            mainTransformer.setResult(new StreamResult(writer));

            this.setEnd(mainTransformer);
            if(filters.size() == 0) {
                this.setContentHandler(mainTransformer);
            }

            parser.setContentHandler(this);
            parser.parse(new InputSource(reader));
        } catch (Exception e) {
            Log.error(SOURCE, e, null, null);
            if (e instanceof InvocationTargetException) {
                e.getCause().printStackTrace();
            } else {
                e.printStackTrace();
            }
            throw new SystemException("", SOURCE, e);
        }
    }

    public void removeFilters() {
        filters = new ArrayList();
    }
}
