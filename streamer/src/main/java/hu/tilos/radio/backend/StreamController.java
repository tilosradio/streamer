package hu.tilos.radio.backend;

import hu.tilos.radio.backend.streamer.Backend;
import hu.tilos.radio.backend.streamer.StatPersistence;
import hu.tilos.radio.backend.streamer.StatToLog;
import hu.tilos.radio.backend.streamer.util.Mp3Joiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet(urlPatterns = "/")

public class StreamController extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger(StreamController.class);

    private static SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMddHHmmss");

    private static SimpleDateFormat FILE_NAME_FORMAT = new SimpleDateFormat("yyyyMMdd-HHmm");

    private static Pattern RANGE_PATTERN = Pattern.compile("bytes=(\\d+)-(\\d+)?");

    Mp3Joiner joiner = new Mp3Joiner();

    @Inject
    Backend backend;

    @Inject
    StatPersistence stat;

    @Inject
    @Configuration(name = "server.url")
    private String serverUrl;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp, resp.getOutputStream());
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp, OutputStream output) throws ServletException, IOException {
        if (stat == null) {
            stat = new StatToLog();
        }
        MDC.put("requestId", "" + Math.round(Math.random() * 10000));
        Segment segment = null;
        try {
            segment = parse(req.getRequestURI());
        } catch (ParseException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            output.write(("Error on parsing url pattern" + e.getMessage()).getBytes());
            LOG.error("Error on parsing url pattern " + req.getRequestURI(), e);
            return;
        }
        if (segment.duration > 360 * 60 /* 6 hours */) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            output.write(("Too long duration").getBytes());
            return;
        }
        LOG.info("Starting to download of episode from " + SDF.format(segment.start));
        String statToken = stat.startDownload(segment.start);
        try {
            ResourceCollection collection = getMp3Links(segment.start, segment.duration);
            detectJoins(collection);
            adjustFirstFrame(collection);
            int size = backend.getSize(collection);


            if (req.getRequestURI().endsWith("m3u")) {
                generateM3u(req, resp, segment, size);
                return;
            }

            if (req.getHeader("Range") != null) {
                //partial request
                String range = req.getHeader("Range");
                Matcher m = RANGE_PATTERN.matcher(range);
                if (m.matches()) {
                    int start = Integer.valueOf(m.group(1));
                    LOG.info("Seeking to " + start);
                    stat.seek(statToken, start);
                    int to = size;
                    if (m.group(2) != null) {
                        to = Integer.parseInt(m.group(2)) + 1;
                    }


                    resp.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
                    resp.setHeader("Accept-Ranges", "bytes");
                    resp.setHeader("Content-Length", "" + (to - start)); // The size of the range
                    resp.setHeader("Content-Range", "bytes=" + start + "-" + (to - 1) + "/" + size); // The size of the range
                    try {
                        //monitor.increment();
                        int writtenBytes = backend.stream(collection, start, size, output);
                        LOG.info("Streaming has been stopped. Written bytes: " + writtenBytes);
                        stat.endDownload(statToken, writtenBytes);
                    } finally {
                        //monitor.decrement();
                    }
                } else {
                    throw new RuntimeException("Unknown range request");
                }

            } else {
                resp.setHeader("Content-Length", "" + size);
                resp.setHeader("Content-Type", "audio/mpeg");
                String filename = "tilos-" + FILE_NAME_FORMAT.format(segment.start) + "-" + segment.duration;
                resp.setHeader("Content-Disposition", "inline; filename=\"" + filename + ".mp3\"");

                resp.setHeader("Accept-Ranges", "bytes");
                try {
                    //monitor.increment();
                    int writtenBytes = backend.stream(collection, 0, size, output);
                    LOG.info("Streaming has been stopped. Written bytes: " + writtenBytes);
                    stat.endDownload(statToken, writtenBytes);
                } finally {
                    //monitor.decrement();
                }
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            if (e.getMessage() != null) {
                resp.setHeader("Content-Length", "" + e.getMessage().length());
                output.write(e.getMessage().getBytes());
            }

            LOG.error("Error on streaming data", e);

        }
    }

    /**
     * Find the next real frame of the adjusted position.
     *
     * @param collection
     */
    private void adjustFirstFrame(ResourceCollection collection) {
        List<Mp3File> mp3Files = collection.getCollection();
        if (mp3Files.size() > 0) {
            int position = joiner.findNextFrame(backend.getLocalFile(mp3Files.get(0)), mp3Files.get(0).getStartOffset());
            mp3Files.get(0).setStartOffset(position);
        }
    }

    private void detectJoins(ResourceCollection collection) {
        List<Mp3File> mp3Files = collection.getCollection();
        for (int i = 0; i < mp3Files.size() - 1; i++) {
            Mp3Joiner.OffsetDouble joinPositions = joiner.findJoinPositions(backend.getLocalFile(mp3Files.get(i)), backend.getLocalFile(mp3Files.get(i + 1)));
            if (joinPositions != null) {
                mp3Files.get(i).setEndOffset(joinPositions.firstEndOffset);
                mp3Files.get(i + 1).setStartOffset(joinPositions.secondStartOffset);
            }
        }
    }

    private void generateM3u(HttpServletRequest req, HttpServletResponse resp, Segment segment, int size) throws IOException {
        String filename = "tilos-" + FILE_NAME_FORMAT.format(segment.start) + "-" + segment.duration;
        resp.setHeader("Content-Type", "audio/x-mpegurl; charset=utf-8");
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + filename + ".m3u\"");
        resp.getOutputStream().write("#EXTM3U\n".getBytes());
        resp.getOutputStream().write(("#EXTINF:" + size + ", Tilos Rádió - " + FILE_NAME_FORMAT.format(segment.start) + "\n").getBytes());
        //workaround for the WP7Application: use the unsplitted version
        if (req.getHeader("User-Agent").contains("WP7App")) {
            generateSplittedResources(req.getRequestURI(), resp.getOutputStream());
        } else {
            resp.getOutputStream().write((serverUrl + req.getRequestURI().toString().replaceAll("\\.m3u", ".mp3")).getBytes());
        }

    }

    protected void generateSplittedResources(String requestURI, OutputStream outputStream) {
        try {
            SimpleDateFormat fileNameFormat = new SimpleDateFormat("yyyyMMdd'-'HHmm");
            SimpleDateFormat dirNameFormat = new SimpleDateFormat("yyyy'/'MM'/'dd");
            //http://tilos.hu/mp3/tilos-20140916-100940-125058.m3u
            SimpleDateFormat parsing = new SimpleDateFormat("yyyyMMddHHmmss");

            long current = getPrevHalfHour(parsing.parse(requestURI.substring(11, 19) + requestURI.substring(20, 26))).getTime();
            long to = parsing.parse(requestURI.substring(11, 19) + requestURI.substring(27, 33)).getTime();
            while (current < to) {
                //http://archive.tilos.hu/online/2014/09/01/tilosradio-20140901-1700.mp3
                outputStream.write(String.format("http://archive.tilos.hu/online/%s/tilosradio-%s.mp3\n", dirNameFormat.format(current), fileNameFormat.format(current)).getBytes());
                current += 1000 * 60 * 30;
            }
        } catch (Exception ex) {
            LOG.error("Can't generate m3u", ex);
        }
    }

    protected Segment parse(String requestURI) throws ParseException {
        Segment s = new Segment();

        Matcher m = Pattern.compile("^/mp3/tilos-(\\d+)-(\\d+)-(\\d+).*$").matcher(requestURI);
        if (m.matches()) {

            s.start = SDF.parse(m.group(1) + m.group(2));
            Date end = SDF.parse(m.group(1) + m.group(3));
            s.duration = Math.round((end.getTime() - s.start.getTime()) / 1000);
            if (s.duration < 0) {
                s.duration += 24 * 60 * 60;
            }
            return s;

        } else {
            m = Pattern.compile("^/mp3/(\\d+)/(\\d+)/(\\d+).*$").matcher(requestURI);
            if (m.matches()) {
                s.start = SDF.parse(m.group(1) + m.group(2));
                Date end = SDF.parse(m.group(1) + m.group(3));
                s.duration = Math.round((end.getTime() - s.start.getTime()) / 1000);
                return s;
            } else {
                m = Pattern.compile("^/mp3/(\\d+)-(\\d+).*$").matcher(requestURI);
                if (m.matches()) {
                    s.start = new Date();
                    s.start.setTime(Long.valueOf(m.group(1)) * 1000);
                    s.duration = Integer.valueOf(m.group(2));
                    return s;
                }
            }
        }

        return null;
    }

    public Date getPrevHalfHour(Date date) {
        Date result = new Date();
        result.setTime(date.getTime() / 1000 * 1000);
        result.setSeconds(0);
        if (result.getMinutes() >= 30) {
            result.setMinutes(30);
        } else {
            result.setMinutes(0);
        }
        return result;
    }

    /**
     * Duration is in seconds.
     *
     * @param start
     * @param duration
     * @return
     */
    public ResourceCollection getMp3Links(Date start, int duration) {
        ResourceCollection collection = new ResourceCollection();
        Date from = getPrevHalfHour(start);

        Date end = new Date();
        end.setTime(start.getTime() + 1000 * duration);

        Date i = new Date();
        Date lastStart = new Date();
        i.setTime(from.getTime());
        while (i.compareTo(end) < 0) {

            SimpleDateFormat d = new SimpleDateFormat("'/'yyyy'/'MM'/'dd'/tilosradio-'yyyMMdd'-'HHmm'.mp3'");
            collection.add(new Mp3File(d.format(i)));
            lastStart.setTime(i.getTime());
            i.setTime(i.getTime() + 60 * 30 * 1000);
        }

        int startOffset = (int) ((start.getTime() - from.getTime()) / 1000);
        collection.getCollection().get(0).setStartOffset((int) Math.round(startOffset * 38.28125 * 836));
        int endOffset = (int) ((end.getTime() - lastStart.getTime())) / 1000;
        collection.getCollection().get(collection.getCollection().size() - 1).setEndOffset((int) Math.round(endOffset * 38.28125 * 836));
        return collection;
    }

    public void setServerUrl(String url) {
        this.serverUrl = url;
    }

    public Backend getBackend() {
        return backend;
    }

    public void setBackend(Backend backend) {
        this.backend = backend;
    }


}
