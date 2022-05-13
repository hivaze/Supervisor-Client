package me.litefine.client.messages.system.connection;

import io.netty.buffer.ByteBuf;
import me.litefine.client.messages.AbstractMessage;
import me.litefine.client.utils.NettyUtils;

public class DisconnectMessage extends AbstractMessage {

    private String reason;

    public DisconnectMessage() {}

    public DisconnectMessage(String reason) {
        this.reason = reason;
    }

    @Override
    public void encodeIn(ByteBuf byteBuf) {
        NettyUtils.writeString(byteBuf, reason);
    }

    @Override
    public void decodeFrom(ByteBuf byteBuf) {
        reason = NettyUtils.readNextString(byteBuf);
    }

    public String getReason() {
        return reason;
    }

}