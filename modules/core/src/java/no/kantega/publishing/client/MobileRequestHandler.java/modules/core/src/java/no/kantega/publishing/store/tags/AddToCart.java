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

import no.kantega.publishing.spring.RootContext;
import no.kantega.publishing.store.Cart;
import no.kantega.publishing.store.Product;
import no.kantega.publishing.store.StoreProvider;
import no.kantega.publishing.store.util.CartUtils;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;
import java.util.Iterator;
import java.util.Map;

/**
 *
 */
public class AddToCart extends TagSupport {
    private String store;
    private Logger log = Logger.getLogger(getClass());
    public final String PRODUCT = "product-";

    public int doStartTag() throws JspTagException {
        if(((HttpServletRequest)pageContext.getRequest()).getMethod().equals("POST")) {
            StoreProvider store = (StoreProvider) RootContext.getInstance().getBean(this.store, StoreProvider.class);

            Cart cart = CartUtils.getCart(store, this.store, pageContext.getSession());

            Map params = pageContext.getRequest().getParameterMap();

            Iterator names = params.keySet().iterator();
            while (names.hasNext()) {
                String name = (String) names.next();
                if(name.startsWith(PRODUCT)) {
                    String productId = name.substring(PRODUCT.length());
                    Product product = store.lookupProduct(productId);

                    String[] value = (String[]) params.get(name);

                    if(!value[0].trim().equals("")) {
                        try {
                            int quantity = Integer.parseInt(value[0]);

                            if(quantity > 0) {
                                addToCart(cart, product, quantity, pageContext.getSession());
                            }
                        } catch (NumberFormatException e) {

                        }
                    }

                }

            }

        }
        return SKIP_BODY;
    }

    /**
     * This method may be overridden by subclasses to customize behavior.
     *
     * @param cart     The cart to add items to.
     * @param product  The product to add.
     * @param quantity The quantity to add.
     * @param session  The user's http session object.
     */
    public void addToCart(Cart cart, Product product, int quantity, HttpSession session) {
        cart.addLine(product, quantity);
    }


    public void setStore(String store) {
        this.store = store;
    }
}
