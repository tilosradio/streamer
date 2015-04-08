package hu.tilos.radio.backend.converters;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class HTMLSanitizerTest {

    @Test
    public void testClean() throws Exception {
        //given
        String html = "<div><p><b>asd</b></p></div><script type=\"javascript\">alert('asd');</script>";

        //when
        String cleanHtml = new HTMLSanitizer().clean(html);

        //then
        Assert.assertEquals("<div><p><b>asd</b></p></div>", cleanHtml);
    }

    @Test
    public void testBold() throws Exception {
        //given
        String html = "<img src=\"asd\"/>";

        //when
        String cleanHtml = new HTMLSanitizer().clean(html);

        //then
        Assert.assertEquals("<img src=\"asd\" />", cleanHtml);
    }
}