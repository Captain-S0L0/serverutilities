package com.terriblefriends.serverutilities.access;

import net.minecraft.nbt.NbtCompound;

public interface BlockEntityAccessor {
    void createNbtWithIdentifyingDataDestroyShadows();
    NbtCompound createNbtShulkerDestroyShadows();
}
