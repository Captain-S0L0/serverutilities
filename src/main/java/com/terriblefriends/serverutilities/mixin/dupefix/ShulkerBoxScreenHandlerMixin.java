package com.terriblefriends.serverutilities.mixin.dupefix;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShulkerBoxScreenHandler.class)
public class ShulkerBoxScreenHandlerMixin {
    private ShulkerBoxScreenHandler sbsh = (ShulkerBoxScreenHandler) (Object) this;
    @Inject(method="transferSlot(Lnet/minecraft/entity/player/PlayerEntity;I)Lnet/minecraft/item/ItemStack;",at=@At("HEAD"),cancellable = true)
    private void transferSlotMixin(PlayerEntity player, int index, CallbackInfoReturnable<ItemStack> cir) { //patched dupe by checking if box still exists before accepting item move
        if (!sbsh.canUse(player)) {cir.setReturnValue(ItemStack.EMPTY);}
    }
}
