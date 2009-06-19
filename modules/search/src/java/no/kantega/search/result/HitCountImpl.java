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

/**
 * Date: Dec 2, 2008
 * Time: 1:41:23 PM
 *
 * @author Tarje Killingberg
 */
public class HitCountImpl implements HitCount {

    private static final String SOURCE = HitCountImpl.class.getName();

    private String field;
    private String fieldTranslated;
    private String term;
    private String termTranslated;
    private int hitCount;
    private long time;


    /**
     * {@inheritDoc}
     */
    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    /**
     * {@inheritDoc}
     */
    public String getFieldTranslated() {
        return fieldTranslated;
    }

    public void setFieldTranslated(String fieldTranslated) {
        this.fieldTranslated = fieldTranslated;
    }

    /**
     * {@inheritDoc}
     */
    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    /**
     * {@inheritDoc}
     */
    public String getTermTranslated() {
        return termTranslated;
    }

    public void setTermTranslated(String termTranslated) {
        this.termTranslated = termTranslated;
    }

    /**
     * {@inheritDoc}
     */
    public int getHitCount() {
        return hitCount;
    }

    public void setHitCount(int hitCount) {
        this.hitCount = hitCount;
    }

    /**
     * {@inheritDoc}
     */
    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String toString() {
        return getField() + "(" + getTerm() + "): " + getHitCount() + " (" + getTime() + ")";
    }

}
