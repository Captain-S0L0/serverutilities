package com.terriblefriends.serverutilities.access;

import net.minecraft.nbt.NbtCompound;

public interface BlockEntityAccessor {
    NbtCompound createNbtWithIdentifyingDataDestroyShadows();
    NbtCompound createNbtShulkerDestroyShadows();
}
