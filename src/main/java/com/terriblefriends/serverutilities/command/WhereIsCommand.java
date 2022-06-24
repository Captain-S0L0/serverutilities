package com.terriblefriends.serverutilities.command;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Dynamic;
import com.terriblefriends.serverutilities.ServerUtilities;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.dimension.DimensionType;
//import us.potatoboy.invview.gui.SavingPlayerDataGui;

import java.security.PublicKey;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

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
            } //Player874
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

    private static ServerPlayerEntity getRequestedPlayer(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        GameProfile requestedProfile = GameProfileArgumentType.getProfileArgument(context, "target").iterator().next();
        ServerPlayerEntity requestedPlayer = ServerUtilities.server.getPlayerManager().getPlayer(requestedProfile.getName());

        if (requestedPlayer == null) {
            requestedPlayer = ServerUtilities.server.getPlayerManager().createPlayer(requestedProfile, null);
            NbtCompound compound = ServerUtilities.server.getPlayerManager().loadPlayerData(requestedPlayer);
            if (compound != null) {
                ServerWorld world = ServerUtilities.server.getWorld(
                        DimensionType.worldFromDimensionNbt(new Dynamic<>(NbtOps.INSTANCE, compound.get("Dimension"))).result().get()
                );

                if (world != null) {
                    requestedPlayer.setWorld(world);
                }
            }
        }

        return requestedPlayer;
    }

    /*private static MinecraftServer minecraftServer = InvView.getMinecraftServer();

    public static int inv(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();
        ServerPlayerEntity requestedPlayer = getRequestedPlayer(context);

        isProtected(requestedPlayer).thenAcceptAsync(isProtected -> {
            if (isProtected) {
                context.getSource().sendError(Text.literal("Requested inventory is protected"));
            } else {
                SimpleGui gui = new SavingPlayerDataGui(ScreenHandlerType.GENERIC_9X5, player, requestedPlayer);
                gui.setTitle(requestedPlayer.getName());
                for (int i = 0; i < player.getInventory().size(); i++) {
                    gui.setSlotRedirect(i, new Slot(requestedPlayer.getInventory(), i, 0, 0));
                }

                gui.open();
            }
        });

        return 1;
    }

    public static int eChest(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();
        ServerPlayerEntity requestedPlayer = getRequestedPlayer(context);
        EnderChestInventory requestedEchest = requestedPlayer.getEnderChestInventory();

        isProtected(requestedPlayer).thenAcceptAsync(isProtected -> {
            if (isProtected) {
                context.getSource().sendError(Text.literal("Requested inventory is protected"));
            } else {
                SimpleGui gui = new SavingPlayerDataGui(ScreenHandlerType.GENERIC_9X3, player, requestedPlayer);
                gui.setTitle(requestedPlayer.getName());
                for (int i = 0; i < requestedEchest.size(); i++) {
                    gui.setSlotRedirect(i, new Slot(requestedEchest, i, 0, 0));
                }

                gui.open();
            }
        });

        return 1;
    }

    public static int trinkets(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();
        ServerPlayerEntity requestedPlayer = getRequestedPlayer(context);
        TrinketComponent requestedComponent = TrinketsApi.getTrinketComponent(requestedPlayer).get();

        isProtected(requestedPlayer).thenAcceptAsync(isProtected -> {
            if (isProtected) {
                context.getSource().sendError(Text.literal("Requested inventory is protected"));
            } else {
                SimpleGui gui = new SavingPlayerDataGui(ScreenHandlerType.GENERIC_9X2, player, requestedPlayer);
                gui.setTitle(requestedPlayer.getName());
                int index = 0;
                for (Map<String, TrinketInventory> group : requestedComponent.getInventory().values()) {
                    for (TrinketInventory inventory : group.values()) {
                        for (int i = 0; i < inventory.size(); i++) {
                            gui.setSlotRedirect(index, new Slot(inventory, i, 0, 0));
                            index += 1;
                        }
                    }
                }

                gui.open();
            }
        });

        return 1;
    }

    public static int origin(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();
        ServerPlayerEntity requestedPlayer = getRequestedPlayer(context);

        isProtected(requestedPlayer).thenAcceptAsync(isProtected -> {
            if (isProtected) {
                context.getSource().sendError(Text.literal("Requested inventory is protected"));
            } else {
                List<InventoryPower> inventories = PowerHolderComponent.getPowers(requestedPlayer, InventoryPower.class);
                if (inventories.isEmpty()) {
                    context.getSource().sendError(Text.literal("Requested player has no inventory power"));
                } else {
                    SimpleGui gui = new SavingPlayerDataGui(ScreenHandlerType.GENERIC_9X5, player, requestedPlayer);
                    gui.setTitle(requestedPlayer.getName());
                    int index = 0;
                    for (InventoryPower inventory : inventories) {
                        for (int i = 0; i < inventory.size(); i++) {
                            gui.setSlotRedirect(index, new Slot(inventory, index, 0, 0));
                            index += 1;
                        }
                    }

                    gui.open();
                }
            }
        });

        return 1;
    }

    private static ServerPlayerEntity getRequestedPlayer(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        GameProfile requestedProfile = GameProfileArgumentType.getProfileArgument(context, "target").iterator().next();
        ServerPlayerEntity requestedPlayer = minecraftServer.getPlayerManager().getPlayer(requestedProfile.getName());

        if (requestedPlayer == null) {
            requestedPlayer = minecraftServer.getPlayerManager().createPlayer(requestedProfile, null);
            NbtCompound compound = minecraftServer.getPlayerManager().loadPlayerData(requestedPlayer);
            if (compound != null) {
                ServerWorld world = minecraftServer.getWorld(
                        DimensionType.worldFromDimensionNbt(new Dynamic<>(NbtOps.INSTANCE, compound.get("Dimension"))).result().get()
                );

                if (world != null) {
                    requestedPlayer.setWorld(world);
                }
            }
        }

        return requestedPlayer;
    }*/
}