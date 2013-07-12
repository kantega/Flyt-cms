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

package no.kantega.publishing.velocity;

import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.runtime.parser.node.ASTReference;
import org.apache.velocity.runtime.parser.node.ASTStringLiteral;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.MethodInvocationException;

import javax.servlet.http.HttpServletRequest;
import java.io.Writer;
import java.io.IOException;
import java.io.StringWriter;

import no.kantega.commons.filter.AksessRequestFilter;

/**
 */
public class SectionDirective extends Directive {

    public String getName() {
        return "section";
    }

    public int getType() {
        return BLOCK;
    }

    public boolean render(InternalContextAdapter context, Writer writer, Node node) throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {
        final Node firstNode = node.jjtGetChild(0);
        String name;
        if(firstNode instanceof ASTStringLiteral) {
            ASTStringLiteral nameNode = (ASTStringLiteral) firstNode;
            name = nameNode.literal();

            name = name.substring(1, name.length()-1);
        } else if (firstNode instanceof  ASTReference) {
            ASTReference nameNode = (ASTReference) node.jjtGetChild(0);
            StringWriter sw = new StringWriter();
            firstNode.render(context, sw);
            name = sw.toString();
        } else {
            throw new IllegalArgumentException("Unknown Velocity node type " + firstNode.getClass().getName());
        }

        StringWriter sw = new StringWriter();
        node.jjtGetChild(1).render(context, sw);
        final String attributeKey = "kantega_template_" + name;
        context.put(attributeKey, sw.toString());
        AksessRequestFilter.getRequest().setAttribute(attributeKey, sw.toString());
        return true;
    }
}
