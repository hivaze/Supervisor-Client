package me.litefine.client.messages.system.conversation;

import io.netty.buffer.ByteBuf;
import me.litefine.client.messages.AbstractMessage;
import me.litefine.client.utils.NettyUtils;

public class CustomPayloadMessage extends AbstractMessage {

    private String header;
    private Object payload;

    public CustomPayloadMessage() {}

    public CustomPayloadMessage(String header, Object payload) {
        this.header = header;
        this.payload = payload;
    }

    @Override
    public void encodeIn(ByteBuf byteBuf) throws Exception {
        NettyUtils.writeString(byteBuf, header);
        NettyUtils.writeObject(byteBuf, payload);
    }

    @Override
    public void decodeFrom(ByteBuf byteBuf) throws Exception {
        header = NettyUtils.readNextString(byteBuf);
        payload = NettyUtils.readNextObject(byteBuf);
    }

    public String getHeader() {
        return header;
    }

    public Object getPayload() {
        return payload;
    }

}