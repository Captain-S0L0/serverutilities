package com.terriblefriends.serverutilities.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.terriblefriends.serverutilities.access.SetPlayerAbilitiesAccess;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Collection;

import static com.mojang.brigadier.arguments.BoolArgumentType.getBool;
import static net.minecraft.command.argument.EntityArgumentType.getPlayers;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;


public class FlyCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(literal("fly")
                .requires(source -> source.hasPermissionLevel(2))
                .then(argument("boolean", BoolArgumentType.bool()).executes(ctx -> handleSet(ctx.getSource(), null,getBool(ctx,"boolean"))))
                .then(argument("targets", EntityArgumentType.players()).executes(ctx -> handleToggle(ctx.getSource(), getPlayers(ctx, "targets")))
                        .then(argument("boolean", BoolArgumentType.bool()).executes(ctx -> handleSet(ctx.getSource(), getPlayers(ctx, "targets"),getBool(ctx,"boolean"))))
                )
                .executes(ctx -> handleToggle(ctx.getSource(), null))
        );
    }

    private static int handleToggle(ServerCommandSource source, Collection<ServerPlayerEntity> targets) throws CommandSyntaxException {

        if(targets == null) {
            toggleFly(source.getPlayer(), source);
        } else {
            targets.forEach(target -> toggleFly(target, source));
        }

        return 1;
    }

    private static int handleSet(ServerCommandSource source, Collection<ServerPlayerEntity> targets, boolean fly) throws CommandSyntaxException {

        if(targets == null) {
            setFly(source.getPlayer(), source, fly);
        } else {
            targets.forEach(target -> setFly(target, source, fly));
        }

        return 1;
    }

    private static void toggleFly(ServerPlayerEntity player, ServerCommandSource source) {
        final Text on = Text.literal("Set "+player.getEntityName()+" mayFly = true");
        final Text off = Text.literal("Set "+player.getEntityName()+" mayFly = false");

        if (!player.getAbilities().allowFlying) {
            ((SetPlayerAbilitiesAccess)player.getAbilities()).setAllowFlying(true);
            source.sendFeedback(on, true);
        } else {
            ((SetPlayerAbilitiesAccess)player.getAbilities()).setAllowFlying(false);
            ((SetPlayerAbilitiesAccess)player.getAbilities()).setFlying(false);
            source.sendFeedback(off, true);
        }

        player.sendAbilitiesUpdate();
    }

    private static void setFly(ServerPlayerEntity player, ServerCommandSource source, boolean fly) {
        final Text on = Text.literal("Set "+player.getEntityName()+" mayFly = true");
        final Text off = Text.literal("Set "+player.getEntityName()+" mayFly = false");

        if (fly) {
            ((SetPlayerAbilitiesAccess)player.getAbilities()).setAllowFlying(true);
            source.sendFeedback(on, true);
        } else {
            ((SetPlayerAbilitiesAccess)player.getAbilities()).setAllowFlying(false);
            ((SetPlayerAbilitiesAccess)player.getAbilities()).setFlying(false);
            source.sendFeedback(off, true);
        }

        player.sendAbilitiesUpdate();
    }
}