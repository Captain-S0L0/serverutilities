package com.terriblefriends.serverutilities.mixin;

import com.terriblefriends.serverutilities.ServerUtilities;
import com.terriblefriends.serverutilities.access.ClientConnectionAccess;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.RateLimitedConnection;
import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RateLimitedConnection.class)
public class RateLimitedConnectionMixin extends ClientConnection {
    @Shadow @Final private int rateLimit;
    @Shadow @Final private static Logger LOGGER;
    @Shadow @Final private static Text RATE_LIMIT_EXCEEDED_MESSAGE;

    public RateLimitedConnectionMixin(NetworkSide side) {
        super(side);
    }

    @Inject(method="updateStats",at=@At("HEAD"),cancellable = true)
    private void updateStatsMixin(CallbackInfo ci) { //implements playerAction packet rate limit
        super.updateStats();
        float f = this.getAveragePacketsReceived();
        if (f > (float)this.rateLimit) {
            LOGGER.warn("Player exceeded rate-limit (sent {} packets per second)", f);
            this.send(new DisconnectS2CPacket(RATE_LIMIT_EXCEEDED_MESSAGE), (future) -> {
                this.disconnect(RATE_LIMIT_EXCEEDED_MESSAGE);
            });
            this.disableAutoRead();
        }
        f = ((ClientConnectionAccess)this).getAverageActionPacketsReceived();
        if (f > (float) ServerUtilities.actionRateLimit) {
            LOGGER.warn("Player exceeded action rate-limit (sent {} packets per second)", f);
            this.send(new DisconnectS2CPacket(RATE_LIMIT_EXCEEDED_MESSAGE), (future) -> {
                this.disconnect(RATE_LIMIT_EXCEEDED_MESSAGE);
            });
            this.disableAutoRead();
        }
    }
}
