package com.terriblefriends.serverutilities.command;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Dynamic;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.dimension.DimensionType;
//import us.potatoboy.invview.gui.SavingPlayerDataGui;

import java.security.PublicKey;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class WhereIsCommand {
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