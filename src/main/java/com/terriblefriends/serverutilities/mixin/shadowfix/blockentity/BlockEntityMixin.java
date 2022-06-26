package com.terriblefriends.serverutilities.mixin.shadowfix.blockentity;

import com.terriblefriends.serverutilities.access.BlockEntityAccessor;
import net.minecraft.block.entity.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockEntity.class)
public class BlockEntityMixin implements BlockEntityAccessor {
    BlockEntity BE_instance = (BlockEntity) (Object) this;

    public void createNbtWithIdentifyingDataDestroyShadows() {
        NbtCompound returnValue = BE_instance.createNbt();

        DefaultedList<ItemStack> inventoryToClear = DefaultedList.ofSize(0, ItemStack.EMPTY);

        if (BE_instance instanceof ChestBlockEntity) {
            if (!returnValue.contains("LootTable")) {
                inventoryToClear = ((ChestBlockEntity)BE_instance).inventory;
            }
        }
        if (BE_instance instanceof BarrelBlockEntity) {
            if (!returnValue.contains("LootTable")) {
                inventoryToClear = ((BarrelBlockEntity)BE_instance).inventory;
            }
        }
        if (BE_instance instanceof ShulkerBoxBlockEntity) {
            if (!returnValue.contains("LootTable")) {
                inventoryToClear = ((ShulkerBoxBlockEntity)BE_instance).inventory;
            }
        }
        if (BE_instance instanceof DispenserBlockEntity) {
            if (!returnValue.contains("LootTable")) {
                inventoryToClear = ((DispenserBlockEntity)BE_instance).inventory;
            }
        }
        if (BE_instance instanceof HopperBlockEntity) {
            if (!returnValue.contains("LootTable")) {
                inventoryToClear = ((HopperBlockEntity)BE_instance).inventory;
            }
        }
        if (BE_instance instanceof BrewingStandBlockEntity) {
            inventoryToClear = ((BrewingStandBlockEntity)BE_instance).inventory;
        }
        if (BE_instance instanceof AbstractFurnaceBlockEntity) {
            inventoryToClear = ((AbstractFurnaceBlockEntity)BE_instance).inventory;
        }

        if (inventoryToClear.size() != 0) {
            NbtList nbtList = new NbtList();
            for(int i = 0; i < inventoryToClear.size(); ++i) {
                ItemStack itemStack = inventoryToClear.get(i);
                if (!itemStack.isEmpty()) {
                    ItemStack copyStack = itemStack.copy();
                    itemStack.setCount(0);
                    inventoryToClear.set(i,copyStack);
                }
            }
            returnValue.put("Items", nbtList);
        }
    }

    public NbtCompound createNbtShulkerDestroyShadows() {
        NbtCompound returnValue = BE_instance.createNbt();

        if (BE_instance instanceof ShulkerBoxBlockEntity) {
            if (!returnValue.contains("LootTable")) {
                NbtList nbtList = new NbtList();
                for(int i = 0; i < ((ShulkerBoxBlockEntity) BE_instance).inventory.size(); ++i) {
                    ItemStack itemStack = ((ShulkerBoxBlockEntity) BE_instance).inventory.get(i);
                    if (!itemStack.isEmpty()) {
                        NbtCompound nbtCompound = new NbtCompound();
                        nbtCompound.putByte("Slot", (byte)i);
                        itemStack.writeNbt(nbtCompound);
                        nbtList.add(nbtCompound);
                        ((ShulkerBoxBlockEntity)BE_instance).inventory.get(i).setCount(0);
                    }
                }
                returnValue.put("Items", nbtList);
            }
        }

        return returnValue;
    }
}
