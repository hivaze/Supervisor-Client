package me.litefine.client.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.internal.ConcurrentSet;
import me.litefine.client.NettyClient;
import me.litefine.client.files.FileReceiving;
import me.litefine.client.messages.AbstractMessage;
import me.litefine.client.messages.files.FileSendingMessage;
import me.litefine.client.messages.player.bungeecord.PlayerInfoRequestMessage;
import me.litefine.client.messages.player.bungeecord.PlayerInfoResponseMessage;
import me.litefine.client.messages.system.connection.DisconnectMessage;
import me.litefine.client.messages.system.connection.IdentificationMessage;
import me.litefine.client.messages.system.conversation.CustomPayloadMessage;
import me.litefine.client.messages.system.conversation.servers.ServerUpdateMessage;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public class LogicHandler extends SimpleChannelInboundHandler<Object> {

    private static String disconnectReason = "REASON NOT DEFINED";
    private static final Set<InboundObjectWaiter> objectWaiters = new ConcurrentSet<>();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(new IdentificationMessage("DDOSER", 234));
        ctx.writeAndFlush(new ServerUpdateMessage(12, 1023, "TEST_MOTD", 14.5));
        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            long time = System.currentTimeMillis();
            ctx.writeAndFlush(new PlayerInfoRequestMessage("LITEFINE"));
            waitForObject(PlayerInfoResponseMessage.class, null, 500L).ifPresent(response -> System.out.println(response.getResponse()));
            System.out.println("time spent " + (System.currentTimeMillis() - time));
        });
        //NettyClient.sendFile(Paths.get("/Users/sergey/Documents/workspace.zip").toFile(), "workspace.zip");
        //for (int i = 0; i < 20; i++) ctx.writeAndFlush(new CustomPayloadMessage("PAYLOAD", "WbZqmBQghHVhtAdvA0A1WBUPVN849fVRQdTSTpZQPK36Wo92Rr8GN0VCG08kSX8pKfJbjlkAkDkfyjMxogWve52QLjFF5NtDWsBp"));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Connection with Supervisor closed: " + disconnectReason);
        objectWaiters.forEach(waiter -> { synchronized (waiter) { waiter.notify();;} });
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object message) throws Exception {
        if (!objectWaiters.removeIf(mw -> mw.test(message))) {
            if (message instanceof AbstractMessage) {
                System.out.println("Message received: " + message.getClass().getSimpleName());
                if (message instanceof FileSendingMessage) NettyClient.getFileReceiver().createNewReceiving((FileSendingMessage) message);
                else if (message instanceof DisconnectMessage) {
                    disconnectReason = ((DisconnectMessage) message).getReason();
                    ctx.close().syncUninterruptibly();
                } else if (message instanceof CustomPayloadMessage) {

                } else if (message instanceof PlayerInfoRequestMessage) {
                    ctx.writeAndFlush(new PlayerInfoResponseMessage(((PlayerInfoRequestMessage) message).getPlayerName(), "NULL"));
                }
            } else if (message instanceof FileReceiving) {
                FileReceiving receiving = (FileReceiving) message;
                System.out.println("File received: " + receiving.getFile().getAbsolutePath() + ", size: " + receiving.getMaxFileLength() + ", time: " + (System.currentTimeMillis() - receiving.getStartTimestamp()) + " ms");
            }
        }
    }

    public static <T> Optional<T> waitForObject(Class<T> clazz, Predicate<T> condition, long timeoutMillis) {
        InboundObjectWaiter<T> objectWaiter = new InboundObjectWaiter<>(clazz, condition);
        synchronized (objectWaiter) {
            try {
                objectWaiters.add(objectWaiter);
                objectWaiter.wait(timeoutMillis);
                if (objectWaiter.result == null) {
                    objectWaiters.remove(objectWaiter);
                    return Optional.empty();
                } else return Optional.of(objectWaiter.result);
            } catch (InterruptedException e) {
                objectWaiters.remove(objectWaiter);
                return Optional.empty();
            }
        }
    }

    private static class InboundObjectWaiter <T> {

        private final Class<T> clazz;
        private final Predicate<T> condition;
        private T result = null;

        InboundObjectWaiter(Class<T> clazz, Predicate<T> condition) {
            this.clazz = clazz;
            this.condition = condition;
        }

        boolean test(Object object) {
            if (clazz == object.getClass() && (condition == null || condition.test((T) object))) {
                this.result = (T) object;
                synchronized (this) { this.notify(); }
                return true;
            }
            return false;
        }

    }

}