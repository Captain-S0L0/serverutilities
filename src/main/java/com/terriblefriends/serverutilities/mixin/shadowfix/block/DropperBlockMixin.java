package com.terriblefriends.serverutilities.mixin.shadowfix.block;

import net.minecraft.block.DispenserBlock;
import net.minecraft.block.DropperBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPointerImpl;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DropperBlock.class)
public class DropperBlockMixin {
    private static final DispenserBehavior BEHAVIOR = new ItemDispenserBehavior();

    @Inject(at=@At("HEAD"),method="dispense",cancellable = true)
    private void dispenseDestroyShadows(ServerWorld world, BlockPos pos, CallbackInfo ci) {
        BlockPointerImpl blockPointerImpl = new BlockPointerImpl(world, pos);
        DispenserBlockEntity dispenserBlockEntity = blockPointerImpl.getBlockEntity();
        int i = dispenserBlockEntity.chooseNonEmptySlot(world.random);
        if (i < 0) {
            world.syncWorldEvent(1001, pos, 0);
        } else {
            ItemStack itemStack = dispenserBlockEntity.getStack(i);
            if (!itemStack.isEmpty()) {
                Direction direction = world.getBlockState(pos).get(DispenserBlock.FACING);
                Inventory inventory = HopperBlockEntity.getInventoryAt(world, pos.offset(direction));
                ItemStack itemStack2;
                if (inventory == null) {
                    BEHAVIOR.dispense(blockPointerImpl, itemStack);
                } else {
                    itemStack2 = HopperBlockEntity.transfer(dispenserBlockEntity, inventory, itemStack.copy().split(1), direction.getOpposite());
                    if (itemStack2.isEmpty()) {
                        itemStack.decrement(1);
                    }
                }

                dispenserBlockEntity.setStack(i, itemStack);
            }
        }

        if (ci.isCancellable()) {ci.cancel();}
    }

}
