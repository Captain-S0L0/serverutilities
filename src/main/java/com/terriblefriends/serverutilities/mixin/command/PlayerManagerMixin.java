package com.terriblefriends.serverutilities.mixin.command;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import com.terriblefriends.serverutilities.access.PlayerManagerAccess;
import com.terriblefriends.serverutilities.access.ServerPlayerEntityAccess;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.ScoreboardDisplayS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardObjectiveUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardPlayerUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.TeamS2CPacket;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.OperatorEntry;
import net.minecraft.server.OperatorList;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin implements PlayerManagerAccess {
    @Shadow OperatorList ops;
    @Shadow abstract ServerPlayerEntity getPlayer(UUID uuid);
    @Shadow abstract void sendCommandTree(ServerPlayerEntity player);

    public void addToOperatorsWithPower(GameProfile profile, int power) { //allows /opwp to work
        this.ops.add(new OperatorEntry(profile, power, false));
        ServerPlayerEntity serverPlayerEntity = this.getPlayer(profile.getId());
        if (serverPlayerEntity != null) {
            this.sendCommandTree(serverPlayerEntity);
        }
    }

    @Inject(at=@At("HEAD"),method="Lnet/minecraft/server/PlayerManager;sendScoreboard(Lnet/minecraft/scoreboard/ServerScoreboard;Lnet/minecraft/server/network/ServerPlayerEntity;)V",cancellable = true)
    private void sendUniqueScoreboard(ServerScoreboard scoreboard, ServerPlayerEntity player, CallbackInfo ci) {
        Set<ScoreboardObjective> set = Sets.newHashSet();

        for (Team team : scoreboard.getTeams()) {
            player.networkHandler.sendPacket(TeamS2CPacket.updateTeam(team, true));
        }

        for(int i = 0; i < 19; ++i) {
            ScoreboardObjective scoreboardObjective;
            if (i == 1) {
                scoreboardObjective = scoreboard.getObjective(((ServerPlayerEntityAccess)player).getUniqueScoreboardName());
            }
            else {
                scoreboardObjective = scoreboard.getObjectiveForSlot(i);
            }
            if (scoreboardObjective != null && !set.contains(scoreboardObjective)) {
                System.out.println(i);
                //List<Packet<?>> list = scoreboard.createChangePackets(scoreboardObjective);

                List<Packet<?>> list = Lists.newArrayList();
                list.add(new ScoreboardObjectiveUpdateS2CPacket(scoreboardObjective, 0));

                list.add(new ScoreboardDisplayS2CPacket(i, scoreboardObjective));

                for (ScoreboardPlayerScore scoreboardPlayerScore : scoreboard.getAllPlayerScores(scoreboardObjective)) {
                    list.add(new ScoreboardPlayerUpdateS2CPacket(ServerScoreboard.UpdateMode.CHANGE, scoreboardPlayerScore.getObjective().getName(), scoreboardPlayerScore.getPlayerName(), scoreboardPlayerScore.getScore()));
                }

                for (Packet<?> value : list) {
                    player.networkHandler.sendPacket(value);
                }

                set.add(scoreboardObjective);
            }
        }
        ci.cancel();
    }
}
