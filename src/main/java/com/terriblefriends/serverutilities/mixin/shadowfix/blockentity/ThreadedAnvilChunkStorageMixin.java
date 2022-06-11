package com.terriblefriends.serverutilities.mixin.shadowfix.blockentity;

import com.terriblefriends.serverutilities.access.BlockEntityAccessor;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkSerializer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ThreadedAnvilChunkStorage.class)
public class ThreadedAnvilChunkStorageMixin {
    @Shadow @Final ServerWorld world;


    @Redirect(at=@At(value="INVOKE",target="Lnet/minecraft/world/ChunkSerializer;serialize(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/world/chunk/Chunk;)Lnet/minecraft/nbt/NbtCompound;"),method="save(Lnet/minecraft/world/chunk/Chunk;)Z")
    private NbtCompound serializeDestroyShadows(ServerWorld world, Chunk chunk) {
        NbtCompound returnValue = ChunkSerializer.serialize(world,chunk);

        if (!this.world.getChunkManager().isChunkLoaded(chunk.getPos().x, chunk.getPos().z)) {
            NbtList nbtList2 = new NbtList();

            for (BlockPos blockPos : chunk.getBlockEntityPositions()) {
                //nbtCompound3 = chunk.getPackedBlockEntityNbt(blockPos);

                if (chunk instanceof WorldChunk) {
                    BlockEntity blockEntity = chunk.getBlockEntity(blockPos);
                    NbtCompound nbtCompound;
                    if (blockEntity != null && !blockEntity.isRemoved()) {
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
                if (chunk instanceof ProtoChunk) {
                    BlockEntity blockEntity = chunk.getBlockEntity(blockPos);
                    NbtCompound nbtCompound;
                    nbtCompound =  blockEntity != null ?
                            blockEntity.createNbtWithIdentifyingData()



                            : chunk.blockEntityNbts.get(blockPos);
                    if (nbtCompound != null) {
                        nbtList2.add(nbtCompound);
                    }
                }

            }

            returnValue.put("block_entities", nbtList2);
        }

        return returnValue;
    }
}
