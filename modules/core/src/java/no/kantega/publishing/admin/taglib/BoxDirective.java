package no.kantega.publishing.admin.taglib;


import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;

import java.io.IOException;
import java.io.Writer;

/**
 * Velocity port of the {@link BoxTag}
 */
public class BoxDirective extends Directive {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "box";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getType() {
        return BLOCK;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean render(InternalContextAdapter context, Writer writer, Node node) throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {
        writer.write("<div class=\"roundCorners\"><div class=\"top\"><div class=\"corner\"></div></div><div class=\"body\"><div class=\"left\"><div class=\"right\">");
        node.jjtGetChild(0).render(context, writer);
        writer.write("</div></div></div><div class=\"bottom\"><div class=\"corner\"></div></div></div>");
        return true;
    }
}
