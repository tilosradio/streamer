package hu.tilos.radio.backend.standalone.handlers;

import hu.tilos.radio.backend.ResourceCollection;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class M3uHandler extends BaseStreamHandler implements Handler {

    private static final Logger LOG = LoggerFactory.getLogger(M3uHandler.class);

    private static Pattern RANGE_PATTERN = Pattern.compile("bytes=(\\d+)-(\\d+)?");

    private String serverUrl = "http://tilos.hu";

    public M3uHandler(File root) {
        super(root);
    }

    public void handle(ChannelHandlerContext ctx, FullHttpRequest req) throws FileNotFoundException {
        ResourceCollection collection = preprocess(ctx, req);
        if (collection == null) {
            return;
        }
        int size = getSize(collection);


        StringBuilder b = new StringBuilder();
        b.append("#EXTM3U\n");
        b.append(("#EXTINF:" + size + ", Tilos Rádió - " + FILE_NAME_FORMAT.format(collection.getDescriptor().start) + "\n"));
        //workaround for the WP7Application: use the unsplitted version
        if (req.headers().get("User-Agent").contains("WP7App")) {
            generateSplittedResources(req.getUri(), b);
        } else {
            b.append(serverUrl + req.getUri().toString().replaceAll("\\.m3u", ".mp3"));
        }
        HttpResponse resp = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(b.toString().getBytes()));
        String filename = "tilos-" + FILE_NAME_FORMAT.format(collection.getDescriptor().start) + "-" + collection.getDescriptor().duration;
        resp.headers().add("Content-Type", "audio/x-mpegurl; charset=utf-8");
        resp.headers().add("Content-Disposition", "attachment; filename=\"" + filename + ".m3u\"");
        resp.headers().add("Content-Length", b.toString().getBytes().length);
        ctx.writeAndFlush(resp);
        ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT).addListener(ChannelFutureListener.CLOSE);

    }

    protected void generateSplittedResources(String requestURI, StringBuilder builder) {
        try {
            SimpleDateFormat fileNameFormat = new SimpleDateFormat("yyyyMMdd'-'HHmm");
            SimpleDateFormat dirNameFormat = new SimpleDateFormat("yyyy'/'MM'/'dd");
            //http://tilos.hu/mp3/tilos-20140916-100940-125058.m3u
            SimpleDateFormat parsing = new SimpleDateFormat("yyyyMMddHHmmss");

            long current = getPrevHalfHour(parsing.parse(requestURI.substring(11, 19) + requestURI.substring(20, 26))).getTime();
            long to = parsing.parse(requestURI.substring(11, 19) + requestURI.substring(27, 33)).getTime();
            while (current < to) {
                //http://archive.tilos.hu/online/2014/09/01/tilosradio-20140901-1700.mp3
                builder.append(String.format("http://archive.tilos.hu/online/%s/tilosradio-%s.mp3\n", dirNameFormat.format(current), fileNameFormat.format(current)).getBytes());
                current += 1000 * 60 * 30;
            }
        } catch (Exception ex) {
            LOG.error("Can't generate m3u", ex);
        }
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


    @Override
    public boolean isApplicable(FullHttpRequest req) {
        return req.getUri().endsWith("m3u");
    }

}
