package com.terriblefriends.serverutilities.command;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.terriblefriends.serverutilities.ServerUtilities;
import com.terriblefriends.serverutilities.config.Config;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.network.message.MessageType;
import net.minecraft.server.BannedPlayerEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.OperatorEntry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.*;

import static net.minecraft.command.argument.EntityArgumentType.getPlayer;
import static net.minecraft.command.argument.MessageArgumentType.getMessage;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class VoteBanCommand {
    static int timer = -1;
    static UUID currentBanTargetUuid = null;
    static List<UUID> votes = new ArrayList();
    static List<String> addresses = new ArrayList();
    static List<VoteBanInstance> voteBanInstances = new ArrayList();
    private static final MutableText SystemName = Text.literal("[VoteBan]").formatted(Formatting.DARK_RED);
    private static final String BAN_REASON = "You have been temp banned by the vote system! If this is in error, please contact "+ Config.get().ownerDiscord+" on the discord at \""+Config.get().discordUrl+"\".";

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(literal("voteban")
                .then(literal("vote").then(argument("player", EntityArgumentType.player()).executes(ctx -> handleVote(ctx.getSource(), getPlayer(ctx, "player"), null))
                        .then(argument("reason", MessageArgumentType.message()).executes(ctx -> handleVote(ctx.getSource(), getPlayer(ctx, "player"), getMessage(ctx,"reason"))))))
                .then(literal("confirm").executes(ctx -> handleConfirm(ctx.getSource())))
                .executes(ctx -> helpMenu(ctx.getSource()))
        );
    }

    private static int helpMenu(ServerCommandSource source) {
        source.sendFeedback(SystemName,false);
        source.sendFeedback(Text.literal("The VoteBan system is a tool to remove players that are breaking the rules until an Administrator can intervene.").formatted(Formatting.YELLOW),false);
        source.sendFeedback(Text.literal("This system is NOT to be abused. Any abuse of this system will be met with a permanent ban.").formatted(Formatting.RED),false);
        source.sendFeedback(Text.literal("To use the system, use \"/voteban vote <playername> <reason>\" to begin the voting process.").formatted(Formatting.YELLOW),false);
        source.sendFeedback(Text.literal("A minimum of "+source.getServer().getOverworld().getGameRules().getInt(ServerUtilities.VOTEBAN_MINIMUM)+" votes are required, and alt accounts cannot count as extra votes. The votes necessary increase with the playercount.").formatted(Formatting.YELLOW),false);
    return 1;
    }

    private static int handleConfirm(ServerCommandSource source) {
        UUID votingPlayerUuid;
        if (source.getPlayer() != null) {
            votingPlayerUuid = source.getPlayer().getUuid();
        }
        else {
            source.sendFeedback(SystemName.copy().append(Text.literal(" Error! You are not a player! Either you are a mod, or you are the console, in which case just use /ban anyways, shitass!").formatted(Formatting.RED)), false);
            return 0;
        }
        for (VoteBanInstance VBI : voteBanInstances) {
            if (VBI.creator == votingPlayerUuid) {
                timer = 20 * 30;
                currentBanTargetUuid = VBI.target;
                ServerPlayerEntity votedOut = source.getServer().getPlayerManager().getPlayer(currentBanTargetUuid);
                source.getServer().getPlayerManager().broadcast(SystemName, MessageType.SYSTEM);
                source.getServer().getPlayerManager().broadcast(Text.literal(source.getName()).formatted(Formatting.RED).append(Text.literal(" has initiated a vote to temp ban ").formatted(Formatting.YELLOW)).append(Text.literal(votedOut.getEntityName()).formatted(Formatting.DARK_RED)), MessageType.SYSTEM);
                source.getServer().getPlayerManager().broadcast(Text.literal("The reason provided is: ").formatted(Formatting.DARK_RED).append(VBI.reason.copy().formatted(Formatting.GOLD)), MessageType.SYSTEM);
                source.getServer().getPlayerManager().broadcast(Text.literal("This system is NOT to be abused. Any abuse of this system will be met with a permanent ban. For more information, see \"/voteban\".").formatted(Formatting.RED), MessageType.SYSTEM);
                source.getServer().getPlayerManager().broadcast(Text.literal("To vote, use \"/voteban vote "+votedOut.getEntityName()+"\". This vote will last for ~30 seconds.").formatted(Formatting.YELLOW), MessageType.SYSTEM);
                votes.add(votingPlayerUuid);
                addresses.add(source.getPlayer().getIp());
                voteBanInstances.remove(VBI);
                int votesRequired = getVotesNeeded(source.getServer());
                source.getServer().getPlayerManager().broadcast(Text.literal(""), MessageType.SYSTEM);
                source.getServer().getPlayerManager().broadcast(SystemName.copy().append(Text.literal(" (" + votes.size() + "/" + votesRequired + ") votes received.").formatted(Formatting.GOLD)), MessageType.SYSTEM);
                return 1;
            }
        }
        source.sendFeedback(SystemName.copy().append(Text.literal(" Error! The vote is already in progress!").formatted(Formatting.RED)), false);
        return 0;
    }

    private static int handleVote(ServerCommandSource source, ServerPlayerEntity votedOut, Text reason) throws CommandSyntaxException {
        UUID votingPlayerUuid;
        if (source.getPlayer() != null) {
            votingPlayerUuid = source.getPlayer().getUuid();
        }
        else {
            source.sendFeedback(SystemName.copy().append(Text.literal(" Error! You are not a player! Either you are a mod, or you are the console, in which case just use /ban anyways, shitass!").formatted(Formatting.RED)), false);
            return 0;
        }
        if (votingPlayerUuid == currentBanTargetUuid) {
            source.sendFeedback(SystemName.copy().append(Text.literal(" Error! You are the player being voted out!").formatted(Formatting.RED)), false);
            return 0;
        }
        if (votingPlayerUuid == votedOut.getUuid()) {
            source.sendFeedback(SystemName.copy().append(Text.literal(" Error! You can't vote for yourself!").formatted(Formatting.RED)), false);
            return 0;
        }
        /*for (String operator: source.getServer().getPlayerManager().getOpNames()) {
            if (operator.equals(votedOut.getEntityName())) {
                source.sendFeedback(SystemName.copy().append(Text.literal(" Error! You can't ban an operator!").formatted(Formatting.RED)), false);
                return 0;
            }
        }*/
        OperatorEntry opProfile = source.getServer().getPlayerManager().getOpList().get(votedOut.getGameProfile());
        if (opProfile != null && opProfile.getPermissionLevel() > 0) {
            source.sendFeedback(SystemName.copy().append(Text.literal(" Error! You can't ban an operator!").formatted(Formatting.RED)), false);
            return 0;
        }
        if (currentBanTargetUuid == null) {
            for (VoteBanInstance VBI : voteBanInstances) {
                if (VBI.creator == votingPlayerUuid) {
                    source.sendFeedback(SystemName.copy().append(Text.literal(" Error! You must run \"/voteban confirm\" to confirm the initiation of the vote!").formatted(Formatting.RED)), false);
                    return 0;
                }
            }

            if (reason == null) {
                source.sendFeedback(SystemName.copy().append(Text.literal(" Error! You must provide a reason!").formatted(Formatting.RED)), false);
                return 0;
            }

            voteBanInstances.add(new VoteBanInstance(votingPlayerUuid, votedOut.getUuid(),reason));
            source.sendFeedback(SystemName, false);
            source.sendFeedback(Text.literal("The VoteBan system is a tool to remove players that are breaking the rules until an Administrator can intervene.").formatted(Formatting.YELLOW), false);
            source.sendFeedback(Text.literal("The reason provided is: ").formatted(Formatting.DARK_RED).append(reason.copy().formatted(Formatting.GOLD)),false);
            source.sendFeedback(Text.literal("This system is NOT to be abused. Any abuse of this system will be met with a permanent ban.").formatted(Formatting.RED), false);
            source.sendFeedback(Text.literal("You have 30 seconds to run \"/voteban confirm\" to confirm the initiation of the vote. More information in \"/voteban\".").formatted(Formatting.YELLOW), false);
            return 1;
        }
        if (votedOut.getUuid() != currentBanTargetUuid) {
            source.sendFeedback(SystemName.copy().append(Text.literal(" Error! Vote in progress! Please wait "+timer/20+" seconds!").formatted(Formatting.RED)), false);
            return 0;
        }
        if (!votes.contains(votingPlayerUuid)) {
            if (addresses.contains(source.getPlayer().getIp())) {
                source.sendFeedback(SystemName.copy().append(Text.literal(" Error! You have already voted!").formatted(Formatting.RED)), false);
                return 0;
            }
            addresses.add(source.getPlayer().getIp());
            votes.add(votingPlayerUuid);
            int votesRequired = getVotesNeeded(source.getServer());
            source.getServer().getPlayerManager().broadcast(SystemName.copy().append(Text.literal(" "+source.getPlayer().getEntityName()+" voted.").formatted(Formatting.RED)),MessageType.SYSTEM);
            source.getServer().getPlayerManager().broadcast(SystemName.copy().append(Text.literal(" ("+votes.size()+"/"+votesRequired+") votes received.").formatted(Formatting.GOLD)),MessageType.SYSTEM);

            Date expiry = new Date(System.currentTimeMillis()+(source.getServer().getOverworld().getGameRules().get(ServerUtilities.VOTEBAN_DURATION).get()* 86400000L));

            if (votes.size() >= votesRequired) {
                BannedPlayerEntry newBan = new BannedPlayerEntry(new GameProfile(currentBanTargetUuid, null),new Date(System.currentTimeMillis()), "VoteBan System", expiry,BAN_REASON);
                source.getServer().getPlayerManager().getUserBanList().add(newBan);
                ServerPlayerEntity serverPlayerEntity = source.getServer().getPlayerManager().getPlayer(currentBanTargetUuid);
                if (serverPlayerEntity != null) {
                    serverPlayerEntity.networkHandler.disconnect(Text.literal(BAN_REASON));
                }
                //source.getServer().getPlayerManager().broadcast(Text.literal(""),MessageType.SYSTEM);
                currentBanTargetUuid = null;
                votes = new ArrayList();
                timer = -1;
                source.getServer().getPlayerManager().broadcast(SystemName.copy().append(Text.literal(" The voteban has passed successfully.").formatted(Formatting.RED)),MessageType.SYSTEM);
            }
            return 1;
        }
        else {
            source.sendFeedback(SystemName.copy().append(Text.literal(" Error! You have already voted!").formatted(Formatting.RED)), false);
            return 0;
        }
    }

    public static void tickBanVoter() {
        if (timer > 0) {timer--;}
        if (timer == 0) {
            ServerUtilities.server.getPlayerManager().broadcast(SystemName.copy().append(Text.literal(" The current vote has expired.").formatted(Formatting.RED)),MessageType.SYSTEM);
            currentBanTargetUuid = null;
            votes = new ArrayList();
            timer = -1;
        }

        List<VoteBanInstance> toRemove = new ArrayList();
        for (VoteBanInstance VBI : voteBanInstances) {
            VBI.timer--;
            if (VBI.timer <= 0) {
                ServerUtilities.server.getPlayerManager().getPlayer(VBI.creator).sendMessage(SystemName.copy().append(Text.literal(" Your vote has expired.").formatted(Formatting.RED)),MessageType.SYSTEM);
                toRemove.add(VBI);
            }
        }
        for (VoteBanInstance VBI : toRemove) {
            voteBanInstances.remove(VBI);
        }

    }

    private static int getVotesNeeded(MinecraftServer server) {
        return Math.max(((int)( server.getPlayerManager().getPlayerList().size() * (server.getOverworld().getGameRules().getInt(ServerUtilities.VOTEBAN_PERCENTAGE)*.01) )), server.getOverworld().getGameRules().getInt(ServerUtilities.VOTEBAN_MINIMUM));
    }
}
