package hu.tilos.radio.backend.netty.handlers;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.handler.codec.http.*;

import java.io.File;
import java.io.RandomAccessFile;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class TestStreamHandler implements Handler {


    @Override
    public boolean isApplicable(FullHttpRequest req) {
        return req.headers().get("Test") != null;
    }

    @Override
    public void handle(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
        HttpResponse resp = new DefaultHttpResponse(HTTP_1_1, HttpResponseStatus.OK);

        int a = 80000;
        int b = 80000;
        resp.headers().add("Content-Length", "" + (a + b));

        ctx.writeAndFlush(resp);
        ctx.writeAndFlush(new DefaultFileRegion(new RandomAccessFile(new File("/tmp/2014/12/17/tilosradio-20141217-1000.mp3"), "r").getChannel(),
                        0,
                        a),
                ctx.newProgressivePromise()
        );
        ctx.write(new DefaultFileRegion(new RandomAccessFile(new File("/tmp/2014/12/17/tilosradio-20141217-1030.mp3"), "r").getChannel(),
                        0,
                        b),
                ctx.newProgressivePromise()
        );

        ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT).addListener(ChannelFutureListener.CLOSE);
    }
}

