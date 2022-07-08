package com.terriblefriends.serverutilities.mixin.shadowfix.blockentity;

import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ThreadedAnvilChunkStorage.class)
public interface ThreadedAnvilChunkStorageAccess {
    @Accessor
    @Mutable
    Long2ObjectLinkedOpenHashMap<ChunkHolder> getChunksToUnload();
    @Accessor
    @Mutable
    LongSet getLoadedChunks();
    @Accessor
    @Mutable
    Long2LongMap getChunkToNextSaveTimeMs();
    @Accessor
    @Mutable
    void setChunksToUnload(Long2ObjectLinkedOpenHashMap<ChunkHolder> long2ObjectLinkedOpenHashMap);
    @Accessor
    @Mutable
    void setLoadedChunks(LongSet longSet);
    @Accessor
    @Mutable
    void setChunkToNextSaveTimeMs(Long2LongMap long2LongMap);
    @Invoker
    boolean invokeUpdateHolderMap();

}
