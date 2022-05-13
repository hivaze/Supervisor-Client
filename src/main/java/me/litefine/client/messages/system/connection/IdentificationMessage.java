package me.litefine.client.messages.system.connection;

import io.netty.buffer.ByteBuf;
import me.litefine.client.NettyClient;
import me.litefine.client.messages.AbstractMessage;
import me.litefine.client.utils.NettyUtils;

public class IdentificationMessage extends AbstractMessage {

    private String identificator, serversInfoPattern;
    private int remotePort;

    public IdentificationMessage() {}

    public IdentificationMessage(String identificator, int remotePort) {
        this.identificator = identificator;
        this.remotePort = remotePort;
        this.serversInfoPattern = NettyClient.getServersInfoPattern();
    }

    @Override
    public void encodeIn(ByteBuf byteBuf) {
        NettyUtils.writeString(byteBuf, identificator);
        NettyUtils.writeString(byteBuf, "MINECRAFT_SERVER");
        byteBuf.writeInt(remotePort);
        NettyUtils.writeString(byteBuf, serversInfoPattern);
    }

    @Override
    public void decodeFrom(ByteBuf byteBuf) {
        identificator = NettyUtils.readNextString(byteBuf);
        NettyUtils.readNextString(byteBuf);
        remotePort = byteBuf.readInt();
        serversInfoPattern = NettyUtils.readNextString(byteBuf);
    }

    public int getRemotePort() {
        return remotePort;
    }

    public String getIdentificator() {
        return identificator;
    }

    public String getServersInfoPattern() {
        return serversInfoPattern;
    }

}