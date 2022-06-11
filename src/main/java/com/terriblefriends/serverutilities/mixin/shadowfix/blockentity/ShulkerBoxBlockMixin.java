package com.terriblefriends.serverutilities.mixin.shadowfix.blockentity;

import com.terriblefriends.serverutilities.access.BlockEntityAccessor;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ShulkerBoxBlock.class)
public class ShulkerBoxBlockMixin extends BlockWithEntity {
    ShulkerBoxBlock SBB_instance = (ShulkerBoxBlock) (Object) this;

    public ShulkerBoxBlockMixin(Settings settings) {
        super(settings);
    }

    @Redirect(at=@At(value="INVOKE",target="Lnet/minecraft/block/entity/BlockEntity;setStackNbt(Lnet/minecraft/item/ItemStack;)V"),method="onBreak")
    public void setStackNbtDestroyShadows(BlockEntity instance, ItemStack stack) {
        System.out.println("nerd2");
        BlockItem.setBlockEntityNbt(stack, instance.getType(), ((BlockEntityAccessor)instance).createNbtShulkerDestroyShadows());
    }

    @Inject(at=@At("HEAD"),method="Lnet/minecraft/block/ShulkerBoxBlock;getDroppedStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/loot/context/LootContext$Builder;)Ljava/util/List;",cancellable = true)
    private void getDroppedStacksDestroyShadows(BlockState state, LootContext.Builder builder, CallbackInfoReturnable<List<ItemStack>> cir) {
        BlockEntity blockEntity = builder.getNullable(LootContextParameters.BLOCK_ENTITY);
        if (blockEntity instanceof ShulkerBoxBlockEntity) {
            ShulkerBoxBlockEntity shulkerBoxBlockEntity = (ShulkerBoxBlockEntity)blockEntity;
            builder = builder.putDrop(ShulkerBoxBlock.CONTENTS, (context, consumer) -> {
                for(int i = 0; i < shulkerBoxBlockEntity.size(); ++i) {
                    consumer.accept(shulkerBoxBlockEntity.getStack(i));
                    shulkerBoxBlockEntity.getStack(i).setCount(0);
                }

            });
        }

        cir.setReturnValue(super.getDroppedStacks(state, builder));
    }

    @Nullable
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ShulkerBoxBlockEntity(SBB_instance.getColor(), pos, state);
    }
}
