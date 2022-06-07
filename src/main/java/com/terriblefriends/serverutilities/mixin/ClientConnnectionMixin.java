package com.terriblefriends.serverutilities.mixin;

import com.terriblefriends.serverutilities.access.ClientConnectionAccess;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.OffThreadException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public class ClientConnnectionMixin implements ClientConnectionAccess{
    @Shadow private void sendQueuedPackets() {}
    @Shadow private PacketListener packetListener;
    @Shadow private boolean disconnected;
    @Shadow private Channel channel;
    @Shadow private int ticks;
    @Shadow protected void updateStats() {}
    @Shadow private static <T extends PacketListener>void handlePacket(Packet<T> packet, PacketListener listener) {}
    @Shadow private int packetsReceivedCounter;
    @Shadow private int packetsSentCounter;
    @Shadow private float averagePacketsSent;
    @Shadow private float averagePacketsReceived;
    @Shadow @Final private static Logger LOGGER;
    private int actionPacketsReceivedCounter;
    private float averageActionPacketsReceived;

    public float getAverageActionPacketsReceived() {return this.averageActionPacketsReceived;}

    private ClientConnection cc = (ClientConnection) (Object) this;

    @Inject(method="tick",at=@At("HEAD"),cancellable = true)
    private void tickMixin(CallbackInfo ci) { //implements actionPacketsRecieved, also processes stats every tick instead of every second to get best naughty behavior detection
        this.sendQueuedPackets();
        if (this.packetListener instanceof ServerLoginNetworkHandler) {
            ((ServerLoginNetworkHandler)this.packetListener).tick();
        }

        if (this.packetListener instanceof ServerPlayNetworkHandler) {
            ((ServerPlayNetworkHandler)this.packetListener).tick();
        }

        if (!cc.isOpen() && !this.disconnected) {
            cc.handleDisconnection();
        }

        if (this.channel != null) {
            this.channel.flush();
        }
        this.packetsSentCounter = 0;
        this.packetsReceivedCounter = 0;
        this.actionPacketsReceivedCounter = 0;
        updateStats();
        this.ticks++;
        if (ci.isCancellable()) {ci.cancel();}
    }

    @Inject(method="channelRead0",at=@At("HEAD"),cancellable = true)
    private void channelRead0Mixin(ChannelHandlerContext channelHandlerContext, Packet<?> packet, CallbackInfo ci) {
        if (this.channel.isOpen()) {
            //limits ratelimit detection to movement / actions, should fix packet exploits while allowing fast crafting etc.
            if (packet instanceof PlayerActionC2SPacket) {++this.actionPacketsReceivedCounter;}
            if (packet instanceof PlayerInteractBlockC2SPacket) {++this.actionPacketsReceivedCounter;}
            if (packet instanceof PlayerInteractEntityC2SPacket) {++this.actionPacketsReceivedCounter;}
            if (packet instanceof PlayerMoveC2SPacket) {++this.actionPacketsReceivedCounter;}
            ++this.packetsReceivedCounter;
            this.updateStats();
            if (this.channel.isOpen()) {
                try {
                    handlePacket(packet, this.packetListener);
                } catch (OffThreadException var4) {
                } catch (ClassCastException var5) {
                    LOGGER.error("Received {} that couldn't be processed", packet.getClass(), var5);
                    cc.disconnect(Text.translatable("multiplayer.disconnect.invalid_packet"));
                }
            }
        }
        if (ci.isCancellable()) {ci.cancel();}
    }

    @Inject(method="updateStats",at=@At("HEAD"),cancellable = true)
    private void updateStatsMixin(CallbackInfo ci) { //implements actionPacketsRecieved
        this.averagePacketsSent = MathHelper.lerp(0.75F, (float)this.packetsSentCounter, this.averagePacketsSent);
        this.averagePacketsReceived = MathHelper.lerp(0.75F, (float)this.packetsReceivedCounter, this.averagePacketsReceived);
        this.averageActionPacketsReceived = MathHelper.lerp(0.75F, (float)this.actionPacketsReceivedCounter, this.averageActionPacketsReceived);
    }
}
