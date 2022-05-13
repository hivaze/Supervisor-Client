package me.litefine.client.files;


import me.litefine.client.messages.files.FileSendingMessage;

import java.io.IOException;
import java.nio.ByteBuffer;

public class FileReceiver {

    private volatile FileReceiving resourceReceiving = null;

    public FileReceiving getReceiving() {
        return resourceReceiving;
    }

    public void receiveDataChunk(ByteBuffer byteBuffer) throws IOException {
        resourceReceiving.fileChannel.write(byteBuffer);
    }

    public long receivedBytes() throws IOException {
        return resourceReceiving.fileChannel.size();
    }

    public void stopReceivingMode() {
        try {
            resourceReceiving.fileChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            resourceReceiving = null;
        }
    }

    public boolean isReceivingMode() {
        return resourceReceiving != null;
    }

    public void createNewReceiving(FileSendingMessage message) throws Exception {
        resourceReceiving = new FileReceiving(message);
    }

}