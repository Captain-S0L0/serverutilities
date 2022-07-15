package com.terriblefriends.serverutilities.mixin.shadowfix.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
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

    @Inject(at=@At("HEAD"),method="writeCustomDataToNbt(Lnet/minecraft/nbt/NbtCompound;)V",cancellable = true)
    private void writeCustomDataToNbtDestroyShadows(NbtCompound nbt, CallbackInfo ci) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("ChestedHorse", ADE_instance.hasChest());
        if (ADE_instance.hasChest()) {
            NbtList nbtList = new NbtList();

            for(int i = 2; i < this.items.size(); ++i) {
                ItemStack itemStack = this.items.getStack(i);
                if (!itemStack.isEmpty()) {
                    NbtCompound nbtCompound = new NbtCompound();
                    nbtCompound.putByte("Slot", (byte)i);
                    itemStack.writeNbt(nbtCompound);
                    nbtList.add(nbtCompound);
                    RemovalReason reason = ADE_instance.getRemovalReason();
                    if (reason == RemovalReason.UNLOADED_TO_CHUNK || reason == RemovalReason.UNLOADED_WITH_PLAYER || reason == RemovalReason.CHANGED_DIMENSION) {
                        this.items.setStack(i,itemStack.copy());
                        itemStack.setCount(0);
                    }
                }
            }

            nbt.put("Items", nbtList);
        }

        if (ci.isCancellable()) {ci.cancel();}
    }
}
