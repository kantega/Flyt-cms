package no.kantega.commons.util;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RegExpTest {
    @Test
    public void testValidEmail() {
        assertTrue("anders@kantega.no", RegExp.isEmail("anders@kantega.no"));
        assertTrue("anders.skar@kantega.no", RegExp.isEmail("anders.skar@kantega.no"));
        assertTrue("anders.skar@stud.ntnu.no", RegExp.isEmail("anders.skar@stud.ntnu.no"));

        assertFalse("anders.skar.kantega.no", RegExp.isEmail("anders.skar.kantega.no"));
        assertFalse("anders.skar@kantegano", RegExp.isEmail("anders.skar@kantegano"));
        assertFalse("anders.skar@.kantegano", RegExp.isEmail("anders.skar@.kantegano"));
    }
}
