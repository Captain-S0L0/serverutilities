package com.terriblefriends.serverutilities.mixin;

import com.terriblefriends.serverutilities.ServerUtilities;
import com.terriblefriends.serverutilities.access.ServerPlayerEntityAccess;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenHandler.class)
public class ScreenHandlerMixin {
    @Inject(method="internalOnSlotClick(IILnet/minecraft/screen/slot/SlotActionType;Lnet/minecraft/entity/player/PlayerEntity;)V",at=@At("HEAD"),cancellable = true)
    private void internalOnSlotClick(int slotIndex, int button, SlotActionType type, PlayerEntity player, CallbackInfo ci) { //make sure adventure mode can't inventory dump
        if (player.getWorld().getGameRules().getBoolean(ServerUtilities.ADVENTURE_GHOST_MODE) && ((ServerPlayerEntityAccess)player).isAdventure() && (type == SlotActionType.THROW || slotIndex == -999) && ci.isCancellable()) {
            player.currentScreenHandler.updateToClient();
            ci.cancel();
        }
    }
}
