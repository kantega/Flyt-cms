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

package no.kantega.commons.util;

import junit.framework.TestCase;

/**
 *
 */
public class StringHelperTest extends TestCase{

    public void testMakeLinksWithHttp(){
        String input = "Her kommer en link http://www.vg.no som skal testes";
        String expected = "Her kommer en link <a href=\"http://www.vg.no\">http://www.vg.no</a> som skal testes";
        String result = StringHelper.makeLinks(input);
        assertEquals(expected,result);
    }

    public void testMakeLinksWithWWW(){
        String input = "Her kommer en link www.vg.no som skal testes";
        String expected = "Her kommer en link <a href=\"http://www.vg.no\">www.vg.no</a> som skal testes";
        String result = StringHelper.makeLinks(input);
        assertEquals(expected,result);
    }

    public void testMakeLinksWithMail(){
        String input = "Her kommer en link test@test.no som skal testes";
        String expected = "Her kommer en link <a href=\"mailto:test@test.no\">test@test.no</a> som skal testes";
        String result = StringHelper.makeLinks(input);
        assertEquals(expected,result);
    }

    public void testMakeLinksFull(){
        String input = "Her er en tekst med alle 3 mulige link former www.db.no," +
                       " kommer først, dernest så kommer http://kantega.no." +
                       " Ev. spørsmål sendes til test@test.no";
        String expected = "Her er en tekst med alle 3 mulige link former <a href=\"http://www.db.no\">www.db.no</a>," +
                         " kommer først, dernest så kommer <a href=\"http://kantega.no\">http://kantega.no</a>." +
                         " Ev. spørsmål sendes til <a href=\"mailto:test@test.no\">test@test.no</a>" ;
        String result = StringHelper.makeLinks(input);
        assertEquals(expected,result);
    }

    public void testIsNumeric() {
        assertEquals(StringHelper.isNumeric("1234"), true);
        assertEquals(StringHelper.isNumeric("abc123"), false);
        assertEquals(StringHelper.isNumeric("abc123"), false);
        assertEquals(StringHelper.isNumeric("1"), true);
        assertEquals(StringHelper.isNumeric("a"), false);
        assertEquals(StringHelper.isNumeric("open aksess"), false);
    }
}
