package com.terriblefriends.serverutilities.mixin.ghost;

import com.terriblefriends.serverutilities.ServerUtilities;
import com.terriblefriends.serverutilities.access.ServerPlayerEntityAccess;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {
    ItemEntity ie = (ItemEntity) (Object) this;

    @Inject(method="onPlayerCollision(Lnet/minecraft/entity/player/PlayerEntity;)V",at=@At("HEAD"),cancellable = true)
    private void CollisionMixin(PlayerEntity player, CallbackInfo ci) { //make sure adventure mode can't pick stuff up
        if (player instanceof ServerPlayerEntity) {
            if (ie.getWorld().getGameRules().getBoolean(ServerUtilities.ADVENTURE_GHOST_MODE) && ((ServerPlayerEntityAccess)player).isAdventure() && ci.isCancellable()) {
                ci.cancel();
            }
        }
    }
}
