package com.terriblefriends.serverutilities.mixin.shadowfix.player;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @Shadow protected EnderChestInventory enderChestInventory;
    PlayerEntity PE_instance = (PlayerEntity) (Object) this;

    @Inject(at=@At("HEAD"),method="writeCustomDataToNbt")
    private void toNbtListDestroyShadows(NbtCompound nbt, CallbackInfo ci) {
        if (PE_instance.getRemovalReason() == Entity.RemovalReason.UNLOADED_WITH_PLAYER) {
            for (int i = 0; i < enderChestInventory.size(); ++i) {
                if (!(enderChestInventory.getStack(i)).isEmpty()) {
                    ItemStack itemStack = enderChestInventory.getStack(i);
                    enderChestInventory.setStack(i,itemStack.copy());
                    itemStack.setCount(0);
                }
            }
        }
    }
}
