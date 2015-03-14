package hu.tilos.radio.backend;


import hu.tilos.radio.backend.streamer.LocalBackend;
import hu.tilos.radio.backend.streamer.util.StatToLog;
import junit.framework.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StreamControllerTest {

    public static SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMddHHmm");

    @Test
    public void generateSplittedResources() {
        //given
        StreamController controller = new StreamController(new StatToLog());
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        //when
        controller.generateSplittedResources("/mp3/tilos-20140916-100940-125058.m3u", output);

        //then
        String result = output.toString();

        System.out.println(output);

    }

    @Test
    public void testDoGet() throws IOException, ServletException {
        //given
        StreamController controller = new StreamController(new StatToLog()) {
            @Override
            public ResourceCollection getMp3Links(Date start, int duration) {
                ResourceCollection c = new ResourceCollection();
                c.add(new Mp3File("/a.txt"));
                c.add(new Mp3File("/b.txt"));
                return c;
            }
        };
        controller.setBackend(new LocalBackend("src/test/resources/"));
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);

        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        Mockito.when(req.getRequestURI()).thenReturn("/mp3/tilos-20120405-100000-120000.mp3");

        new File("target").mkdirs();
        //when
        controller.doGet(req, resp, new FileOutputStream("target/test.out"));

        //then


    }

    @Test
    public void testDoGetPartial() throws IOException, ServletException {
        //given
        StreamController controller = new StreamController(new StatToLog()) {
            @Override
            public ResourceCollection getMp3Links(Date start, int duration) {
                ResourceCollection c = new ResourceCollection();
                c.add(new Mp3File("/a.txt"));
                c.add(new Mp3File("/b.txt"));
                return c;
            }
        };
        controller.setBackend(new LocalBackend("src/test/resources/"));
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        Mockito.when(req.getRequestURI()).thenReturn("/mp3/tilos-20120405-100000-120000.mp3");
        Mockito.when(req.getHeader("Range")).thenReturn("bytes=18-");

        new File("target").mkdirs();
        //when
        controller.doGet(req, resp, new FileOutputStream("target/test.out"));

        //then


    }

    @Test
    public void testParse() throws ParseException {
        //given
        StreamController controller = new StreamController(new StatToLog());
        //when
        Segment segment = controller.parse("/mp3/tilos-20131012-200000-230000.mp3");

        //then
        Assert.assertEquals(SDF.parse("201310122000"), segment.start);
        Assert.assertEquals(180 * 60, segment.duration);
    }


    @Test
    public void testParseWithQuery() throws ParseException {
        //given
        StreamController controller = new StreamController(new StatToLog());
        //when
        Segment segment = controller.parse("/mp3/tilos-20131012-200000-230000.mp3?download=true");

        //then
        Assert.assertEquals(SDF.parse("201310122000"), segment.start);
        Assert.assertEquals(180 * 60, segment.duration);
    }


    @Test
    public void testParseOldFormat() throws ParseException {
        //given
        StreamController controller = new StreamController();
        //when
        Segment segment = controller.parse("/mp3/1404763200-135.mp3");

        //then
        Assert.assertEquals(SDF.parse("201407072200"), segment.start);
        Assert.assertEquals(135, segment.duration);
    }

    @Test
    public void testGetPrevHalfHour() throws Exception {
        //given
        StreamController controller = new StreamController(new StatToLog());
        Date start = SDF.parse("201406011234");
        start.setTime(start.getTime() + 100);
        //when
        Date date = controller.getPrevHalfHour(start);

        //then
        Assert.assertEquals(SDF.parse("201406011230"), date);

    }

    @Test
    public void testGetPrevHalfHourExact() throws Exception {
        //given
        StreamController controller = new StreamController();
        Date start = SDF.parse("201406011230");
        //when
        Date date = controller.getPrevHalfHour(start);

        //then
        Assert.assertEquals(SDF.parse("201406011230"), date);

    }

    @Test
    public void testGetPrevHalfHourExactHour() throws Exception {
        //given
        StreamController controller = new StreamController(new StatToLog());
        Date start = SDF.parse("201406011200");
        //when
        Date date = controller.getPrevHalfHour(start);

        //then
        Assert.assertEquals(SDF.parse("201406011200"), date);

    }

    @Test
    public void testGetPrevHalfHour2() throws Exception {
        //given
        StreamController controller = new StreamController();
        Date start = SDF.parse("201406011229");
        start.setTime(start.getTime() + 100);
        //when
        Date date = controller.getPrevHalfHour(start);

        //then
        Assert.assertEquals(SDF.parse("201406011200"), date);

    }

    @Test
    public void stream() throws Exception {
        StreamController controller = new StreamController(new StatToLog());
        ResourceCollection resources = controller.getMp3Links(SDF.parse("201406041005"), 90 * 60);
        resources.getCollection().get(0).setStartOffset(2);
        resources.getCollection().get(1).setEndOffset(8);
        for (Mp3File f : resources.getCollection()) {
            System.out.println(f.getName());
            System.out.println(f.getStartOffset());
            System.out.println(f.getEndOffset());
        }
        LocalBackend n = new LocalBackend("src/test/resources");
        n.stream(resources, 0, 40, new FileOutputStream(new File("target/stream.out")));
        Assert.assertEquals(36, n.getSize(resources));

    }


}