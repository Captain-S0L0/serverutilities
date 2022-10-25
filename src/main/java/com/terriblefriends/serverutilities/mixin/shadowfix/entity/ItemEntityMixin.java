package com.terriblefriends.serverutilities.mixin.shadowfix.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.stat.Stats;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {
    ItemEntity IE_instance = (ItemEntity) (Object) this;
    @Shadow private int pickupDelay;
    @Shadow @Nullable
    private UUID owner;
    @Shadow private int health;
    @Shadow private int itemAge;

    @Inject(method="onPlayerCollision(Lnet/minecraft/entity/player/PlayerEntity;)V",at=@At("HEAD"),cancellable = true)
    private void CollisionMixin(PlayerEntity player, CallbackInfo ci) {
        if (!IE_instance.world.isClient) {
            ItemStack itemStack = IE_instance.getStack();
            Item item = itemStack.getItem();
            int i = itemStack.getCount();
            if (this.pickupDelay == 0 && (this.owner == null || this.owner.equals(player.getUuid())) && player.getInventory().insertStack(itemStack)) {
                player.sendPickup(IE_instance, i);
                if (itemStack.isEmpty()) {
                    IE_instance.discard();
                }

                player.increaseStat(Stats.PICKED_UP.getOrCreateStat(item), i);
                player.triggerItemPickedUpByEntityCriteria(IE_instance);
            }

        }
        if (ci.isCancellable()) {ci.cancel();}
    }

    @Inject(at=@At("HEAD"),method="writeCustomDataToNbt(Lnet/minecraft/nbt/NbtCompound;)V",cancellable = true)
    private void writeCustomDataToNbtDestroyShadows(NbtCompound nbt, CallbackInfo ci) {
        nbt.putShort("Health", (short)health);
        nbt.putShort("Age", (short)itemAge);
        nbt.putShort("PickupDelay", (short)pickupDelay);
        if (IE_instance.getThrower() != null) {
            nbt.putUuid("Thrower", IE_instance.getThrower());
        }

        if (IE_instance.getOwner() != null) {
            nbt.putUuid("Owner", IE_instance.getOwner());
        }

        if (!IE_instance.getStack().isEmpty()) {
            nbt.put("Item", IE_instance.getStack().writeNbt(new NbtCompound()));
            Entity.RemovalReason reason = IE_instance.getRemovalReason();
            if (reason == Entity.RemovalReason.UNLOADED_TO_CHUNK || reason == Entity.RemovalReason.UNLOADED_WITH_PLAYER || reason == Entity.RemovalReason.CHANGED_DIMENSION) {
                ItemStack copyStack = IE_instance.getStack().copy();
                IE_instance.getStack().setCount(0);
                IE_instance.setStack(copyStack);
            }
        }
        if (ci.isCancellable()) {ci.cancel();}
    }
}
