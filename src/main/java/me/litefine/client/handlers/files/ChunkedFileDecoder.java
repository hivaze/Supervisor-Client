package me.litefine.client.handlers.files;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import me.litefine.client.NettyClient;
import me.litefine.client.files.FileReceiving;

public class ChunkedFileDecoder extends SimpleChannelInboundHandler<ByteBuf> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception {
        ReferenceCountUtil.retain(byteBuf);
        if (NettyClient.getFileReceiver().isReceivingMode()) {
            System.out.println("[ChunkedFileDecoder] Receiving file region " + byteBuf);
            FileReceiving resourceReceiving = NettyClient.getFileReceiver().getReceiving();
            long offset = resourceReceiving.getMaxFileLength() - (NettyClient.getFileReceiver().receivedBytes() + byteBuf.readableBytes());
            if (offset <= 0) {
                int errorIndex = (int) (offset + byteBuf.readableBytes());
                ByteBuf fixed = byteBuf.slice(0, errorIndex), extra = byteBuf.skipBytes(errorIndex).slice();
                NettyClient.getFileReceiver().receiveDataChunk(fixed.nioBuffer());
                NettyClient.getFileReceiver().stopReceivingMode();
                ctx.fireChannelRead(resourceReceiving);
                if (extra.readableBytes() > 0) ctx.fireChannelRead(extra.retain());
            } else NettyClient.getFileReceiver().receiveDataChunk(byteBuf.nioBuffer());
            ReferenceCountUtil.safeRelease(byteBuf);
        } else ctx.fireChannelRead(byteBuf);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("[ChunkedFileDecoder] Can't read ByteBuf from " + ctx.channel().remoteAddress());
        cause.printStackTrace();
    }

}