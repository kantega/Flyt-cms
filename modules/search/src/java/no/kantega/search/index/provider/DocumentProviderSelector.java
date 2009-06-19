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

package no.kantega.search.index.provider;

import no.kantega.search.index.provider.DocumentProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 */
public class DocumentProviderSelector {
    private List providers = new ArrayList();
    public DocumentProvider select(String sourceId) {
        if(sourceId != null) {
            for (int i = 0; i < providers.size(); i++) {
                DocumentProvider provider = (DocumentProvider) providers.get(i);
                if(sourceId.equals(provider.getSourceId())) {
                    return provider;
                }
            }
        }
        return null;
    }

    public DocumentProvider selectByDocumentType(String doctype) {
            if(doctype != null) {
                for (int i = 0; i < providers.size(); i++) {
                    DocumentProvider provider = (DocumentProvider) providers.get(i);
                    if(doctype.equals(provider.getDocumentType())) {
                        return provider;
                    }
                }
            }
            return null;
        }



    public Collection getAllProviders() {
        return providers;
    }

    public void setProviders(List providers) {
        this.providers = providers;
    }
}
