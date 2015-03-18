package hu.tilos.radio.backend.standalone.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

public interface Handler {

    boolean isApplicable(FullHttpRequest req);

    public void handle(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception;
}
