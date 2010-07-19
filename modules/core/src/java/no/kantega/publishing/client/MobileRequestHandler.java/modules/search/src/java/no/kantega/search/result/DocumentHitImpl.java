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
import org.apache.lucene.search.Explanation;

/**
 * Date: Dec 11, 2008
 * Time: 11:03:57 AM
 *
 * @author Tarje Killingberg
 */
public class DocumentHitImpl implements DocumentHit {

    private static final String SOURCE = DocumentHitImpl.class.getName();

    private int documentId;
    private Document document;
    private float score;
    private Explanation explanation;


    /**
     * {@inheritDoc}
     */
    public int getDocumentId() {
        return documentId;
    }

    public void setDocumentId(int documentId) {
        this.documentId = documentId;
    }

    /**
     * {@inheritDoc}
     */
    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    /**
     * {@inheritDoc}
     */
    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public Explanation getExplanation() {
        return explanation;
    }

    public void setExplanation(Explanation explanation) {
        this.explanation = explanation;
    }

}
