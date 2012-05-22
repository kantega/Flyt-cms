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
 * Interface som representerer et antall treff for et søk på et gitt term i indeksen.
 *
 * Date: Dec 1, 2008
 * Time: 3:50:23 PM
 *
 * @author Tarje Killingberg
 */
public interface HitCount {

    public static final String FIELD_OTHER = "other";


    /**
     * Returnerer hvilket felt i indeksen dette HitCount-objektet gjelder for.
     * @return hvilket felt i indeksen dette HitCount-objektet gjelder for
     */
    public String getField();

    /**
     * Returnerer hvilket felt i indeksen dette HitCount-objektet gjelder for - oversatt til noe som gir mening.
     * @return hvilket felt i indeksen dette HitCount-objektet gjelder for - oversatt til noe som gir mening
     */
    public String getFieldTranslated();

    /**
     * Returnerer termen dette HitCount-objektet gjelder for.
     * @return termen dette HitCount-objektet gjelder for
     */
    public String getTerm();

    /**
     * Returnerer termen dette HitCount-objektet gjelder for - oversatt til noe som gir mening.
     * @return termen dette HitCount-objektet gjelder for - oversatt til noe som gir mening
     */
    public String getTermTranslated();

    /**
     * Returnerer antall søketreff på denne HitCount'ens term.
     * @return antall søketreff på denne HitCount'ens term
     */
    public int getHitCount();

    /**
     * Returnerer antall millisekunder brukt på å finne antall treff på denne HitCount'ens term.
     * @return antall millisekunder brukt på å finne antall treff på denne HitCount'ens term
     */
    public long getTime();
    
}
