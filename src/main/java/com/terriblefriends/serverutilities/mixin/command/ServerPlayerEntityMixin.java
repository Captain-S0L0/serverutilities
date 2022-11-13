package com.terriblefriends.serverutilities.mixin.command;

import com.terriblefriends.serverutilities.access.ServerPlayerEntityAccess;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin implements ServerPlayerEntityAccess {
    private String uniqueScoreboardName;

    public String getUniqueScoreboardName() {
        return uniqueScoreboardName;
    }

    public void setUniqueScoreboardName(String string) {
        uniqueScoreboardName = string;
    }
}
