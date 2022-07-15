package com.terriblefriends.serverutilities.mixin.shadowfix.player;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin {
    PlayerInventory PI_instance = (PlayerInventory) (Object) this;

    @Inject(at=@At("HEAD"),method="Lnet/minecraft/entity/player/PlayerInventory;writeNbt(Lnet/minecraft/nbt/NbtList;)Lnet/minecraft/nbt/NbtList;")
    private void writeNbtDestroyShadows(NbtList nbtList, CallbackInfoReturnable<NbtList> cir) {
        if (PI_instance.player.getRemovalReason() == Entity.RemovalReason.UNLOADED_WITH_PLAYER) {
            int i;
            for (i = 0; i < PI_instance.main.size(); ++i) {
                if (!(PI_instance.main.get(i)).isEmpty()) {
                    ItemStack itemStack = PI_instance.main.get(i);
                    PI_instance.main.set(i,itemStack.copy());
                    itemStack.setCount(0);
                }
            }

            for (i = 0; i < PI_instance.armor.size(); ++i) {
                if (!(PI_instance.armor.get(i)).isEmpty()) {
                    ItemStack itemStack = PI_instance.armor.get(i);
                    PI_instance.armor.set(i,itemStack.copy());
                    itemStack.setCount(0);
                }
            }

            for (i = 0; i < PI_instance.offHand.size(); ++i) {
                if (!(PI_instance.offHand.get(i)).isEmpty()) {
                    ItemStack itemStack = PI_instance.offHand.get(i);
                    PI_instance.offHand.set(i,itemStack.copy());
                    itemStack.setCount(0);
                }
            }
        }
    }
}
