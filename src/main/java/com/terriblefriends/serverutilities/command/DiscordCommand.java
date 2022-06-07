package com.terriblefriends.serverutilities.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;


import static net.minecraft.server.command.CommandManager.literal;

public class DiscordCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(literal("discord")
                .executes(ctx -> discord(ctx.getSource())));
    }

    private static int discord(ServerCommandSource source) throws CommandSyntaxException {
        ClickEvent link = new ClickEvent(ClickEvent.Action.OPEN_URL,"http://discord.terriblefriends.ml");
        //source.getPlayer().sendMessage(new LiteralTextContent("[Discord Link (Click Me!)]").(Style.EMPTY.withClickEvent(link)).formatted(Formatting.BLUE).formatted(Formatting.UNDERLINE));
        source.getPlayer().sendMessage(Text.literal("[Discord Link (Click Me!)]").setStyle(Style.EMPTY.withClickEvent(link)).formatted(Formatting.BLUE,Formatting.UNDERLINE));

        return 1;
    }
}
