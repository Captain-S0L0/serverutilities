package com.terriblefriends.serverutilities.mixin.shadowfix.screenhandler.transferfix;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.BeaconScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BeaconScreenHandler.class)
public class BeaconScreenHandlerMixin {
    ScreenHandler SH_instance = (ScreenHandler) (Object) this;

    @Inject(at=@At("HEAD"),method="transferSlot")
    private void destroyShadowsOnTransferSlot(PlayerEntity player, int index, CallbackInfoReturnable<ItemStack> cir) {
        Slot slot = SH_instance.slots.get(index);
        ItemStack originalStack = slot.getStack();
        slot.setStack(originalStack.copy());
        originalStack.setCount(0);
    }
}
