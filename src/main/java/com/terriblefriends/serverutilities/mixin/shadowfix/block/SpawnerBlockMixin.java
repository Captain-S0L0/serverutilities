package com.terriblefriends.serverutilities.mixin.shadowfix.block;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.MobSpawnerLogic;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SpawnerBlock.class)
public class SpawnerBlockMixin extends Block {
    public SpawnerBlockMixin(Settings settings) {
        super(settings);
    }

    //fix those damn tile entityless spawners
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity == null) {
            world.setBlockState(pos, Blocks.SPAWNER.getDefaultState());
            blockEntity = world.getBlockEntity(pos);
        }
        if (blockEntity instanceof MobSpawnerBlockEntity && ((MobSpawnerLogicAccess)((MobSpawnerBlockEntity)blockEntity).getLogic()).getSpawnEntry().entity().getString("id").equals("minecraft:pig")) {
            if (world.getRegistryKey() == World.OVERWORLD) {
                boolean isCaveSpider = false;
                for (int x = -5; x < 6; x++) {
                    for (int y = -5; y < 6; y++) {
                        for (int z = -5; z < 6; z++) {
                            if (world.getBlockState(pos.add(x,y,z)).isOf(Blocks.COBWEB)) {
                                isCaveSpider = true;
                                break;
                            }
                        }
                    }
                }
                if (!isCaveSpider) {
                    setSpawnerMob(blockEntity, pos, world, Util.getRandom(new EntityType[]{EntityType.SKELETON, EntityType.ZOMBIE, EntityType.ZOMBIE, EntityType.SPIDER}, random));
                    logChanges("[TileFixer] randomized overworld dungeon! "+pos.toShortString()+" in "+world.getRegistryKey().getValue().toString(),world.getServer());
                }
                else {
                    setSpawnerMob(blockEntity, pos, world, EntityType.CAVE_SPIDER);
                    logChanges("[TileFixer] fixed mineshaft spawner! "+pos.toShortString()+" in "+world.getRegistryKey().getValue().toString(),world.getServer());
                }
            }
            if (world.getRegistryKey() == World.NETHER) {
                boolean isBastion = false;

                for (int x = -5; x < 6; x++) {
                    for (int y = 5; y < 6; y++) {
                        for (int z = -5; z < 6; z++) {
                            if (world.getBlockState(pos.add(x,y,z)).isOf(Blocks.POLISHED_BLACKSTONE_BRICKS)) {
                                isBastion = true;
                                break;
                            }
                        }
                    }
                }
                if (!isBastion) {
                    setSpawnerMob(blockEntity, pos, world, EntityType.BLAZE);
                    logChanges("[TileFixer] fixed fortress spawner! "+pos.toShortString()+" in "+world.getRegistryKey().getValue().toString(),world.getServer());
                }
                else {
                    setSpawnerMob(blockEntity, pos, world, EntityType.MAGMA_CUBE);
                    logChanges("[TileFixer] fixed bastion spawner! "+pos.toShortString()+" in "+world.getRegistryKey().getValue().toString(),world.getServer());
                }
            }
        }
    }

    private void setSpawnerMob(BlockEntity blockEntity, BlockPos blockPos, World world, EntityType<?> entityType) {
        MobSpawnerLogic mobSpawnerLogic = ((MobSpawnerBlockEntity)blockEntity).getLogic();
        mobSpawnerLogic.setEntityId(entityType);
        blockEntity.markDirty();
        BlockState blockState = world.getBlockState(blockPos);
        world.updateListeners(blockPos, blockState, blockState, 3);
        world.emitGameEvent(null, GameEvent.BLOCK_CHANGE, blockPos);
    }

    private void logChanges(String string, MinecraftServer server) {
        for (ServerPlayerEntity serverPlayerEntity : server.getPlayerManager().getPlayerList()) {
            if (server.getPlayerManager().isOperator(serverPlayerEntity.getGameProfile())) {
                serverPlayerEntity.sendMessage(Text.literal(string));
            }
        }
    }
}
