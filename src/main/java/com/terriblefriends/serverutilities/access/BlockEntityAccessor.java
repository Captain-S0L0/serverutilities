package com.terriblefriends.serverutilities.access;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.chunk.Chunk;

public interface BlockEntityAccessor {
    void destroyShadows(Chunk chunk);
    NbtCompound createNbtShulkerDestroyShadows();
}
