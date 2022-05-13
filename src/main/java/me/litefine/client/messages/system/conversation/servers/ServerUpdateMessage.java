package me.litefine.client.messages.system.conversation.servers;

import io.netty.buffer.ByteBuf;
import me.litefine.client.messages.AbstractMessage;
import me.litefine.client.utils.NettyUtils;

public class ServerUpdateMessage extends AbstractMessage {

    private String formattedInfoData;

    public ServerUpdateMessage() {}

    public ServerUpdateMessage(int online, int maxPlayers, String motd, double tps) {
        formattedInfoData = "`" + online + "`" + maxPlayers + "`" + motd + "`" + tps + "`";
    }

    @Override
    public void encodeIn(ByteBuf byteBuf) throws Exception {
        NettyUtils.writeString(byteBuf, formattedInfoData);
    }

    @Override
    public void decodeFrom(ByteBuf byteBuf) throws Exception {
        formattedInfoData = NettyUtils.readNextString(byteBuf);
    }

    public String getFormattedInfoData() {
        return formattedInfoData;
    }

}