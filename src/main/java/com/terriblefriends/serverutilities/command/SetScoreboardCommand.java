package com.terriblefriends.serverutilities.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.argument.ScoreboardObjectiveArgumentType;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;

import static net.minecraft.server.command.CommandManager.literal;

public class SetScoreboardCommand {
    private static final SimpleCommandExceptionType OBJECTIVES_DISPLAY_ALREADY_SET_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.scoreboard.objectives.display.alreadySet"));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
        dispatcher.register(literal("setscoreboard")
                .then(CommandManager.argument("objective", ScoreboardObjectiveArgumentType.scoreboardObjective()).executes(ctx -> executeSetDisplay(ctx.getSource(), ScoreboardObjectiveArgumentType.getObjective(ctx, "objective")))));
    }

    private static int executeSetDisplay(ServerCommandSource source, ScoreboardObjective objective) throws CommandSyntaxException {
        Scoreboard scoreboard = source.getServer().getScoreboard();
        if (scoreboard.getObjectiveForSlot(1) == objective) {
            throw OBJECTIVES_DISPLAY_ALREADY_SET_EXCEPTION.create();
        } else {
            scoreboard.setObjectiveSlot(1, objective);
            source.sendFeedback(new TranslatableText("commands.scoreboard.objectives.display.set", new Object[]{Scoreboard.getDisplaySlotNames()[1], objective.getDisplayName()}), true);
            return 0;
        }
    }
}
