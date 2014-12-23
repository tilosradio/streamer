package hu.tilos.radio.backend.util;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class TextConverterTest {

    @Test
    public void youtubize() throws Exception {
        //given
        String video = "asd\nhttps://www.youtube.com/watch?v=7LSaATzQfTE";

        //when
        String result = new TextConverter().youtubize(video);

        //then

        Assert.assertEquals("asd\n<iframe width=\"420\" height=\"315\" src=\"https://www.youtube.com/embed/7LSaATzQfTE\" frameborder=\"0\" allowfullscreen></iframe>",result);

    }
}