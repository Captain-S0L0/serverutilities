package com.terriblefriends.serverutilities.mixin.shadowfix.player;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    PlayerEntity PE_instance = (PlayerEntity) (Object) this;

    @Redirect(at=@At(value="INVOKE",target="Lnet/minecraft/inventory/EnderChestInventory;toNbtList()Lnet/minecraft/nbt/NbtList;"),method="writeCustomDataToNbt")
    private NbtList toNbtListDestroyShadows(EnderChestInventory instance) {
        if (PE_instance.getRemovalReason() == Entity.RemovalReason.UNLOADED_WITH_PLAYER) {
            NbtList nbtList = new NbtList();

            for(int i = 0; i < instance.size(); ++i) {
                ItemStack itemStack = instance.getStack(i);
                if (!itemStack.isEmpty()) {
                    NbtCompound nbtCompound = new NbtCompound();
                    nbtCompound.putByte("Slot", (byte)i);
                    itemStack.writeNbt(nbtCompound);
                    nbtList.add(nbtCompound);
                    instance.getStack(i).setCount(0);
                }
            }
            return nbtList;
        }
        else {
            return instance.toNbtList();
        }
    }

}
