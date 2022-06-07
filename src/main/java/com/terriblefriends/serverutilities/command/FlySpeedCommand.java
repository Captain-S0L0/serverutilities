package com.terriblefriends.serverutilities.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.terriblefriends.serverutilities.access.SetPlayerAbilitiesAccess;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Collection;

import static net.minecraft.command.argument.EntityArgumentType.getPlayers;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;


public class FlySpeedCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(literal("flyspeed")
                .requires(source -> source.hasPermissionLevel(2))
                .then(argument("speed", FloatArgumentType.floatArg(0.0f))
                    .then(argument("targets", EntityArgumentType.players())
                        .executes(ctx -> flyspeed(ctx.getSource(), FloatArgumentType.getFloat(ctx,"speed"), getPlayers(ctx,"targets"))))
                    .executes(ctx -> flyspeed(ctx.getSource(), FloatArgumentType.getFloat(ctx,"speed"),null))));
    }

    private static int flyspeed(ServerCommandSource source, float flo, Collection<ServerPlayerEntity> targets) throws CommandSyntaxException {

        if(targets == null) {
            handlePlayers(source.getPlayer(), source, flo);
        } else {
            targets.forEach(target -> handlePlayers(target, source, flo));
        }

        return 1;
    }

    private static void handlePlayers(ServerPlayerEntity player, ServerCommandSource source, float flo) {
        final Text speed = Text.literal("Set "+player.getEntityName()+"'s fly speed");

        ((SetPlayerAbilitiesAccess)player.getAbilities()).setFlySpeed(flo);
        source.sendFeedback(speed, true);

        player.sendAbilitiesUpdate();
    }
}