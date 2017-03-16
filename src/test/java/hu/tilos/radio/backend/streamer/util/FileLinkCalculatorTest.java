package hu.tilos.radio.backend.streamer.util;

import hu.tilos.streamer.FileLinkCalculator;
import hu.tilos.streamer.Mp3File;
import hu.tilos.streamer.ResourceCollection;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;

public class FileLinkCalculatorTest {

    private SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMdd HHmmss");

    @Test
    public void getMp3Links() throws Exception {
        //given
        FileLinkCalculator flc = new FileLinkCalculator();
        Date startDate = SDF.parse("20141404 003746");
        System.out.println(startDate);

        //when
        ResourceCollection collections = flc.getMp3Links(startDate, 60 * 60);

        //then
        assertEquals(3, collections.getCollection().size());

        Mp3File mp3File = collections.getCollection().get(0);
        assertEquals(14913456, mp3File.getStartOffset());
        assertEquals(Integer.MAX_VALUE, mp3File.getEndOffset());

        mp3File = collections.getCollection().get(1);
        assertEquals(0, mp3File.getStartOffset());
        assertEquals(Integer.MAX_VALUE, mp3File.getEndOffset());

        mp3File = collections.getCollection().get(2);
        assertEquals(0, mp3File.getStartOffset());
        assertEquals(14913456, mp3File.getEndOffset());

    }


    @Test
    public void getMp3LinksSimple() throws Exception {
        //given
        FileLinkCalculator flc = new FileLinkCalculator();
        Date startDate = SDF.parse("20141404 003746");
        System.out.println(startDate);

        //when
        ResourceCollection collections = flc.getMp3Links(startDate, 77);

        //then
        assertEquals(1, collections.getCollection().size());

        Mp3File mp3File = collections.getCollection().get(0);
        assertEquals(14913456, mp3File.getStartOffset());
        assertEquals(17377697, mp3File.getEndOffset());

    }


}