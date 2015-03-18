package hu.tilos.radio.backend.standalone.handlers;

import hu.tilos.radio.backend.Mp3File;
import hu.tilos.radio.backend.ResourceCollection;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class SimpleStreamHandler extends BaseStreamHandler implements Handler {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleStreamHandler.class);

    public SimpleStreamHandler(File root) {
        super(root);
    }

    public void handle(ChannelHandlerContext ctx, FullHttpRequest req) throws FileNotFoundException {
        MDC.put("requestId", "" + Math.round(Math.random() * 10000));

        ResourceCollection collection = preprocess(ctx, req);
        if (collection == null) {
            return;
        }

        HttpResponse resp = new DefaultHttpResponse(HTTP_1_1, HttpResponseStatus.OK);

        int size = getSize(collection);

        resp.headers().add("Content-Length", "" + size);
        resp.headers().add("Content-Type", "audio/mpeg");
        String filename = "tilos-" + FILE_NAME_FORMAT.format(collection.getDescriptor().start) + "-" + collection.getDescriptor().duration;
        //if (req.getParameter("download") != null) {
        //    resp.headers().add("Content-Disposition", "attachment; filename=\"" + filename + ".mp3\"");
        //} else {
        resp.headers().add("Content-Disposition", "inline; filename=\"" + filename + ".mp3\"");
        //}

        resp.headers().add("Accept-Ranges", "bytes");
        try {
            //monitor.increment();
            ctx.write(resp);
            streamContent(ctx, collection);
            ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT).addListener(ChannelFutureListener.CLOSE);
        } finally {
            //monitor.decrement();
        }


    }

    private void streamContent(ChannelHandlerContext ctx, ResourceCollection collection) throws FileNotFoundException {
        for (Mp3File file : collection.getCollection()) {
            ctx.write(new DefaultFileRegion(new RandomAccessFile(new File(root, file.getName()), "r").getChannel(),
                            file.getStartOffset(),
                            file.getEndOffset() - file.getStartOffset()),
                    ctx.newProgressivePromise()
            );

        }
    }

    @Override
    public boolean isApplicable(FullHttpRequest req) {
        return true;
    }
}
