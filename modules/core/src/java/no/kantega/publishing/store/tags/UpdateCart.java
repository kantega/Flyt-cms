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

package no.kantega.publishing.store.tags;

import org.apache.log4j.Logger;

import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspException;
import javax.servlet.http.HttpServletRequest;

import no.kantega.publishing.store.StoreProvider;
import no.kantega.publishing.store.Cart;
import no.kantega.publishing.store.CartLine;
import no.kantega.publishing.store.util.CartUtils;
import no.kantega.publishing.spring.RootContext;

import java.util.Map;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class UpdateCart extends TagSupport {
    private String store;
    private Logger log = Logger.getLogger(getClass());
    public final String LINE = "line-";

    public int doStartTag() throws JspTagException {
        if (((HttpServletRequest)pageContext.getRequest()).getMethod().equals("POST")) {

            StoreProvider store = (StoreProvider) RootContext.getInstance().getBean(this.store, StoreProvider.class);

            Cart cart = CartUtils.getCart(store, this.store, pageContext.getSession());

            Map params = pageContext.getRequest().getParameterMap();

            List removes = new ArrayList();
            Iterator names = params.keySet().iterator();
            while (names.hasNext()) {
                String name = (String) names.next();
                if(name.startsWith(LINE)) {
                    String[] value = (String[]) params.get(name);

                    int line = Integer.parseInt(name.substring(LINE.length()));

                    if(!value[0].trim().equals("")) {
                        try {
                            int quantity = Integer.parseInt(value[0]);

                            if(quantity == 0) {
                                removes.add(new Integer(line));
                            } else if(quantity > 0) {
                                ((CartLine) cart.getLines().get(line)).updateQuantity(quantity);
                            }

                        } catch (NumberFormatException e) {

                        }
                    }

                }

            }
            for (int i = removes.size() -1; i >= 0; i--) {
                Integer integer = (Integer) removes.get(i);
                cart.getLines().remove(integer.intValue());
            }


        }
        return SKIP_BODY;
    }

    public void setStore(String store) {
        this.store = store;
    }
}
