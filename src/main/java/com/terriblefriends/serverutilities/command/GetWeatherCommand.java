package com.terriblefriends.serverutilities.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.terriblefriends.serverutilities.access.ServerWorldAccess;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.literal;

public class GetWeatherCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(literal("getweather")
                //.requires(source -> source.hasPermissionLevel(2))
                .executes(ctx -> getWeather(ctx.getSource())));
    }

    private static int getWeather(ServerCommandSource source) throws CommandSyntaxException {
        int clearWeatherTime = ((ServerWorldAccess)source.getWorld()).getServerWorldProperties().getClearWeatherTime();
        int rainTime = ((ServerWorldAccess)source.getWorld()).getServerWorldProperties().getRainTime();
        int thunderTime = ((ServerWorldAccess)source.getWorld()).getServerWorldProperties().getThunderTime();
        boolean isRaining = source.getWorld().isRaining();
        boolean isThundering = source.getWorld().isThundering();
        source.sendFeedback(Text.literal("cleartime:"+clearWeatherTime+",raintime:"+rainTime+",thundertime:"+thunderTime),false);
        source.sendFeedback(Text.literal("israining:"+isRaining+",isthundering:"+isThundering),false);
        return 1;
    }
}
