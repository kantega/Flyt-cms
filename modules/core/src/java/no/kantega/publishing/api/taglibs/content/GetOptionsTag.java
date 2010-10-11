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

package no.kantega.publishing.api.taglibs.content;

import no.kantega.publishing.common.ao.EditableListAO;
import no.kantega.publishing.common.data.enums.Language;
import no.kantega.publishing.common.data.Content;
import no.kantega.commons.log.Log;

import javax.servlet.jsp.jstl.core.LoopTagSupport;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspException;
import java.util.Iterator;
import java.util.Collection;

public class GetOptionsTag  extends LoopTagSupport {

    private static final String SOURCE = "aksess.GetOptionsTag";
    private Iterator i;
    private String key;
    private int language = Language.NORWEGIAN_BO;
    private boolean ignorevariant = true;

    protected Object next() throws JspTagException {
        if(i != null) {
            return i.next();
        }
        return null;
    }

    protected boolean hasNext() throws JspTagException {
        if(i == null){
            return false;
        }
        return i.hasNext();
    }

    protected void prepare() throws JspTagException {
        Content content = (Content) pageContext.getRequest().getAttribute("aksess_this");
        language = content.getLanguage();
        
        Collection options = EditableListAO.getOptions(key, Language.getLanguageAsLocale(language), ignorevariant);
        if(options != null) {
            i = options.iterator();
        }
    }


    public void setKey(String key) {
        this.key = key;
    }


    public void setLanguage(int language) {
        this.language = language;
    }

    public void setIgnorevariant(boolean ignorevariant) {
        this.ignorevariant = ignorevariant; 
    }
}
