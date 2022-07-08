package com.terriblefriends.serverutilities.mixin.shadowfix.block;

import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.loot.LootTables;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ChestBlock.class)
public class ChestBlockMixin {
    /*public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (world.isClient) {
            return;
        }

        System.out.println("chest tick");
        System.out.println(pos.toShortString());
        BlockEntity blockEntityOld = world.getBlockEntity(pos);
        if (blockEntityOld == null) {
            world.setBlockState(pos, Blocks.CHEST.getDefaultState());
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity == null) {
                System.out.println("chest block entity not made!");
            }

            boolean isDungeon = false;
            boolean isPortal = false;
            boolean isStronghold = false;

            boolean isBastion = false;
            boolean isFortress = false;

            if (world.getRegistryKey() == World.OVERWORLD) {
                for (int x = -5; x < 6; x++) {
                    for (int y = -5; y < 6; y++) {
                        for (int z = -5; z < 6; z++) {
                            if (world.getBlockState(pos.add(x,y,z)).isOf(Blocks.MOSSY_COBBLESTONE)) {
                                isDungeon = true;
                                System.out.println("dungeon");
                                break;
                            }
                        }
                    }
                }
                for (int x = -5; x < 6; x++) {
                    for (int y = 5; y < 6; y++) {
                        for (int z = -5; z < 6; z++) {
                            if (world.getBlockState(pos.add(x,y,z)).isOf(Blocks.CRYING_OBSIDIAN)) {
                                isPortal = true;
                                System.out.println("portal");
                                break;
                            }
                        }
                    }
                }
                for (int x = -5; x < 6; x++) {
                    for (int y = 5; y < 6; y++) {
                        for (int z = -5; z < 6; z++) {
                            if (world.getBlockState(pos.add(x,y,z)).isOf(Blocks.STONE_BRICKS)) {
                                isStronghold = true;
                                System.out.println("stronghold");
                                break;
                            }
                        }
                    }
                }
            }
            if (world.getRegistryKey() == World.NETHER) {
                for (int x = -5; x < 6; x++) {
                    for (int y = 5; y < 6; y++) {
                        for (int z = -5; z < 6; z++) {
                            if (world.getBlockState(pos.add(x,y,z)).isOf(Blocks.POLISHED_BLACKSTONE_BRICKS)) {
                                isBastion = true;
                                System.out.println("bastion");
                                break;
                            }
                        }
                    }
                }
                for (int x = -5; x < 6; x++) {
                    for (int y = 5; y < 6; y++) {
                        for (int z = -5; z < 6; z++) {
                            if (world.getBlockState(pos.add(x,y,z)).isOf(Blocks.NETHER_BRICKS)) {
                                isFortress = true;
                                System.out.println("fortress");
                                break;
                            }
                        }
                    }
                }
                for (int x = -5; x < 6; x++) {
                    for (int y = 5; y < 6; y++) {
                        for (int z = -5; z < 6; z++) {
                            if (world.getBlockState(pos.add(x,y,z)).isOf(Blocks.CRYING_OBSIDIAN)) {
                                isPortal = true;
                                System.out.println("portal");
                                break;
                            }
                        }
                    }
                }
            }
            if (isDungeon) {
                ((LootableContainerBlockEntity)blockEntity).setLootTable(LootTables.SIMPLE_DUNGEON_CHEST, random.nextLong());
                logChanges("[TileFixer] set dungeon chest loot! "+pos.toShortString()+" in "+world.getRegistryKey().getValue().toString(),world.getServer());
            }
            if (isPortal) {
                LootableContainerBlockEntity.setLootTable(world, random, pos, LootTables.SIMPLE_DUNGEON_CHEST);
                logChanges("[TileFixer] set portal chest loot! "+pos.toShortString()+" in "+world.getRegistryKey().getValue().toString(),world.getServer());
            }
            if (isStronghold) {
                LootableContainerBlockEntity.setLootTable(world, random, pos, LootTables.STRONGHOLD_CORRIDOR_CHEST);
                logChanges("[TileFixer] set stronghold chest loot! "+pos.toShortString()+" in "+world.getRegistryKey().getValue().toString(),world.getServer());
            }
            if (isFortress) {
                LootableContainerBlockEntity.setLootTable(world, random, pos, LootTables.NETHER_BRIDGE_CHEST);
                logChanges("[TileFixer] set fortress chest loot! "+pos.toShortString()+" in "+world.getRegistryKey().getValue().toString(),world.getServer());
            }
            if (isBastion) {
                LootableContainerBlockEntity.setLootTable(world, random, pos, LootTables.BASTION_OTHER_CHEST);
                logChanges("[TileFixer] set bastion chest loot! "+pos.toShortString()+" in "+world.getRegistryKey().getValue().toString(),world.getServer());
            }
        }

        System.out.println(blockEntityOld);
    }

    private void logChanges(String string, MinecraftServer server) {
        for (ServerPlayerEntity serverPlayerEntity : server.getPlayerManager().getPlayerList()) {
            if (server.getPlayerManager().isOperator(serverPlayerEntity.getGameProfile())) {
                serverPlayerEntity.sendMessage(Text.literal(string));
            }
        }
    }*/
}
