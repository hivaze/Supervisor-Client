package me.litefine.client.messages.files;

import io.netty.buffer.ByteBuf;
import me.litefine.client.messages.AbstractMessage;
import me.litefine.client.utils.NettyUtils;

public class FileSendingMessage extends AbstractMessage {

    private String path;
    private long fileSize;

    public FileSendingMessage() {}

    public FileSendingMessage(String path, long fileSize) {
        this.path = path;
        this.fileSize = fileSize;
    }

    @Override
    public void encodeIn(ByteBuf byteBuf) {
        NettyUtils.writeString(byteBuf, path);
        byteBuf.writeLong(fileSize);
    }

    @Override
    public void decodeFrom(ByteBuf byteBuf) {
        path = NettyUtils.readNextString(byteBuf);
        fileSize = byteBuf.readLong();
    }

    public String getPath() {
        return path;
    }

    public long getFileSize() {
        return fileSize;
    }

}