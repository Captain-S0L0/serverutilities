package com.terriblefriends.serverutilities.mixin.dupefix;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenHandler.class)
public class ScreenHandlerMixin {
    ScreenHandler SH_instance = (ScreenHandler) (Object) this;

    @Inject(at=@At("HEAD"),method="onSlotClick",cancellable = true)
    private void preventItemTransfersOfClosedInventories(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
        if (!SH_instance.canUse(player) && ci.isCancellable()) {
            ci.cancel();
        }
    }

}
