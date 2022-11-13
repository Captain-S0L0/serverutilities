package com.terriblefriends.serverutilities.command;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.terriblefriends.serverutilities.access.ServerPlayerEntityAccess;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.ScoreboardObjectiveArgumentType;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.ScoreboardDisplayS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardObjectiveUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardPlayerUpdateS2CPacket;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Set;

import static net.minecraft.server.command.CommandManager.literal;

public class SetScoreboardCommand {
    public static Set<ScoreboardObjective> objectives = Sets.newHashSet();

    private static final SimpleCommandExceptionType OBJECTIVES_DISPLAY_ALREADY_SET_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.scoreboard.objectives.display.alreadySet"));
    private static final SimpleCommandExceptionType OBJECTIVES_DISPLAY_ALREADY_EMPTY_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.scoreboard.objectives.display.alreadyEmpty"));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(literal("setscoreboard")
                .then(CommandManager.argument("objective", ScoreboardObjectiveArgumentType.scoreboardObjective()).executes(ctx -> executeSetDisplay(ctx.getSource(), ScoreboardObjectiveArgumentType.getObjective(ctx, "objective"))))
                .then(CommandManager.literal("blank").executes(ctx -> executeSetDisplay(ctx.getSource(),null)))
        );
    }

    private static int executeSetDisplay(ServerCommandSource source, ScoreboardObjective newObjective) throws CommandSyntaxException {
        if (!source.isExecutedByPlayer()) {
            throw new SimpleCommandExceptionType(Text.literal("Non-players cannot set their scoreboard!")).create();
        }
        String oldName = ((ServerPlayerEntityAccess)source.getPlayer()).getUniqueScoreboardName();
        ScoreboardObjective oldObjective = null;
        if (oldName != null) {
            oldObjective = source.getServer().getScoreboard().getObjective(oldName);
            source.getPlayer().networkHandler.sendPacket(new ScoreboardObjectiveUpdateS2CPacket(oldObjective, 1));
        }

        if (newObjective == null) {
            if (oldObjective != null) {
                boolean removeFromUpdateList = true;

                ((ServerPlayerEntityAccess)source.getPlayer()).setUniqueScoreboardName(null);

                for (ServerPlayerEntity player : source.getServer().getPlayerManager().getPlayerList()) {
                    String nameToCheck = ((ServerPlayerEntityAccess)player).getUniqueScoreboardName();
                    if (nameToCheck != null && nameToCheck.equals(oldName)) {
                        System.out.println(player.getEntityName()+" has old scoreboard");
                        removeFromUpdateList = false;
                    }
                }

                if (removeFromUpdateList) {
                    System.out.println("removing "+oldName);
                    objectives.remove(oldObjective);
                }

                source.sendFeedback(Text.translatable("commands.scoreboard.objectives.display.cleared", Scoreboard.getDisplaySlotNames()[1]), false);
                return 0;
            }
            else {
                throw OBJECTIVES_DISPLAY_ALREADY_EMPTY_EXCEPTION.create();
            }
        }



        String newName = newObjective.getName();

        if (oldName != null && oldName.equals(newName)) {
            throw OBJECTIVES_DISPLAY_ALREADY_SET_EXCEPTION.create();
        } else {
            ((ServerPlayerEntityAccess)source.getPlayer()).setUniqueScoreboardName(newName);

            List<Packet<?>> list = Lists.newArrayList();
            list.add(new ScoreboardObjectiveUpdateS2CPacket(newObjective, 0));
            list.add(new ScoreboardDisplayS2CPacket(1, newObjective));
            for (ScoreboardPlayerScore scoreboardPlayerScore : source.getServer().getScoreboard().getAllPlayerScores(newObjective)) {
                list.add(new ScoreboardPlayerUpdateS2CPacket(ServerScoreboard.UpdateMode.CHANGE, newName, scoreboardPlayerScore.getPlayerName(), scoreboardPlayerScore.getScore()));
            }

            for (Packet<?> packet : list) {
                source.getPlayer().networkHandler.sendPacket(packet);
            }

            boolean removeFromUpdateList = true;

            for (ServerPlayerEntity player : source.getServer().getPlayerManager().getPlayerList()) {
                String nameToCheck = ((ServerPlayerEntityAccess)player).getUniqueScoreboardName();
                if (nameToCheck != null && nameToCheck.equals(oldName)) {
                    System.out.println(player.getEntityName()+" has old scoreboard");
                    removeFromUpdateList = false;
                }
            }
            if (removeFromUpdateList) {
                System.out.println("removing "+oldName);
                objectives.remove(oldObjective);
            }
            objectives.add(newObjective);

            source.sendFeedback(Text.translatable("commands.scoreboard.objectives.display.set", Scoreboard.getDisplaySlotNames()[1], newName), false);
            return 0;
        }
    }
}
