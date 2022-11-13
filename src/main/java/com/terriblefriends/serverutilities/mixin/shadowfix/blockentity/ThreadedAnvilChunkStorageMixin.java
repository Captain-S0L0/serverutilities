package com.terriblefriends.serverutilities.mixin.shadowfix.blockentity;

import com.terriblefriends.serverutilities.access.BlockEntityAccessor;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.*;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Mixin(ThreadedAnvilChunkStorage.class)
public abstract class ThreadedAnvilChunkStorageMixin {
    @Shadow @Final ServerWorld world;
    @Shadow @Final @Mutable private Long2ObjectLinkedOpenHashMap<ChunkHolder> chunksToUnload;
    @Shadow @Final @Mutable private LongSet loadedChunks;
    @Shadow @Final @Mutable private Long2LongMap chunkToNextSaveTimeMs;
    @Shadow @Final @Mutable private ServerLightingProvider lightingProvider;
    @Shadow @Final @Mutable private WorldGenerationProgressListener worldGenerationProgressListener;
    @Shadow @Final @Mutable private Queue<Runnable> unloadTaskQueue;
    @Shadow @Final private static Logger LOGGER;
    @Shadow protected abstract boolean save(Chunk chunk);

    @Shadow protected abstract void tryUnloadChunk(long pos, ChunkHolder holder);

    /*@Redirect(at=@At(value="INVOKE",target="Lnet/minecraft/server/world/ThreadedAnvilChunkStorage;tryUnloadChunk(JLnet/minecraft/server/world/ChunkHolder;)V"),method="unloadChunks")
    private void test(ThreadedAnvilChunkStorage instance, long pos, ChunkHolder holder) {
        Chunk chunk = holder.getCurrentChunk();
        if (chunk != null) {
            for (BlockPos blockPos : chunk.getBlockEntityPositions()) {
                BlockEntity blockEntity = chunk.getBlockEntity(blockPos);
                if (blockEntity != null) {
                    ((BlockEntityAccessor) blockEntity).destroyShadows();
                }
            }
        }
        tryUnloadChunk(pos, holder);

        /*CompletableFuture<Chunk> completableFuture = holder.getSavingFuture();
        Consumer<Chunk> var10001 = (chunk) -> {
            CompletableFuture<Chunk> completableFuture2 = holder.getSavingFuture();
            if (completableFuture2 != completableFuture) {
                test(instance, pos, holder);
            } else {
                if (chunksToUnload.remove(pos, holder) && chunk != null) {
                    for (BlockPos blockPos : chunk.getBlockEntityPositions()) {
                        BlockEntity blockEntity = chunk.getBlockEntity(blockPos);
                        if (blockEntity != null) {
                            ((BlockEntityAccessor) blockEntity).createNbtWithIdentifyingDataDestroyShadows();
                        }
                    }

                    if (chunk instanceof WorldChunk) {
                        ((WorldChunk) chunk).setLoadedToWorld(false);
                    }
                    save(chunk);
                    if (loadedChunks.remove(pos) && chunk instanceof WorldChunk) {
                        WorldChunk worldChunk = (WorldChunk) chunk;
                        this.world.unloadEntities(worldChunk);
                    }

                    this.lightingProvider.updateChunkStatus(chunk.getPos());
                    this.lightingProvider.tick();
                    this.worldGenerationProgressListener.setChunkStatus(chunk.getPos(), null);

                    chunkToNextSaveTimeMs.remove(chunk.getPos().toLong());
                }
            }

        };
        Queue var10002 = this.unloadTaskQueue;
        Objects.requireNonNull(var10002);
        completableFuture.thenAcceptAsync(var10001, var10002::add).whenComplete((void_, throwable) -> {
            if (throwable != null) {
                LOGGER.error("Failed to save chunk {}", holder.getPos(), throwable);
            }

        });*/
    //}

    /*@Inject(at=@At("HEAD"),method="save(Lnet/minecraft/world/chunk/Chunk;)Z", cancellable = true)
    private void blockEntityShadowDeletion(Chunk chunk, CallbackInfoReturnable<Boolean> cir) {
        if (chunk instanceof WorldChunk && !((WorldChunk)chunk).loadedToWorld) {
            chunk.setNeedsSaving(true);
            for (BlockPos blockPos : chunk.getBlockEntityPositions()) {
                BlockEntity blockEntity = chunk.getBlockEntity(blockPos);
                if (blockEntity != null) {
                    ((BlockEntityAccessor) blockEntity).destroyShadows(chunk);
                }
            }
        }
    }*/

    @Inject(at=@At("HEAD"),method="save(Lnet/minecraft/world/chunk/Chunk;)Z", cancellable = true)
    private void blockEntityShadowDeletion(Chunk chunk, CallbackInfoReturnable<Boolean> cir) {
        if (chunk instanceof WorldChunk && !((WorldChunk)chunk).loadedToWorld && chunk.getStatus().getChunkType() == ChunkStatus.ChunkType.LEVELCHUNK) {
            //boolean hasBeenEdited = false;
            for (BlockPos blockPos : chunk.getBlockEntityPositions()) {
                BlockEntity blockEntity = chunk.getBlockEntity(blockPos);
                if (blockEntity != null) {
                    if (((BlockEntityAccessor) blockEntity).destroyShadows(chunk)) {
                        blockEntity.markDirty();
                        //hasBeenEdited = true;
                        chunk.setNeedsSaving(true);
                    }
                }
            }
            /*if (hasBeenEdited) {
                cir.setReturnValue(false);
                cir.cancel();
            }*/
        }
    }
}
