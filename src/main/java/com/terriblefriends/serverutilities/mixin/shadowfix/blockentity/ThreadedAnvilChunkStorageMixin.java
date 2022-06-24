package com.terriblefriends.serverutilities.mixin.shadowfix.blockentity;

import com.terriblefriends.serverutilities.access.BlockEntityAccessor;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerLightingProvider;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ChunkSerializer;
import net.minecraft.world.chunk.*;
import net.minecraft.world.poi.PointOfInterestStorage;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Mixin(ThreadedAnvilChunkStorage.class)
public abstract class ThreadedAnvilChunkStorageMixin {
    ThreadedAnvilChunkStorage TACS_instance = (ThreadedAnvilChunkStorage) (Object) this;

    @Shadow @Final ServerWorld world;


    @Shadow protected abstract void tryUnloadChunk(long pos, ChunkHolder holder);

    /*@Redirect(at=@At(value="INVOKE",target="Lnet/minecraft/world/ChunkSerializer;serialize(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/world/chunk/Chunk;)Lnet/minecraft/nbt/NbtCompound;"),method="save(Lnet/minecraft/world/chunk/Chunk;)Z")
    private NbtCompound serializeDestroyShadows(ServerWorld world, Chunk chunk) {
        NbtCompound returnValue = ChunkSerializer.serialize(world,chunk);

        //if (!this.world.getChunkManager().isChunkLoaded(chunk.getPos().x, chunk.getPos().z)) {

        //System.out.println(chunk.getPos().toString());
        //System.out.println(this.world.getChunkManager().threadedAnvilChunkStorage.getTicketManager().);

        if (((ChunkAccess)((Chunk)chunk)).isBeingSaved()) {
            if (chunk.getPos().equals(new ChunkPos(624, -1))) {
                System.out.println(chunk.getPos());
            }
            NbtList nbtList2 = new NbtList();

            for (BlockPos blockPos : chunk.getBlockEntityPositions()) {
                //nbtCompound3 = chunk.getPackedBlockEntityNbt(blockPos);

                if (chunk instanceof WorldChunk) {
                    System.out.println("world");
                    BlockEntity blockEntity = chunk.getBlockEntity(blockPos);
                    NbtCompound nbtCompound;
                    // && !blockEntity.isRemoved()
                    if (blockEntity != null) {
                        System.out.println(blockEntity);
                        //nbtCompound = blockEntity.createNbtWithIdentifyingData();

                        nbtCompound = ((BlockEntityAccessor)blockEntity).createNbtWithIdentifyingDataDestroyShadows();

                        //
                        nbtCompound.putBoolean("keepPacked", false);
                    } else {
                        nbtCompound = chunk.blockEntityNbts.get(blockPos);
                        if (nbtCompound != null) {
                            nbtCompound = nbtCompound.copy();
                            nbtCompound.putBoolean("keepPacked", true);
                        }
                    }
                    if (nbtCompound != null) {
                        nbtList2.add(nbtCompound);
                    }
                }
                //
                else if (chunk instanceof ProtoChunk) {
                    System.out.println("proto");

                    BlockEntity blockEntity = chunk.getBlockEntity(blockPos);
                    NbtCompound nbtCompound;
                    if (blockEntity != null) {
                        System.out.println(blockEntity);
                        nbtCompound = ((BlockEntityAccessor)blockEntity).createNbtWithIdentifyingDataDestroyShadows();
                    } else {
                        nbtCompound = chunk.blockEntityNbts.get(blockPos);
                    }
                    if (nbtCompound != null) {
                        nbtList2.add(nbtCompound);
                    }
                }
                else {
                    System.out.println("something wrong!");
                }

            }

            returnValue.put("block_entities", nbtList2);
        }

        return returnValue;
    }*/

    @Shadow @Final private Long2ObjectLinkedOpenHashMap<ChunkHolder> chunksToUnload;
    @Shadow @Final private LongSet loadedChunks;
    @Shadow @Final private ServerLightingProvider lightingProvider;
    @Shadow @Final private WorldGenerationProgressListener worldGenerationProgressListener;
    @Shadow @Final private Long2LongMap chunkToNextSaveTimeMs;
    @Shadow @Final private Queue<Runnable> unloadTaskQueue;
    @Shadow @Final private static Logger LOGGER;

