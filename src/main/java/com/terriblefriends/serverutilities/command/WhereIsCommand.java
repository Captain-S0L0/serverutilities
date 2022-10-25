package com.terriblefriends.serverutilities.command;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Dynamic;
import com.terriblefriends.serverutilities.ServerUtilities;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.dimension.DimensionType;

import java.util.Collection;
import java.util.Iterator;

import static net.minecraft.server.command.CommandManager.literal;

public class WhereIsCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(literal("whereis")
                .requires(source -> source.hasPermissionLevel(3))
                .then(CommandManager.argument("target", GameProfileArgumentType.gameProfile()).executes(ctx -> whereis(ctx.getSource(), GameProfileArgumentType.getProfileArgument(ctx,"target"))))
        );
    }

    private static int whereis(ServerCommandSource source, Collection<GameProfile> targets) throws CommandSyntaxException {
        Iterator var5 = targets.iterator();

        while(var5.hasNext()) {
            GameProfile gameProfile = (GameProfile)var5.next();
            ServerPlayerEntity requestedPlayer = ServerUtilities.server.getPlayerManager().getPlayer(gameProfile.getName());

            NbtCompound playerData;
            if (requestedPlayer == null) {
                requestedPlayer = ServerUtilities.server.getPlayerManager().createPlayer(gameProfile, null);
                playerData = ServerUtilities.server.getPlayerManager().loadPlayerData(requestedPlayer);
                if (playerData != null) {
                    ServerWorld world = ServerUtilities.server.getWorld(
                            DimensionType.worldFromDimensionNbt(new Dynamic<>(NbtOps.INSTANCE, playerData.get("Dimension"))).result().get()
                    );

                    if (world != null) {
                        requestedPlayer.setWorld(world);
                    }
                }
            }
            else {
                playerData = requestedPlayer.writeNbt(new NbtCompound());
            }

            NbtList nbtList = playerData.getList("Pos", 6);
            int x = (int)nbtList.getDouble(0);
            int y = (int)nbtList.getDouble(1);
            int z = (int)nbtList.getDouble(2);

            String dimension = DimensionType.worldFromDimensionNbt(new Dynamic<>(NbtOps.INSTANCE, playerData.get("Dimension"))).result().get().getValue().toString();

            source.sendFeedback(Text.literal(gameProfile.getName()+" is / was located at: "+x+","+y+","+z+" in dimension "+dimension),false);
        }
        return 1;
    }
}