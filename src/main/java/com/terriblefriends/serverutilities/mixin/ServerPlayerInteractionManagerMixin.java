package com.terriblefriends.serverutilities.mixin;

import com.terriblefriends.serverutilities.ServerUtilities;
import com.terriblefriends.serverutilities.access.ServerPlayerEntityAccess;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerInteractionManager.class)
public class ServerPlayerInteractionManagerMixin {
    @Inject(method="interactBlock(Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;Lnet/minecraft/util/Hand;Lnet/minecraft/util/hit/BlockHitResult;)Lnet/minecraft/util/ActionResult;", at=@At("HEAD"),cancellable = true)
    private void InteractBlockMixin(ServerPlayerEntity player, World world, ItemStack stack, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) { //no opening inventories
        if (player.getWorld().getGameRules().getBoolean(ServerUtilities.ADVENTURE_GHOST_MODE) && ((ServerPlayerEntityAccess)player).isAdventure() && cir.isCancellable()) {
            cir.setReturnValue(ActionResult.FAIL);
            cir.cancel();
        }
    }
    @Inject(method="interactItem(Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;", at=@At("HEAD"),cancellable = true)
    private void InteractItemMixin(ServerPlayerEntity player, World world, ItemStack stack, Hand hand, CallbackInfoReturnable<ActionResult> cir) { //no using items (eggs) either
        if (player.getWorld().getGameRules().getBoolean(ServerUtilities.ADVENTURE_GHOST_MODE) && ((ServerPlayerEntityAccess)player).isAdventure() && cir.isCancellable()) {
            cir.setReturnValue(ActionResult.FAIL);
            cir.cancel();
        }
    }
}
