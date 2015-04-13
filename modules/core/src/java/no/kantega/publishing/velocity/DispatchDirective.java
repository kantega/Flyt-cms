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

import no.kantega.commons.filter.AksessRequestFilter;
import no.kantega.publishing.common.util.CharResponseWrapper;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.ASTReference;
import org.apache.velocity.runtime.parser.node.ASTStringLiteral;
import org.apache.velocity.runtime.parser.node.Node;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

/**
 */
public class DispatchDirective extends Directive {
    public String getName() {
        return "dispatch";
    }

    public int getType() {
        return LINE;
    }

    public boolean render(InternalContextAdapter internalContextAdapter, Writer writer, Node node) throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {
        final Node firstNode = node.jjtGetChild(0);
        String name;
        if(firstNode instanceof ASTStringLiteral) {
            ASTStringLiteral nameNode = (ASTStringLiteral) firstNode;
            name = nameNode.literal();

            name = name.substring(1, name.length()-1);
        } else if (firstNode instanceof  ASTReference) {
            ASTReference nameNode = (ASTReference) node.jjtGetChild(0);
            StringWriter sw = new StringWriter();
            firstNode.render(internalContextAdapter, sw);
            name = sw.toString();
        } else {
            throw new IllegalArgumentException("Unknown Velocity node type " + firstNode.getClass().getName());
        }
        final HttpServletRequest request = AksessRequestFilter.getRequest();
        final CharResponseWrapper response = new CharResponseWrapper(AksessRequestFilter.getResponse());

        try {
            request.getRequestDispatcher(name).forward(request, response);
        } catch (ServletException e) {
            throw new RuntimeException("Exception dispatching request to " + name, e);
        }

        writer.write(response.toString());
        return false;
    }
    

}
