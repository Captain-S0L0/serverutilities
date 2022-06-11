package com.terriblefriends.serverutilities.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static net.minecraft.server.command.CommandManager.literal;

public class VoteCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(literal("vote")
                .executes(ctx -> discord(ctx.getSource())));
    }

    private static int discord(ServerCommandSource source) throws CommandSyntaxException {
        ClickEvent link1 = new ClickEvent(ClickEvent.Action.OPEN_URL,"https://minecraftservers.org/vote/636921");
        ClickEvent link2 = new ClickEvent(ClickEvent.Action.OPEN_URL,"https://minecraft-server-list.com/server/488400/vote/");
        ClickEvent link3 = new ClickEvent(ClickEvent.Action.OPEN_URL,"https://minecraft-mp.com/server/305486/vote/");
        ClickEvent link4 = new ClickEvent(ClickEvent.Action.OPEN_URL,"https://minecraftlist.org/vote/28841");
        ClickEvent link5 = new ClickEvent(ClickEvent.Action.OPEN_URL,"https://servers-minecraft.net/server-terrible-friends.20149");

        source.getPlayer().sendMessage(Text.literal("Voting helps us keep the server active! You also will receive a vote token with each vote, which can be spent at spawn for cool items!").formatted(Formatting.RED));
        source.getPlayer().sendMessage(Text.literal("IMPORTANT: You must be online to receive rewards!!!").formatted(Formatting.DARK_RED,Formatting.BOLD));

        source.getPlayer().sendMessage(Text.literal("Site 1: ").formatted(Formatting.GOLD).append(Text.literal("[minecraftservers.org]").setStyle(Style.EMPTY.withClickEvent(link1)).formatted(Formatting.BLUE,Formatting.UNDERLINE)));
        source.getPlayer().sendMessage(Text.literal("Site 2: ").formatted(Formatting.GOLD).append(Text.literal("[minecraft-server-list.com]").setStyle(Style.EMPTY.withClickEvent(link2)).formatted(Formatting.BLUE,Formatting.UNDERLINE)));
        source.getPlayer().sendMessage(Text.literal("Site 3: ").formatted(Formatting.GOLD).append(Text.literal("[minecraft-mp.com]").setStyle(Style.EMPTY.withClickEvent(link3)).formatted(Formatting.BLUE,Formatting.UNDERLINE)));
        source.getPlayer().sendMessage(Text.literal("Site 4: ").formatted(Formatting.GOLD).append(Text.literal("[minecraftlist.org]").setStyle(Style.EMPTY.withClickEvent(link4)).formatted(Formatting.BLUE,Formatting.UNDERLINE)));
        source.getPlayer().sendMessage(Text.literal("Site 5: ").formatted(Formatting.GOLD).append(Text.literal("[servers-minecraft.net]").setStyle(Style.EMPTY.withClickEvent(link5)).formatted(Formatting.BLUE,Formatting.UNDERLINE)));

        return 1;
    }
}
