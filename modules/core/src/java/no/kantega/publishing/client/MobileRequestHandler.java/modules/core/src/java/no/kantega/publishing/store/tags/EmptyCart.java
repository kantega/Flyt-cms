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

import no.kantega.publishing.store.StoreProvider;
import no.kantega.publishing.store.Cart;
import no.kantega.publishing.store.util.CartUtils;
import no.kantega.publishing.spring.RootContext;

import java.util.Map;
import java.util.Iterator;

/**
 *
 */
public class EmptyCart extends TagSupport {
    private String store;
    private Logger log = Logger.getLogger(getClass());

    public int doStartTag() throws JspTagException {
        StoreProvider store = (StoreProvider) RootContext.getInstance().getBean(this.store, StoreProvider.class);
        Cart cart = CartUtils.getCart(store, this.store, pageContext.getSession());
        cart.clear();
        return SKIP_BODY;
    }

    public void setStore(String store) {
        this.store = store;
    }
}
