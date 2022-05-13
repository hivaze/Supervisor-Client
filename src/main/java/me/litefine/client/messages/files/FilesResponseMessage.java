package me.litefine.client.messages.files;

import io.netty.buffer.ByteBuf;
import me.litefine.client.messages.AbstractMessage;
import me.litefine.client.utils.NettyUtils;

public class FilesResponseMessage extends AbstractMessage {

    private String path;
    private int filesCount;

    public FilesResponseMessage() {}

    @Override
    public void encodeIn(ByteBuf byteBuf) {
        NettyUtils.writeString(byteBuf, path);
        byteBuf.writeInt(filesCount);
    }

    @Override
    public void decodeFrom(ByteBuf byteBuf) {
        path = NettyUtils.readNextString(byteBuf);
        filesCount = byteBuf.readInt();
    }

    public String getPath() {
        return path;
    }

    public int getFilesCount() {
        return filesCount;
    }

}