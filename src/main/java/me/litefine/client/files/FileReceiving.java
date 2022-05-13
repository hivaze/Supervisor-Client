package me.litefine.client.files;

import me.litefine.client.messages.files.FileSendingMessage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class FileReceiving {

    private final String path;
    private final File file;
    final FileChannel fileChannel;
    private final long maxFileLength, startTimestamp;

    FileReceiving(FileSendingMessage message) throws IOException {
        this.path = message.getPath();
        this.file = File.createTempFile("CoreAPI-Receiving-", ".dwn");
        this.maxFileLength = message.getFileSize();
        this.fileChannel = new FileOutputStream(file).getChannel();
        this.file.deleteOnExit();
        this.startTimestamp = System.currentTimeMillis();
    }

    public String getPath() {
        return path;
    }

    public File getFile() {
        return file;
    }

    public long getMaxFileLength() {
        return maxFileLength;
    }

    public long getStartTimestamp() {
        return startTimestamp;
    }

}