package me.litefine.client.messages.player.skin;

import io.netty.buffer.ByteBuf;
import me.litefine.client.messages.AbstractMessage;
import me.litefine.client.utils.NettyUtils;

public class PlayerSkinResponseMessage extends AbstractMessage {

    private String playerName, UUID;
    private boolean hasTextures;
    private String texturesValue, signature;

    public PlayerSkinResponseMessage() {}

    @Override
    public void encodeIn(ByteBuf byteBuf) throws Exception {
        NettyUtils.writeString(byteBuf, playerName);
        NettyUtils.writeString(byteBuf, UUID);
        byteBuf.writeBoolean(hasTextures);
        if (hasTextures) {
            NettyUtils.writeString(byteBuf, texturesValue);
            NettyUtils.writeString(byteBuf, signature);
        }
    }

    @Override
    public void decodeFrom(ByteBuf byteBuf) throws Exception {
        playerName = NettyUtils.readNextString(byteBuf);
        UUID = NettyUtils.readNextString(byteBuf);
        hasTextures = byteBuf.readBoolean();
        if (hasTextures) {
            texturesValue = NettyUtils.readNextString(byteBuf);
            signature = NettyUtils.readNextString(byteBuf);
        }
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getUUID() {
        return UUID;
    }

    public boolean isHasTextures() {
        return hasTextures;
    }

    public String getTexturesValue() {
        return texturesValue;
    }

    public String getSignature() {
        return signature;
    }

}