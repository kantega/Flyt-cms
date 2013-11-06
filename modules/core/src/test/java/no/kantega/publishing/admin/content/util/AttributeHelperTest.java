package no.kantega.publishing.admin.content.util;


import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AttributeHelperTest {

    @Test
    public void shouldAcceptAZChars() {
        String expected = "contentAttribute_AbZ";
        assertEquals(expected, AttributeHelper.getInputContainerName("AbZ"));
    }

    @Test
    public void shouldReplaceSpecialChars() {
        String expected = "contentAttribute_A_Z";
        assertEquals(expected, AttributeHelper.getInputContainerName("A!Z"));
    }

    @Test
    public void shouldReplaceDotWith_dot_() {
        String expected = "contentAttribute_A_0__dot_image";
        assertEquals(expected, AttributeHelper.getInputContainerName("A[0].image"));
    }

}
