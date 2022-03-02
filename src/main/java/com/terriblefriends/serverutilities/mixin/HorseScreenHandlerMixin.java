package com.terriblefriends.serverutilities.mixin;

import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.HorseScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HorseScreenHandler.class)
public class HorseScreenHandlerMixin {
    @Shadow
    private HorseBaseEntity entity;

    @Inject(method="transferSlot(Lnet/minecraft/entity/player/PlayerEntity;I)Lnet/minecraft/item/ItemStack;", at=@At("HEAD"), cancellable = true)
    private void transferSlot(PlayerEntity player, int index, CallbackInfoReturnable<ItemStack> cir) { //fix dupe by checking if horse still exists before accepting item move
        if (entity.isRemoved() && cir.isCancellable()) {cir.setReturnValue(ItemStack.EMPTY);}
    }
}
