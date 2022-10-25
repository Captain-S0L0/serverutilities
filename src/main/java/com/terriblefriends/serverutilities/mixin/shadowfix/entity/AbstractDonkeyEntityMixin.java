package com.terriblefriends.serverutilities.mixin.shadowfix.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractDonkeyEntity.class)
public class AbstractDonkeyEntityMixin extends AbstractHorseEntity {
    protected AbstractDonkeyEntityMixin(EntityType<? extends AbstractHorseEntity> entityType, World world) {
        super(entityType, world);
    }

    AbstractDonkeyEntity ADE_instance = (AbstractDonkeyEntity) (Object) this;

    @Inject(at=@At("HEAD"),method="writeCustomDataToNbt(Lnet/minecraft/nbt/NbtCompound;)V")
    private void writeCustomDataToNbtDestroyShadows(NbtCompound nbt, CallbackInfo ci) {
        RemovalReason reason = ADE_instance.getRemovalReason();
        if (ADE_instance.hasChest() && reason == RemovalReason.UNLOADED_TO_CHUNK || reason == RemovalReason.UNLOADED_WITH_PLAYER || reason == RemovalReason.CHANGED_DIMENSION) {
            for(int i = 2; i < this.items.size(); ++i) {
                ItemStack itemStack = this.items.getStack(i);
                if (!itemStack.isEmpty()) {
                    this.items.setStack(i,itemStack.copy());
                    itemStack.setCount(0);
                }
            }

        }
    }
}
