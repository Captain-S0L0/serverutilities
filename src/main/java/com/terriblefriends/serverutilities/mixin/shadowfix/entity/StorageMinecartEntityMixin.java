package com.terriblefriends.serverutilities.mixin.shadowfix.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.StorageMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StorageMinecartEntity.class)
public abstract class StorageMinecartEntityMixin extends AbstractMinecartEntity {
    @Shadow private DefaultedList<ItemStack> inventory;

    StorageMinecartEntity SME_instance = (StorageMinecartEntity) (Object) this;

    protected StorageMinecartEntityMixin(EntityType<?> entityType, World world) {
        super(entityType, world);
    }


    @Inject(at=@At("HEAD"),method="writeCustomDataToNbt(Lnet/minecraft/nbt/NbtCompound;)V")
    private void writeCustomDataToNbtDestroyShadows(NbtCompound nbt, CallbackInfo ci) {
        RemovalReason reason = SME_instance.getRemovalReason();
        if (reason == RemovalReason.UNLOADED_TO_CHUNK || reason == RemovalReason.UNLOADED_WITH_PLAYER || reason == RemovalReason.CHANGED_DIMENSION) {
            for(int i = 2; i < inventory.size(); ++i) {
                ItemStack itemStack = inventory.get(i);
                if (!itemStack.isEmpty()) {
                    inventory.set(i,itemStack.copy());
                    itemStack.setCount(0);
                }
            }
        }
    }

    public Type getMinecartType() {
        return null;
    }
}
