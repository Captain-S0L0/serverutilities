package com.terriblefriends.serverutilities.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;


import static net.minecraft.server.command.CommandManager.literal;

public class DiscordCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
        dispatcher.register(literal("discord")
                .executes(ctx -> discord(ctx.getSource())));
    }

    private static int discord(ServerCommandSource source) throws CommandSyntaxException {
        ClickEvent link = new ClickEvent(ClickEvent.Action.OPEN_URL,"http://terriblefriends.tk/discord");
        source.getPlayer().sendSystemMessage(new LiteralText("[Discord Link (Click Me!)]").setStyle(Style.EMPTY.withClickEvent(link)).formatted(Formatting.BLUE).formatted(Formatting.UNDERLINE), Util.NIL_UUID);

        return 1;
    }
}
