package com.terriblefriends.serverutilities.command;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.server.BannedPlayerEntry;
import net.minecraft.server.BannedPlayerList;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import static net.minecraft.server.command.CommandManager.literal;

public class TempBanCommand {

    private static final SimpleCommandExceptionType ALREADY_BANNED_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.ban.failed"));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("tempban").requires((source) -> source.hasPermissionLevel(3))
        .then(CommandManager.argument("targets", GameProfileArgumentType.gameProfile()).then(CommandManager.argument("time", IntegerArgumentType.integer(1)).executes((context) -> {
            return ban(context.getSource(), GameProfileArgumentType.getProfileArgument(context, "targets"), IntegerArgumentType.getInteger(context, "time"), null);
        }).then(CommandManager.argument("reason", MessageArgumentType.message()).executes((context) -> {
            return ban(context.getSource(), GameProfileArgumentType.getProfileArgument(context, "targets"), IntegerArgumentType.getInteger(context, "time"), MessageArgumentType.getMessage(context, "reason"));
        }))))
        );
    }

    private static int ban(ServerCommandSource source, Collection<GameProfile> targets, int days, @Nullable Text reason) throws CommandSyntaxException {
        BannedPlayerList bannedPlayerList = source.getServer().getPlayerManager().getUserBanList();
        int i = 0;
        Iterator var5 = targets.iterator();

        while(var5.hasNext()) {
            GameProfile gameProfile = (GameProfile)var5.next();
            if (!bannedPlayerList.contains(gameProfile)) {
                BannedPlayerEntry bannedPlayerEntry = new BannedPlayerEntry(gameProfile, (Date)null, source.getName(), new Date(System.currentTimeMillis()+(86400000L * days)), reason == null ? null : reason.getString());
                bannedPlayerList.add(bannedPlayerEntry);
                ++i;
                source.sendFeedback(Text.translatable("commands.ban.success", new Object[]{Texts.toText(gameProfile), bannedPlayerEntry.getReason()}), true);
                ServerPlayerEntity serverPlayerEntity = source.getServer().getPlayerManager().getPlayer(gameProfile.getId());
                if (serverPlayerEntity != null) {
                    serverPlayerEntity.networkHandler.disconnect(Text.translatable("multiplayer.disconnect.banned"));
                }
            }
        }

        if (i == 0) {
            throw ALREADY_BANNED_EXCEPTION.create();
        } else {
            return i;
        }
    }
}
