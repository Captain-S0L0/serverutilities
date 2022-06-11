package com.terriblefriends.serverutilities.mixin;

import com.terriblefriends.serverutilities.ServerUtilities;
import com.terriblefriends.serverutilities.access.ClientConnectionAccess;
import com.terriblefriends.serverutilities.access.RateLimitedConnectionAccess;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.*;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RateLimitedConnection.class)
public class RateLimitedConnectionMixin extends ClientConnection implements RateLimitedConnectionAccess {
    @Shadow @Final private int rateLimit;
    @Shadow @Final private static Logger LOGGER;
    @Shadow @Final private static Text RATE_LIMIT_EXCEEDED_MESSAGE;
    private int actionPacketsReceivedCounter;
    private float averageActionPacketsReceived;

    RateLimitedConnection RLC_instance = (RateLimitedConnection) (Object) this;

    public RateLimitedConnectionMixin(NetworkSide side) {
        super(side);
    }

    public void increaseActionPacketsReceived() {
        this.actionPacketsReceivedCounter++;
    }

    @Inject(method="updateStats",at=@At("HEAD"),cancellable = true)
    private void updateStatsMixin(CallbackInfo ci) { //implements playerAction packet rate limit
        super.updateStats();
        this.averageActionPacketsReceived = MathHelper.lerp(0.75F, (float)this.actionPacketsReceivedCounter, this.averageActionPacketsReceived);
        this.actionPacketsReceivedCounter = 0;


        float f = this.getAveragePacketsReceived();
        if (f > (float)this.rateLimit) {
            LOGGER.warn("Player exceeded rate-limit (sent {} packets per second)", f);
            this.send(new DisconnectS2CPacket(RATE_LIMIT_EXCEEDED_MESSAGE), (future) -> {
                this.disconnect(RATE_LIMIT_EXCEEDED_MESSAGE);
            });
            this.disableAutoRead();
        }
        f = actionPacketsReceivedCounter;
        if (f > (float) ServerUtilities.actionRateLimit) {
            LOGGER.warn("Player exceeded action rate-limit (sent {} packets per second)", f);
            this.send(new DisconnectS2CPacket(RATE_LIMIT_EXCEEDED_MESSAGE), (future) -> {
                this.disconnect(RATE_LIMIT_EXCEEDED_MESSAGE);
            });
            this.disableAutoRead();
        }
        if (ci.isCancellable()) {ci.cancel();}
    }
}
