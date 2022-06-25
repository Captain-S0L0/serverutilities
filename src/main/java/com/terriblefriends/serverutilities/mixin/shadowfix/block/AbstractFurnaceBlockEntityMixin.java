package com.terriblefriends.serverutilities.mixin.shadowfix.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractFurnaceBlockEntity.class)
public class AbstractFurnaceBlockEntityMixin {

    @Inject(at=@At("HEAD"),method="tick",cancellable = true)
    private static void shadowMaker(World world, BlockPos pos, BlockState state, AbstractFurnaceBlockEntity blockEntity, CallbackInfo ci) {
        if (blockEntity.getCustomName() != null && blockEntity.getCustomName().getString().equals("Shadow")) {

            ItemStack in = (blockEntity.inventory.get(0));
            ItemStack out = (blockEntity.inventory.get(2));

            if (in != out) {
                blockEntity.inventory.set(2,in);
            }
            if (ci.isCancellable()) {ci.cancel();}
        }
    }
}
