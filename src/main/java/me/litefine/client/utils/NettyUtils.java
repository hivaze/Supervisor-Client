package me.litefine.client.utils;

import io.netty.buffer.ByteBuf;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;

import java.io.*;

public class NettyUtils {

    public static EventLoopGroup getEventLoopGroup(int threadsCound) {
        if (Epoll.isAvailable()) return new EpollEventLoopGroup(threadsCound);
        else return new NioEventLoopGroup(threadsCound);
    }

    public static Class<? extends SocketChannel> getClientSocketClass() {
        if (Epoll.isAvailable()) return EpollSocketChannel.class;
        else return NioSocketChannel.class;
    }

    public static String readNextString(ByteBuf byteBuf) {
        byte[] array = new byte[byteBuf.readInt()];
        byteBuf.readBytes(array);
        return new String(array, CharsetUtil.UTF_8);
    }

    public static void writeString(ByteBuf byteBuf, String string) {
        byte[] bytes = string.getBytes(CharsetUtil.UTF_8);
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
    }

    public static void writeObject(ByteBuf byteBuf, Object object) throws IOException {
        byte[] bytes = objectToByteArray(object);
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
    }

    public static Object readNextObject(ByteBuf byteBuf) throws IOException, ClassNotFoundException {
        byte[] bytes = new byte[byteBuf.readInt()];
        byteBuf.readBytes(bytes);
        return byteArrayToObject(bytes);
    }

    public static byte[] objectToByteArray(Object object) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            try (ObjectOutputStream oos = new ObjectOutputStream(bos)) {
                oos.writeObject(object);
            }
            return bos.toByteArray();
        }
    }

    public static Object byteArrayToObject(byte[] bytes) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes)) {
            try (ObjectInputStream ois = new ObjectInputStream(bis)) {
                return ois.readObject();
            }
        }
    }

}