package com.terriblefriends.serverutilities.mixin.shadowfix.entity;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.server.world.ServerEntityManager;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.entity.EntityChangeListener;
import net.minecraft.world.entity.EntityLike;
import net.minecraft.world.entity.SectionedEntityCache;
import net.minecraft.world.storage.ChunkDataAccess;
import net.minecraft.world.storage.ChunkDataList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Mixin(ServerEntityManager.class)
public class ServerEntityManagerMixin<T extends EntityLike> {
    @Shadow @Final private Long2ObjectMap<ServerEntityManager.Status> managedStatuses;
    @Shadow @Final SectionedEntityCache<T> cache;
    @Shadow @Final private ChunkDataAccess<T> dataAccess;
    @Shadow @Final private void scheduleRead(long chunkPos) {}

    @Inject(at=@At("HEAD"),method="Lnet/minecraft/server/world/ServerEntityManager;trySave(JLjava/util/function/Consumer;)Z",cancellable = true)
    private void trySaveDestroyShadows(long chunkPos, Consumer<T> action, CallbackInfoReturnable<Boolean> cir) {
        ServerEntityManager.Status status = this.managedStatuses.get(chunkPos);
        if (status == ServerEntityManager.Status.PENDING) {
            cir.setReturnValue(false);
        } else {
            List<T> list = this.cache.getTrackingSections(chunkPos).flatMap((section) -> {
                return section.stream().filter(EntityLike::shouldSave);
            }).collect(Collectors.toList());
            if (list.isEmpty()) {
                if (status == ServerEntityManager.Status.LOADED) {
                    this.dataAccess.writeChunkData(new ChunkDataList(new ChunkPos(chunkPos), ImmutableList.of()));
                }

                cir.setReturnValue(true);
            } else if (status == ServerEntityManager.Status.FRESH) {
                this.scheduleRead(chunkPos);
                cir.setReturnValue(false);
            } else {
                list.forEach(action);
                this.dataAccess.writeChunkData(new ChunkDataList(new ChunkPos(chunkPos), list));
                cir.setReturnValue(true);
            }
        }
        if (cir.isCancellable()) {cir.cancel();}
    }
}
