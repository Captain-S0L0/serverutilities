package com.terriblefriends.serverutilities.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public class SuicideCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
        dispatcher.register(literal("suicide")
                .executes(ctx -> suicide(ctx.getSource())));
    }

    private static int suicide(ServerCommandSource source) throws CommandSyntaxException {
        source.getPlayer().kill();
        return 1;
    }
}
