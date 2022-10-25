package com.terriblefriends.serverutilities.mixin.shadowfix.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.ChestBoatEntity;
import net.minecraft.entity.vehicle.StorageMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChestBoatEntity.class)
public abstract class ChestBoatEntityMixin extends BoatEntity {
    @Shadow @Nullable private Identifier lootTableId;
    @Shadow private long lootTableSeed;
    @Shadow private DefaultedList<ItemStack> inventory;

    ChestBoatEntity CBE_instance = (ChestBoatEntity) (Object) this;

    public ChestBoatEntityMixin(EntityType<? extends BoatEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(at=@At("HEAD"),method="writeCustomDataToNbt(Lnet/minecraft/nbt/NbtCompound;)V",cancellable = true)
    private void writeCustomDataToNbtDestroyShadows(NbtCompound nbt, CallbackInfo ci) {
        super.writeCustomDataToNbt(nbt);
        if (this.lootTableId != null) {
            nbt.putString("LootTable", this.lootTableId.toString());
            if (this.lootTableSeed != 0L) {
                nbt.putLong("LootTableSeed", this.lootTableSeed);
            }
        } else {
            NbtList nbtList = new NbtList();

            for(int i = 0; i < this.inventory.size(); ++i) {
                ItemStack itemStack = this.inventory.get(i);
                if (!itemStack.isEmpty()) {
                    NbtCompound nbtCompound = new NbtCompound();
                    nbtCompound.putByte("Slot", (byte)i);
                    itemStack.writeNbt(nbtCompound);
                    nbtList.add(nbtCompound);
                    RemovalReason reason = CBE_instance.getRemovalReason();
                    if (reason == RemovalReason.UNLOADED_TO_CHUNK || reason == RemovalReason.UNLOADED_WITH_PLAYER || reason == RemovalReason.CHANGED_DIMENSION) {
                        this.inventory.set(i,itemStack.copy());
                        itemStack.setCount(0);
                    }
                }
            }
            nbt.put("Items", nbtList);
        }

        if (ci.isCancellable()) {ci.cancel();}
    }

    public Type getMinecartType() {
        return null;
    }
}
