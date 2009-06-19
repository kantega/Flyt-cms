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

import no.kantega.publishing.store.StoreProvider;
import no.kantega.publishing.store.ProductCategory;
import no.kantega.publishing.spring.RootContext;

import javax.servlet.jsp.jstl.core.LoopTagSupport;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspException;
import java.util.Iterator;


import org.apache.log4j.Logger;

public class ForEachProduct extends LoopTagSupport {
    private String store;
    private ProductCategory category;

    private Iterator i;

    private Logger log = Logger.getLogger(getClass());

    protected void prepare() throws JspTagException {
        StoreProvider store = (StoreProvider) RootContext.getInstance().getBean(this.store, StoreProvider.class);
        i = store.getProductsInCategory(category).iterator();
    }

    protected Object next() throws JspTagException {
        return i.next();
    }

    protected boolean hasNext() throws JspTagException {
        return i.hasNext();
    }

    public void setStore(String store) {
        this.store = store;
    }

    public void setCategory(ProductCategory category) {
        this.category = category;
    }


}
