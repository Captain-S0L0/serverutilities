package com.terriblefriends.serverutilities.command;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Iterator;
import com.terriblefriends.serverutilities.access.PlayerManagerAccess;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class OperatorCommand {
    private static final SimpleCommandExceptionType ALREADY_OPPED_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.op.failed"));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(literal("opwp")
                .requires((source) -> source.hasPermissionLevel(3))
                .then(argument("targets", GameProfileArgumentType.gameProfile()).suggests((context, builder) -> {
            PlayerManager playerManager = (context.getSource()).getServer().getPlayerManager();
            return CommandSource.suggestMatching(playerManager.getPlayerList().stream().filter((player) -> {
                return !playerManager.isOperator(player.getGameProfile());
            }).map((player) -> {
                return player.getGameProfile().getName();
            }), builder);
        }).then(argument("power", IntegerArgumentType.integer(1,4)).executes((context) -> {
            return op(context.getSource(), GameProfileArgumentType.getProfileArgument(context, "targets"), IntegerArgumentType.getInteger(context, "power"));
        }))));
    }

    private static int op(ServerCommandSource source, Collection<GameProfile> targets, int power) throws CommandSyntaxException {
        PlayerManager playerManager = source.getServer().getPlayerManager();
        int i = 0;
        Iterator var4 = targets.iterator();
        while(var4.hasNext()) {
            GameProfile gameProfile = (GameProfile)var4.next();
            if (!playerManager.isOperator(gameProfile)) {
                ((PlayerManagerAccess)(playerManager)).addToOperatorsWithPower(gameProfile, power);
                ++i;
                source.sendFeedback(Text.literal("Opped "+gameProfile.getName()+" with power "+power), true);
            }
        }

        if (i == 0) {
            throw ALREADY_OPPED_EXCEPTION.create();
        } else {
            return i;
        }
    }
}
