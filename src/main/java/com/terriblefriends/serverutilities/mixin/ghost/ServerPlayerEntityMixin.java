package com.terriblefriends.serverutilities.mixin.ghost;

import com.terriblefriends.serverutilities.access.ServerPlayerEntityAccess;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin implements ServerPlayerEntityAccess {
    @Shadow ServerPlayerInteractionManager interactionManager;
    public boolean isAdventure() {
        return this.interactionManager.getGameMode() == GameMode.ADVENTURE;
    } //simplifies adventure mode checks
}
