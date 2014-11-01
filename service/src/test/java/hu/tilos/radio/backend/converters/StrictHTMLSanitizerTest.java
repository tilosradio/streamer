package hu.tilos.radio.backend.converters;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class StrictHTMLSanitizerTest {

    @Test
    public void testClean() throws Exception {
        //given
        StrictHTMLSanitizer sanitizer = new StrictHTMLSanitizer();
        String input = "asd<br/><a href=\"asd\">teljes</a><p>asd</p>";

        //when
        String result = sanitizer.clean(input);

        //then
        assertThat(result, equalTo("asd<br />teljesasd"));
    }
}