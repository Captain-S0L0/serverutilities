package com.terriblefriends.serverutilities.mixin.shadowfix.player;

import net.minecraft.entity.Entity;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {

    @Inject(at=@At("HEAD"),method="Lnet/minecraft/server/PlayerManager;remove(Lnet/minecraft/server/network/ServerPlayerEntity;)V")
    private void removeDestroyShadows(ServerPlayerEntity player, CallbackInfo ci) {
        player.getWorld().removePlayer(player, Entity.RemovalReason.UNLOADED_WITH_PLAYER);
        if (player.hasVehicle()) {
            Entity entity = player.getRootVehicle();
            if (entity.hasPlayerRider()) {
                entity.streamPassengersAndSelf().forEach((entityx) -> entityx.setRemoved(Entity.RemovalReason.UNLOADED_WITH_PLAYER));
            }
        }
    }
}
