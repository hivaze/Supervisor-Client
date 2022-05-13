package me.litefine.client.handlers.files;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.stream.ChunkedFile;

import java.io.File;

public class ChunkedFileEncoder extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof File) super.write(ctx, new ChunkedFile((File) msg), promise);
        else super.write(ctx, msg, promise);
    }

}