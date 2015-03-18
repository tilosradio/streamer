package hu.tilos.radio.backend.standalone.handlers;

import hu.tilos.radio.backend.Mp3File;
import hu.tilos.radio.backend.ResourceCollection;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.handler.codec.http.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class RangeStreamHandler extends BaseStreamHandler implements Handler {

    private static Pattern RANGE_PATTERN = Pattern.compile("bytes=(\\d+)-(\\d+)?");

    public RangeStreamHandler(File root) {
        super(root);
    }

    public void handle(ChannelHandlerContext ctx, FullHttpRequest req) throws FileNotFoundException {
        ResourceCollection collection = preprocess(ctx, req);
        if (collection == null) {
            return;
        }
        HttpResponse resp = new DefaultHttpResponse(HTTP_1_1, HttpResponseStatus.PARTIAL_CONTENT);
        int size = getSize(collection);

        //partial request
        String range = req.headers().get("Range");
        Matcher m = RANGE_PATTERN.matcher(range);
        if (m.matches()) {
            int start = Integer.valueOf(m.group(1));
            int to = size;
            if (m.group(2) != null) {
                to = Integer.parseInt(m.group(2)) + 1;
            }


            resp.headers().add("Accept-Ranges", "bytes");
            resp.headers().add("Content-Length", "" + (to - start)); // The size of the range
            resp.headers().add("Content-Range", "bytes=" + start + "-" + (to - 1) + "/" + size); // The size of the range

            try {
                //monitor.increment();
                ctx.writeAndFlush(resp);
                int offset = 0;
                for (Mp3File file : collection.getCollection()) {
                    int segmentSize = file.getEndOffset() - file.getStartOffset();
                    if (offset + segmentSize > start && offset < to) {
                        int b = file.getStartOffset() + Math.max(0, start - offset);
                        int e = Math.max(file.getEndOffset(), offset + segmentSize - start + file.getStartOffset());
                        ctx.writeAndFlush(new DefaultFileRegion(new RandomAccessFile(new File(root, file.getName()), "r").getChannel(),
                                        b,
                                        e - b),
                                ctx.newProgressivePromise());
                        offset += e - b;
                    }
                    offset += file.getEndOffset() - file.getStartOffset();
                }
                ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT).addListener(ChannelFutureListener.CLOSE);


            } finally {
                //monitor.decrement();
            }
        } else {
            throw new RuntimeException("Unknown range request");
        }

    }

    @Override
    public boolean isApplicable(FullHttpRequest req) {
        return req.headers().get("Range") != null;
    }

}
