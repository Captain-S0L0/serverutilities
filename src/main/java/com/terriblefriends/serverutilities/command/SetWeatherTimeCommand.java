package com.terriblefriends.serverutilities.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.terriblefriends.serverutilities.access.ServerWorldAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;

import static net.minecraft.server.command.CommandManager.literal;

public class SetWeatherTimeCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
        dispatcher.register(literal("setweathertime")
                .requires(source -> source.hasPermissionLevel(2))
                .then(literal("rain")
                    .then(CommandManager.argument("duration", IntegerArgumentType.integer(0, Integer.MAX_VALUE))
                        .executes(ctx -> setRainTime(ctx.getSource(), IntegerArgumentType.getInteger(ctx,"duration")))))
                .then(literal("thunder")
                    .then(CommandManager.argument("duration", IntegerArgumentType.integer(0, Integer.MAX_VALUE))
                        .executes(ctx -> setThunderTime(ctx.getSource(), IntegerArgumentType.getInteger(ctx,"duration")))))
                .then(literal("clear")
                    .then(CommandManager.argument("duration", IntegerArgumentType.integer(0, Integer.MAX_VALUE))
                        .executes(ctx -> setClearTime(ctx.getSource(), IntegerArgumentType.getInteger(ctx,"duration")))))
        );
    }

    private static int setRainTime(ServerCommandSource source, int time) throws CommandSyntaxException {
        ((ServerWorldAccess)source.getWorld()).getServerWorldProperties().setRainTime(time);
        source.sendFeedback(new LiteralText("Set rain time to "+time),true);
        return 1;
    }
    private static int setThunderTime(ServerCommandSource source, int time) throws CommandSyntaxException {
        ((ServerWorldAccess)source.getWorld()).getServerWorldProperties().setThunderTime(time);
        source.sendFeedback(new LiteralText("Set thunder time to "+time),true);
        return 1;
    }
    private static int setClearTime(ServerCommandSource source, int time) throws CommandSyntaxException {
        ((ServerWorldAccess)source.getWorld()).getServerWorldProperties().setClearWeatherTime(time);
        source.sendFeedback(new LiteralText("Set clear time to "+time),true);
        return 1;
    }
}
