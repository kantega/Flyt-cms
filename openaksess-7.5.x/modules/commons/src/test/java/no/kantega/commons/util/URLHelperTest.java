package no.kantega.commons.util;

import org.junit.Test;

import static no.kantega.commons.util.URLHelper.getValidUrl;
import static org.junit.Assert.assertEquals;

public class URLHelperTest {

    @Test
    public void shouldGenerateValidUrlWhenBothHaveSlash(){
        assertEquals("/a/b", getValidUrl("/a/", "/b"));
    }

    @Test
    public void shouldGenerateValidUrlWhenFirstHaveSlash(){
        assertEquals("/a/b", getValidUrl("/a/", "b"));
    }

    @Test
    public void shouldGenerateValidUrlWhenSecondHaveSlash(){
        assertEquals("/a/b", getValidUrl("/a", "/b"));
    }

    @Test
    public void shouldGenerateValidUrlWhenNoneHaveSlash(){
        assertEquals("/a/b", getValidUrl("/a", "b"));
    }
}
