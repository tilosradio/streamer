package hu.tilos.radio.backend.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

import java.io.File;

public class NettyInitializer extends ChannelInitializer<SocketChannel> {

    File root;

    public NettyInitializer(File root) {
        this.root = root;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(99965536));
        //pipeline.addLast(new ChunkedWriteHandler());
        pipeline.addLast(new NettyFileHandler(root));
    }
}