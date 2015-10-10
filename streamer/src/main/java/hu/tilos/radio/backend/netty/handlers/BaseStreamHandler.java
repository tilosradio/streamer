package hu.tilos.radio.backend.netty.handlers;

import hu.tilos.radio.backend.Mp3File;
import hu.tilos.radio.backend.ResourceCollection;
import hu.tilos.radio.backend.Segment;
import hu.tilos.radio.backend.streamer.util.FileLinkCalculator;
import hu.tilos.radio.backend.streamer.util.Mp3Joiner;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;
import org.slf4j.MDC;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class BaseStreamHandler {

    static SimpleDateFormat FILE_NAME_FORMAT = new SimpleDateFormat("yyyyMMdd-HHmm");

    private static SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMddHHmmss");

    protected FileLinkCalculator fileLinkCalculator = new FileLinkCalculator();

    protected Mp3Joiner joiner = new Mp3Joiner();

    protected File root;

    public BaseStreamHandler(File root) {
        this.root = root;
    }

    public void error(ChannelHandlerContext ctx, int code, String message) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.valueOf(code),
                Unpooled.copiedBuffer(message + "\r\n", CharsetUtil.UTF_8));
        response.headers().set("Content-type", "text/plain; charset=UTF-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    public void error(ChannelHandlerContext ctx, int code, String message, Throwable t) {
        error(ctx, code, message);
        t.printStackTrace();
    }

    public int getSize(ResourceCollection collection) {
        int size = 0;
        for (Mp3File file : collection.getCollection()) {
            size += file.size();
        }
        return size;
    }

    public int size(Mp3File file) {
        int size = (int) new File(root, file.getName()).length();
        if (file.getEndOffset() < size) {
            size = file.getEndOffset();
        }
        return size;
    }

    public ResourceCollection preprocess(ChannelHandlerContext ctx, FullHttpRequest req) {
        MDC.put("requestId", "" + Math.round(Math.random() * 10000));
        Segment segment = null;
        try {
            segment = parse(req.getUri());
        } catch (ParseException e) {
            error(ctx, 500, "Error on parsing url pattern " + req.getUri(), e);
            return null;
        }
        if (segment.duration > 360 * 60 /* 6 hours */) {
            error(ctx, 500, "Too long duration");
            return null;
        }


        ResourceCollection collection = fileLinkCalculator.getMp3Links(segment.start, segment.duration);
        for (Mp3File file : collection.getCollection()) {
            File realFile = new File(root, file.getName());
            if (!realFile.exists() || realFile.length() < 57700000) {
                error(ctx, 404, "One or more archive segment is missing or corrupt" + file.getName());
            }
        }
        adjustSizes(collection);
        collection.setDescriptor(segment);
        joiner.detectJoins(root, collection);
        joiner.adjustFirstFrame(root, collection);
        return collection;
    }

    private void adjustSizes(ResourceCollection collection) {
        for (Mp3File file : collection.getCollection()) {
            file.setEndOffset(size(file));
        }
    }

    protected Segment parse(String requestURI) throws ParseException {
        Segment s = new Segment();
        requestURI = requestURI.replaceAll("download\\?=\\w+", "");
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
}
