package com.terriblefriends.serverutilities.mixin.shadowfix.player;

import com.terriblefriends.serverutilities.access.WorldSaveHandlerAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.integrated.IntegratedPlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.Stats;
import net.minecraft.world.WorldSaveHandler;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Environment(EnvType.CLIENT)
@Mixin(PlayerManager.class)
public class PlayerManagerMixin_CLIENT {
    @Shadow @Final private WorldSaveHandler saveHandler;
    @Shadow @Final private Map<UUID, ServerStatHandler> statisticsMap;
    @Shadow @Final private Map<UUID, PlayerAdvancementTracker> advancementTrackers;
    @Shadow @Final private Map<UUID, ServerPlayerEntity> playerMap;
    @Shadow @Final private List<ServerPlayerEntity> players;
    @Shadow @Final private MinecraftServer server;
    @Shadow @Final private static Logger LOGGER;

    PlayerManager PM_instance = (PlayerManager) (Object) this;

    @Inject(at=@At("HEAD"),method="Lnet/minecraft/server/PlayerManager;remove(Lnet/minecraft/server/network/ServerPlayerEntity;)V",cancellable = true)
    private void removeDestroyShadows(ServerPlayerEntity player, CallbackInfo ci) {
        player.getWorld().removePlayer(player, Entity.RemovalReason.UNLOADED_WITH_PLAYER);
        player.incrementStat(Stats.LEAVE_GAME);
        if (PM_instance instanceof IntegratedPlayerManager) {
            NbtCompound playerData = player.writeNbt(new NbtCompound());
            ((IntegratedPlayerManager)PM_instance).userData = playerData;
            ((WorldSaveHandlerAccessor)saveHandler).savePlayerDataFromNbt(player, playerData);
        }
        else {
            saveHandler.savePlayerData(player);
        }
        if (player.hasVehicle()) {
            Entity entity = player.getRootVehicle();
            if (entity.hasPlayerRider()) {
                LOGGER.debug("Removing player mount");
                player.stopRiding();
                entity.streamPassengersAndSelf().forEach((entityx) -> {
                    entityx.setRemoved(Entity.RemovalReason.UNLOADED_WITH_PLAYER);
                });
            }
        }
        player.detach();
        player.getAdvancementTracker().clearCriteria();
        player.getAdvancementTracker().save();
        this.players.remove(player);
        this.server.getBossBarManager().onPlayerDisconnect(player);
        UUID uUID = player.getUuid();
        ServerPlayerEntity serverPlayerEntity = this.playerMap.get(uUID);
        if (serverPlayerEntity.getUuid() == player.getUuid()) {
            this.playerMap.remove(uUID);
            this.statisticsMap.remove(uUID);
            this.advancementTrackers.remove(uUID);
        }

        PM_instance.sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Action.REMOVE_PLAYER, new ServerPlayerEntity[]{player}));

        if (ci.isCancellable()) {ci.cancel();}
    }
}
