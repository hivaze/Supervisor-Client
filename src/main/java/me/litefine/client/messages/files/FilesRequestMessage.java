package me.litefine.client.messages.files;

import io.netty.buffer.ByteBuf;
import me.litefine.client.messages.AbstractMessage;
import me.litefine.client.utils.NettyUtils;

public class FilesRequestMessage extends AbstractMessage {

    private String path;

    public FilesRequestMessage() {}

    public FilesRequestMessage(String path) {
        this.path = path;
    }

    @Override
    public void encodeIn(ByteBuf byteBuf) {
        NettyUtils.writeString(byteBuf, path);
    }

    @Override
    public void decodeFrom(ByteBuf byteBuf) {
        path = NettyUtils.readNextString(byteBuf);
    }

    public String getPath() {
        return path;
    }

}