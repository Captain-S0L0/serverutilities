package com.terriblefriends.serverutilities.mixin.command;

import com.google.common.collect.Lists;
import com.terriblefriends.serverutilities.access.ServerPlayerEntityAccess;
import com.terriblefriends.serverutilities.command.SetScoreboardCommand;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.ScoreboardDisplayS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardObjectiveUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardPlayerUpdateS2CPacket;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ServerScoreboard.class)
public class ServerScoreboardMixin extends Scoreboard {
    ServerScoreboard SS_instance = (ServerScoreboard) (Object) this;
    @Final @Shadow private MinecraftServer server;
    @Final @Shadow private java.util.Set<net.minecraft.scoreboard.ScoreboardObjective> objectives;

    @Shadow protected void runUpdateListeners() {}

    @Inject(at=@At("HEAD"),method="Lnet/minecraft/scoreboard/ServerScoreboard;addScoreboardObjective(Lnet/minecraft/scoreboard/ScoreboardObjective;)V",cancellable = true)
    private void addScoreboardObjective(ScoreboardObjective objective, CallbackInfo ci) {
        List<Packet<?>> list = Lists.newArrayList();
        list.add(new ScoreboardObjectiveUpdateS2CPacket(objective, 0));

        for (ServerPlayerEntity serverPlayerEntity : server.getPlayerManager().getPlayerList()) {
            for (int i = 0; i < 19; ++i) {
                ScoreboardObjective objective1;
                if (i == 1) {
                    objective1 = SS_instance.getObjective(((ServerPlayerEntityAccess)serverPlayerEntity).getUniqueScoreboardName());
                }
                else {
                    objective1 = SS_instance.getObjectiveForSlot(i);
                }
                if (objective1 == objective) {
                    list.add(new ScoreboardDisplayS2CPacket(i, objective));
                }
            }

            for (ScoreboardPlayerScore scoreboardPlayerScore : SS_instance.getAllPlayerScores(objective)) {
                list.add(new ScoreboardPlayerUpdateS2CPacket(ServerScoreboard.UpdateMode.CHANGE, scoreboardPlayerScore.getObjective().getName(), scoreboardPlayerScore.getPlayerName(), scoreboardPlayerScore.getScore()));
            }

            for (Packet<?> packet : list) {
                serverPlayerEntity.networkHandler.sendPacket(packet);
            }
        }

        this.objectives.add(objective);
        ci.cancel();
    }

    @Inject(at=@At("HEAD"),method="Lnet/minecraft/scoreboard/ServerScoreboard;removeScoreboardObjective(Lnet/minecraft/scoreboard/ScoreboardObjective;)V",cancellable = true)
    private void removeScoreboardObjective(ScoreboardObjective objective, CallbackInfo ci) {
        List<Packet<?>> list = Lists.newArrayList();
        list.add(new ScoreboardObjectiveUpdateS2CPacket(objective, 1));

        for (ServerPlayerEntity serverPlayerEntity : server.getPlayerManager().getPlayerList()) {
            for (int i = 0; i < 19; ++i) {
                ScoreboardObjective objective1;
                if (i == 1) {
                    objective1 = SS_instance.getObjective(((ServerPlayerEntityAccess)serverPlayerEntity).getUniqueScoreboardName());
                }
                else {
                    objective1 = SS_instance.getObjectiveForSlot(i);
                }
                if (objective1 == objective) {
                    list.add(new ScoreboardDisplayS2CPacket(i, objective));
                }
            }

            for (Packet<?> packet : list) {
                serverPlayerEntity.networkHandler.sendPacket(packet);
            }
        }

        this.objectives.remove(objective);
        ci.cancel();
    }

    //@Inject(at=@At("HEAD"),method="",cancellable = true)


    @Inject(at=@At("HEAD"),method="Lnet/minecraft/scoreboard/ServerScoreboard;updateScore(Lnet/minecraft/scoreboard/ScoreboardPlayerScore;)V",cancellable = true)
    private void updateScore(ScoreboardPlayerScore score, CallbackInfo ci) {
        super.updateScore(score);
        if (this.objectives.contains(score.getObjective()) || SetScoreboardCommand.objectives.contains(score.getObjective())) {
            this.server.getPlayerManager().sendToAll(new ScoreboardPlayerUpdateS2CPacket(ServerScoreboard.UpdateMode.CHANGE, score.getObjective().getName(), score.getPlayerName(), score.getScore()));
        }

        runUpdateListeners();
        ci.cancel();
    }

    @Inject(at=@At("HEAD"),method="Lnet/minecraft/scoreboard/ServerScoreboard;updatePlayerScore(Ljava/lang/String;Lnet/minecraft/scoreboard/ScoreboardObjective;)V",cancellable = true)
    private void updatePlayerScore(String playerName, ScoreboardObjective objective, CallbackInfo ci) {
        super.updatePlayerScore(playerName, objective);
        if (this.objectives.contains(objective) || SetScoreboardCommand.objectives.contains(objective)) {
            this.server.getPlayerManager().sendToAll(new ScoreboardPlayerUpdateS2CPacket(ServerScoreboard.UpdateMode.REMOVE, objective.getName(), playerName, 0));
        }

        runUpdateListeners();
        ci.cancel();
    }

    @Inject(at=@At("HEAD"),method="Lnet/minecraft/scoreboard/ServerScoreboard;updateExistingObjective(Lnet/minecraft/scoreboard/ScoreboardObjective;)V",cancellable = true)
    private void updateExistingObjective(ScoreboardObjective objective, CallbackInfo ci) {
        super.updateExistingObjective(objective);
        if (this.objectives.contains(objective) || SetScoreboardCommand.objectives.contains(objective)) {
            this.server.getPlayerManager().sendToAll(new ScoreboardObjectiveUpdateS2CPacket(objective, 2));
        }

        this.runUpdateListeners();
        ci.cancel();
    }

    @Inject(at=@At("HEAD"),method="Lnet/minecraft/scoreboard/ServerScoreboard;updateRemovedObjective(Lnet/minecraft/scoreboard/ScoreboardObjective;)V",cancellable = true)
    private void updateRemovedObjective(ScoreboardObjective objective, CallbackInfo ci) {
        super.updateRemovedObjective(objective);
        if (this.objectives.contains(objective) || SetScoreboardCommand.objectives.contains(objective)) {
            SS_instance.removeScoreboardObjective(objective);
        }

        this.runUpdateListeners();
        ci.cancel();
    }
}
