package com.terriblefriends.serverutilities.mixin.shadowfix.entity;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.UUID;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {
    ItemEntity ie = (ItemEntity) (Object) this;
    @Shadow private int pickupDelay;
    @Shadow @Nullable private UUID owner;


    @Inject(method="onPlayerCollision(Lnet/minecraft/entity/player/PlayerEntity;)V",at=@At("HEAD"),cancellable = true)
    private void CollisionMixin(PlayerEntity player, CallbackInfo ci) {
        if (!ie.world.isClient) {
            ItemStack itemStack = ie.getStack();
            Item item = itemStack.getItem();
            int i = itemStack.getCount();
            if (this.pickupDelay == 0 && (this.owner == null || this.owner.equals(player.getUuid())) && player.getInventory().insertStack(itemStack)) {
                player.sendPickup(ie, i);
                if (itemStack.isEmpty()) {
                    ie.discard();
                }

                player.increaseStat(Stats.PICKED_UP.getOrCreateStat(item), i);
                player.triggerItemPickedUpByEntityCriteria(ie);
            }

        }
        if (ci.isCancellable()) {ci.cancel();}
    }
}
