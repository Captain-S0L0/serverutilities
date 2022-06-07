package com.terriblefriends.serverutilities.mixin.command;

import com.terriblefriends.serverutilities.ServerUtilities;
import net.minecraft.block.Blocks;
import net.minecraft.item.EnderEyeItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(EnderEyeItem.class)
public class NoEndMixin {
    @Inject(method="useOnBlock(Lnet/minecraft/item/ItemUsageContext;)Lnet/minecraft/util/ActionResult;",at = @At("HEAD"), cancellable = true)
    private void useOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> callback) { //disables the end via gamerule, specifically by disabling placing eyes into portal frames. if your world has a 12 eye portal, then i guess you're out of luck :P
        if (context.getWorld().getGameRules().getBoolean(ServerUtilities.END_DISABLED)) {
            if (context.getWorld().getBlockState(context.getBlockPos()).getBlock() == Blocks.END_PORTAL_FRAME) {
                Text wrong = Text.literal("Hey, that's kinda like, not allowed. Don't get your panties in a twist though, the dragon fight is a community event and I just don't need someone cheesing it");
                ServerPlayerEntity serverPlayer = (ServerPlayerEntity) context.getPlayer();
                serverPlayer.sendMessage(wrong);
            }
            callback.setReturnValue(ActionResult.PASS);
            if (callback.isCancelled()) return;
        }
    }
}