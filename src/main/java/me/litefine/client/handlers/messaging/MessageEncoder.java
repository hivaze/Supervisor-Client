package me.litefine.client.handlers.messaging;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import me.litefine.client.messages.AbstractMessage;
import me.litefine.client.utils.NettyUtils;

public class MessageEncoder extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof AbstractMessage) {
            ByteBuf byteBuf = ctx.alloc().buffer();
            AbstractMessage message = (AbstractMessage) msg;
            String className = message.getClass().getName();
            NettyUtils.writeString(byteBuf, className.substring(className.indexOf("messages")));
            message.encodeIn(byteBuf);
            ByteBuf wrapper = byteBuf.alloc().buffer(2 + byteBuf.readableBytes()).writeShort(byteBuf.readableBytes()).writeBytes(byteBuf);
            //System.out.println("[MessageEncoder] Sening buffer " + wrapper);
            super.write(ctx, wrapper, promise);
        } else super.write(ctx, msg, promise);
    }

}