    @Redirect(at=@At(value="INVOKE",target="Lnet/minecraft/server/world/ThreadedAnvilChunkStorage;tryUnloadChunk(JLnet/minecraft/server/world/ChunkHolder;)V"),method="unloadChunks")
    private void test(ThreadedAnvilChunkStorage instance, long pos, ChunkHolder holder) {

        CompletableFuture<Chunk> completableFuture = holder.getSavingFuture();
        Consumer<Chunk> var10001 = (chunk) -> {
            CompletableFuture<Chunk> completableFuture2 = holder.getSavingFuture();
            if (completableFuture2 != completableFuture) {
                this.tryUnloadChunk(pos, holder);
            } else {
                if (this.chunksToUnload.remove(pos, holder) && chunk != null) {
                    if (chunk instanceof WorldChunk) {
                        ((WorldChunk)chunk).setLoadedToWorld(false);
                    }
                    saveDestroyShadows(chunk);
                    if (this.loadedChunks.remove(pos) && chunk instanceof WorldChunk) {
                        WorldChunk worldChunk = (WorldChunk)chunk;
                        this.world.unloadEntities(worldChunk);
                    }

                    this.lightingProvider.updateChunkStatus(chunk.getPos());
                    this.lightingProvider.tick();
                    this.worldGenerationProgressListener.setChunkStatus(chunk.getPos(), null);
                    this.chunkToNextSaveTimeMs.remove(chunk.getPos().toLong());
                }

            }
        };
        Queue var10002 = this.unloadTaskQueue;
        Objects.requireNonNull(var10002);
        completableFuture.thenAcceptAsync(var10001, var10002::add).whenComplete((void_, throwable) -> {
            if (throwable != null) {
                LOGGER.error("Failed to save chunk {}", holder.getPos(), throwable);
            }

        });
    }

    @Shadow private @Final PointOfInterestStorage pointOfInterestStorage;
    @Shadow private boolean isLevelChunk(ChunkPos pos) {return false;}
    @Shadow private byte mark(ChunkPos pos, ChunkStatus.ChunkType type) {return 0;}


    private boolean saveDestroyShadows(Chunk chunk) {
        this.pointOfInterestStorage.saveChunk(chunk.getPos());
        //if (!chunk.needsSaving()) {
            //return false;
        //} else {
            chunk.setNeedsSaving(false);
            ChunkPos chunkPos = chunk.getPos();

            try {
                ChunkStatus chunkStatus = chunk.getStatus();
                if (chunkStatus.getChunkType() != ChunkStatus.ChunkType.LEVELCHUNK) {
                    if (this.isLevelChunk(chunkPos)) {
                        return false;
                    }

                    if (chunkStatus == ChunkStatus.EMPTY && chunk.getStructureStarts().values().stream().noneMatch(StructureStart::hasChildren)) {
                        return false;
                    }
                }

                this.world.getProfiler().visit("chunkSave");
                NbtCompound nbtCompound = ChunkSerializer.serialize(this.world, chunk);

                NbtList nbtList2 = new NbtList();

                for (BlockPos blockPos : chunk.getBlockEntityPositions()) {

                    if (chunk instanceof WorldChunk) {
                        BlockEntity blockEntity = chunk.getBlockEntity(blockPos);
                        NbtCompound nbtCompound2;
                        if (blockEntity != null && !blockEntity.isRemoved()) {
                            nbtCompound2 = ((BlockEntityAccessor)blockEntity).createNbtWithIdentifyingDataDestroyShadows();
                            nbtCompound2.putBoolean("keepPacked", false);
                        } else {
                            nbtCompound2 = chunk.blockEntityNbts.get(blockPos);
                            if (nbtCompound2 != null) {
                                nbtCompound2 = nbtCompound2.copy();
                                nbtCompound2.putBoolean("keepPacked", true);
                            }
                        }
                        if (nbtCompound2 != null) {
                            nbtList2.add(nbtCompound2);
                        }
                    }
                    else if (chunk instanceof ProtoChunk) {

                        BlockEntity blockEntity = chunk.getBlockEntity(blockPos);
                        NbtCompound nbtCompound2;
                        if (blockEntity != null) {
                            System.out.println(blockEntity);
                            nbtCompound2 = ((BlockEntityAccessor)blockEntity).createNbtWithIdentifyingDataDestroyShadows();
                        } else {
                            nbtCompound2 = chunk.blockEntityNbts.get(blockPos);
                        }
                        if (nbtCompound2 != null) {
                            nbtList2.add(nbtCompound2);
                        }
                    }

                }

                nbtCompound.put("block_entities", nbtList2);

                TACS_instance.setNbt(chunkPos, nbtCompound);
                mark(chunkPos, chunkStatus.getChunkType());
                return true;
            } catch (Exception var5) {
                LOGGER.error("Failed to save chunk {},{}", new Object[]{chunkPos.x, chunkPos.z, var5});
                return false;
            }
        //}
    }

}
