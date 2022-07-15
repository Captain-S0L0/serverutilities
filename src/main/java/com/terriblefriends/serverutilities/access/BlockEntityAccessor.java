package com.terriblefriends.serverutilities.access;

import net.minecraft.nbt.NbtCompound;

public interface BlockEntityAccessor {
    void destroyShadows();
    NbtCompound createNbtShulkerDestroyShadows();
}
