package org.kantega.openaksess.plugins.pdf;

import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;

/**
 * Create a PDF document using Apache FOP
 */
public class FOPPDFGenerator implements PDFGenerator, ServletContextAware {
    private ServletContext servletContext;

    private FopFactory fopFactory = FopFactory.newInstance();
    private TransformerFactory tFactory = TransformerFactory.newInstance();

    public byte[] createPDF(String xml, String xslFoUrl) throws Exception {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        //Setup FOP
        Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, out);

        //Setup Transformer
        Source xsltSrc = new StreamSource(servletContext.getResourceAsStream(xslFoUrl));
        Transformer transformer = tFactory.newTransformer(xsltSrc);

        //Make sure the XSL transformation's result is piped through to FOP
        Result res = new SAXResult(fop.getDefaultHandler());

        //Setup input
        Source src = new StreamSource(new StringReader(xml));

        //Start the transformation and rendering process
        transformer.transform(src, res);

        return out.toByteArray();
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

}
