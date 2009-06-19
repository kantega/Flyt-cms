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

import javax.servlet.jsp.jstl.core.LoopTagSupport;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspException;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import no.kantega.publishing.store.util.CartUtils;
import no.kantega.publishing.store.StoreProvider;
import no.kantega.publishing.spring.RootContext;

/**
 *
 */
public class CartTag extends LoopTagSupport {
    private String store;
    private Logger log = Logger.getLogger(getClass());
    private Iterator i;


    protected Object next() throws JspTagException {
        return i.next();
    }

    protected boolean hasNext() throws JspTagException {
        return i.hasNext();
    }

    protected void prepare() throws JspTagException {
        StoreProvider store = (StoreProvider) RootContext.getInstance().getBean(this.store, StoreProvider.class);
        List l = new ArrayList();
        l.add(CartUtils.getCart(store, this.store, pageContext.getSession()));
        i = l.iterator();
    }

    public void setStore(String store) {
        this.store = store;
    }
}

