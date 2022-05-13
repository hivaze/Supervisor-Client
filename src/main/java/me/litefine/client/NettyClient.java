package me.litefine.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.internal.SocketUtils;
import me.litefine.client.files.FileReceiver;
import me.litefine.client.files.FileReceiving;
import me.litefine.client.handlers.LogicHandler;
import me.litefine.client.handlers.files.ChunkedFileDecoder;
import me.litefine.client.handlers.files.ChunkedFileEncoder;
import me.litefine.client.handlers.messaging.FramedMessageDecoder;
import me.litefine.client.handlers.messaging.MessageEncoder;
import me.litefine.client.messages.AbstractMessage;
import me.litefine.client.messages.files.FileSendingMessage;
import me.litefine.client.messages.system.connection.DisconnectMessage;
import me.litefine.client.utils.NettyUtils;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.function.Predicate;

public final class NettyClient {

    private static Channel channel;
    private static final EventLoopGroup eventLoops = NettyUtils.getEventLoopGroup(1);
    private static final FileReceiver fileReceiver = new FileReceiver();

    private static final String serversInfoPattern = System.getProperty("coreapi.serversInfoPattern", "(.*)");

    public static void main(String[] args) {
        ChannelFutureListener establishListener = channelFuture -> {
            if (channelFuture.isSuccess()) {
                channel = channelFuture.channel();
                System.out.println("Server connected to Supervisor");
            } else System.out.println("Can't connect to Supervisor");
        };
        ChannelInitializer<Channel> initializer = new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel channel) {
                //Experimental: channel.config().setRecvByteBufAllocator(new AdaptiveRecvByteBufAllocator(64, 2*1024, 64*1024));
                channel.pipeline().addLast(new ReadTimeoutHandler(3000));
                channel.pipeline().addLast(new ChunkedWriteHandler());
                channel.pipeline().addLast(new ChunkedFileDecoder());
                channel.pipeline().addLast(new FramedMessageDecoder());
                channel.pipeline().addLast(new MessageEncoder());
                channel.pipeline().addLast(new ChunkedFileEncoder());
                channel.pipeline().addLast(new LogicHandler());
            }
        };
        try {
            System.out.println("Connecting to Supervisor...");
            new Bootstrap().group(eventLoops)
                    .channel(NettyUtils.getClientSocketClass())
                    .option(ChannelOption.TCP_NODELAY, true) //Experimental
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                    .handler(initializer)
                    .connect(SocketUtils.socketAddress("localhost", 7000))
                    .addListener(establishListener).syncUninterruptibly();
        } catch (Exception ignored) {}
    }

    public static <T extends AbstractMessage> Optional<T> waitForMessage(Class<T> clazz, Predicate<T> condition) throws IOException {
        return waitForMessage(clazz, condition, 500);
    }

    public static <T extends AbstractMessage> Optional<T> waitForMessage(Class<T> clazz, Predicate<T> condition, long timeoutMillis) throws IOException {
        if (!isConnected()) throw new IOException("Connection not established!");
        return LogicHandler.waitForObject(clazz, condition, timeoutMillis);
    }

    public static Optional<FileReceiving> waitForResourceReceiving(Predicate<FileReceiving> condition) throws IOException {
        return waitForResourceReceiving(condition, 5000);
    }

    public static Optional<FileReceiving> waitForResourceReceiving(Predicate<FileReceiving> condition, long timeoutMillis) throws IOException {
        if (!isConnected()) throw new IOException("Connection not established!");
        return LogicHandler.waitForObject(FileReceiving.class, condition, timeoutMillis);
    }

    public static void sendMessage(AbstractMessage message) throws IOException {
        if (!isConnected()) throw new IOException("Connection not established!");
        channel.writeAndFlush(message);
    }

    public static void sendFile(File file, String remotePath) throws IOException {
        if (!isConnected()) throw new IOException("Connection not established!");
        channel.writeAndFlush(new FileSendingMessage(remotePath, file.length()));
        channel.writeAndFlush(file);
    }

    public static boolean isConnected() {
        return channel != null && channel.isActive();
    }

    public static void shutdown() {
        if (isConnected()) channel.writeAndFlush(new DisconnectMessage("CLIENT SHUTDOWN"));
        eventLoops.shutdownGracefully().syncUninterruptibly();
    }

    public static FileReceiver getFileReceiver() {
        return fileReceiver;
    }

    public static String getServersInfoPattern() {
        return serversInfoPattern;
    }

}