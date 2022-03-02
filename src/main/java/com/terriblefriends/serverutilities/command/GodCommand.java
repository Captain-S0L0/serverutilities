package com.terriblefriends.serverutilities.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.terriblefriends.serverutilities.access.SetPlayerAbilitiesAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.util.Collection;

import static com.mojang.brigadier.arguments.BoolArgumentType.getBool;
import static net.minecraft.command.argument.EntityArgumentType.getPlayers;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;


public class GodCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
        dispatcher.register(literal("god")
                .requires(source -> source.hasPermissionLevel(2))
                .then(argument("boolean", BoolArgumentType.bool()).executes(ctx -> handleSet(ctx.getSource(), null,getBool(ctx,"boolean"))))
                .then(argument("targets", EntityArgumentType.players()).executes(ctx -> handleToggle(ctx.getSource(), getPlayers(ctx, "targets")))
                        .then(argument("boolean", BoolArgumentType.bool()).executes(ctx -> handleSet(ctx.getSource(), getPlayers(ctx, "targets"),getBool(ctx,"boolean"))))
                )
                .executes(ctx -> handleToggle(ctx.getSource(), null)));
    }

    private static int handleToggle(ServerCommandSource source, Collection<ServerPlayerEntity> targets) throws CommandSyntaxException {

        if(targets == null) {
            toggleGodMode(source.getPlayer(), source);
        } else {
            targets.forEach(target -> toggleGodMode(target, source));
        }

        return 1;
    }

    private static int handleSet(ServerCommandSource source, Collection<ServerPlayerEntity> targets, boolean god) throws CommandSyntaxException {

        if(targets == null) {
            setGodMode(source.getPlayer(), source, god);
        } else {
            targets.forEach(target -> setGodMode(target, source, god));
        }

        return 1;
    }

    private static void toggleGodMode(ServerPlayerEntity player, ServerCommandSource source) {
        final Text on = new LiteralText("Set "+player.getEntityName()+" invulnerable = true");
        final Text off = new LiteralText("Set "+player.getEntityName()+" invulnerable = false");

        if (!player.getAbilities().invulnerable) {
            ((SetPlayerAbilitiesAccess)player.getAbilities()).setInvulnerable(true);
            source.sendFeedback(on, true);
        } else {
            ((SetPlayerAbilitiesAccess)player.getAbilities()).setInvulnerable(false);
            source.sendFeedback(off, true);
        }

        player.sendAbilitiesUpdate();
    }

    private static void setGodMode(ServerPlayerEntity player, ServerCommandSource source, boolean god) {
        final Text on = new LiteralText("Set "+player.getEntityName()+" invulnerable = true");
        final Text off = new LiteralText("Set "+player.getEntityName()+" invulnerable = false");

        if (god) {
            ((SetPlayerAbilitiesAccess)player.getAbilities()).setInvulnerable(true);
            source.sendFeedback(on, true);
        } else {
            ((SetPlayerAbilitiesAccess)player.getAbilities()).setInvulnerable(false);
            source.sendFeedback(off, true);
        }

        player.sendAbilitiesUpdate();
    }
}