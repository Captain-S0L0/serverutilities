package com.terriblefriends.serverutilities.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.terriblefriends.serverutilities.config.Config;
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
                .executes(ctx -> vote(ctx.getSource())));
    }

    private static int vote(ServerCommandSource source) throws CommandSyntaxException {

        source.getPlayer().sendMessage(Text.literal(Config.get().voteInfo).formatted(Formatting.RED));
        source.getPlayer().sendMessage(Text.literal("IMPORTANT: You must be online to receive rewards!!!").formatted(Formatting.DARK_RED,Formatting.BOLD));

        int counter = 1;
        for (String site: Config.get().votingSites) {
            source.getPlayer().sendMessage(Text.literal("Site "+counter+": ").formatted(Formatting.GOLD).append(Text.literal("["+site+"]").setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, site))).formatted(Formatting.BLUE,Formatting.UNDERLINE)));
            counter++;
        }

        return 1;
    }
}
