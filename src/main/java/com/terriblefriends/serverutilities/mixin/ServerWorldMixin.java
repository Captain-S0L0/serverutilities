package com.terriblefriends.serverutilities.mixin;

import com.terriblefriends.serverutilities.access.ServerWorldAccess;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.level.ServerWorldProperties;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin implements ServerWorldAccess {
    @Shadow @Final private ServerWorldProperties worldProperties;

    public ServerWorldProperties getServerWorldProperties() {
        return this.worldProperties;
    } //used in weather commands to get / edit weather stuff
}
