package com.terriblefriends.serverutilities.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public class ShadowCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(literal("shadow")
                .requires(source -> source.hasPermissionLevel(2))
                .executes(ctx -> shadow(ctx.getSource())));
    }

    private static int shadow(ServerCommandSource source) throws CommandSyntaxException {
        int slotId = source.getPlayer().getInventory().getEmptySlot();
        if (slotId != -1) {
            source.getPlayer().getInventory().setStack(slotId,source.getPlayer().getMainHandStack());
        }
        return 1;
    }
}
