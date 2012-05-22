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

package no.kantega.search.result;

import org.apache.lucene.document.Document;

/**
 * Interface som representerer et søketreff på et dokument i indeksen.
 *
 * Date: Dec 8, 2008
 * Time: 9:25:33 AM
 *
 * @author Tarje Killingberg
 */
public interface DocumentHit {


    public int getDocumentId();

    public Document getDocument();

    public float getScore();

}
