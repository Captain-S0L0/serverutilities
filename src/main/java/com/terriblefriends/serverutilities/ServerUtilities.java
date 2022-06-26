package com.terriblefriends.serverutilities;

import com.terriblefriends.serverutilities.command.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerBlockEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.GameRules;

import java.util.Iterator;

public class ServerUtilities implements ModInitializer {
    public static MinecraftServer server;

    public static final GameRules.Key<GameRules.BooleanRule> END_DISABLED =
            GameRuleRegistry.register("endDisabled", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(false));
    public static final GameRules.Key<GameRules.IntRule> HORSE_SPEED_MULTIPLIER =
            GameRuleRegistry.register("horseSpeedMultiplier", GameRules.Category.MOBS,GameRuleFactory.createIntRule(1));
    public static final GameRules.Key<GameRules.IntRule> MINECART_SPEED_MULTIPLIER =
            GameRuleRegistry.register("minecartSpeedMultiplier", GameRules.Category.MOBS,GameRuleFactory.createIntRule(1));
    public static final GameRules.Key<GameRules.BooleanRule> ADVENTURE_GHOST_MODE =
            GameRuleRegistry.register("adventureGhostMode", GameRules.Category.PLAYER,GameRuleFactory.createBooleanRule(false));
    public static final GameRules.Key<GameRules.BooleanRule> THROWABLE_FIREBALLS =
            GameRuleRegistry.register("throwableFireballs", GameRules.Category.PLAYER,GameRuleFactory.createBooleanRule(false));
    public static final GameRules.Key<GameRules.IntRule> THROWABLE_FIREBALL_POWER =
            GameRuleRegistry.register("throwableFireballPower", GameRules.Category.MISC,GameRuleFactory.createIntRule(1));
    public static final GameRules.Key<GameRules.IntRule> ACTION_RATE_LIMIT =
            GameRuleRegistry.register("actionRateLimit", GameRules.Category.MISC,GameRuleFactory.createIntRule(40));
    public static int actionRateLimit = 40;
    public static ServerWorld overworld;


    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register(DiscordCommand::register); // discord
        CommandRegistrationCallback.EVENT.register(DynmapCommand::register); // dynmaplink
        CommandRegistrationCallback.EVENT.register(FlyCommand::register); // fly
        CommandRegistrationCallback.EVENT.register(FlySpeedCommand::register); // flyspeed
        CommandRegistrationCallback.EVENT.register(GetWeatherCommand::register); // getweather
        CommandRegistrationCallback.EVENT.register(GodCommand::register); // god
        CommandRegistrationCallback.EVENT.register(OperatorCommand::register); // opwp
        CommandRegistrationCallback.EVENT.register(SetScoreboardCommand::register); // setscoreboard
        CommandRegistrationCallback.EVENT.register(SetWeatherTimeCommand::register); // setweathertime
        CommandRegistrationCallback.EVENT.register(ShadowCommand::register); //shadow
        CommandRegistrationCallback.EVENT.register(SuicideCommand::register); // suicide
        CommandRegistrationCallback.EVENT.register(TempBanCommand::register); //tempban
        CommandRegistrationCallback.EVENT.register(VoteBanCommand::register); //voteban
        CommandRegistrationCallback.EVENT.register(VoteCommand::register); //vote
        CommandRegistrationCallback.EVENT.register(WhereIsCommand::register); //whereis

        ServerLifecycleEvents.SERVER_STARTED.register(serverArg -> {
            overworld = serverArg.getOverworld();
            server = serverArg;
        });
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            actionRateLimit = server.getOverworld().getGameRules().getInt(ACTION_RATE_LIMIT);
            VoteBanCommand.tickBanVoter();
        });
        ServerBlockEntityEvents.BLOCK_ENTITY_LOAD.register((blockEntity, world) -> {
            if (blockEntity instanceof MobSpawnerBlockEntity) {
                MobSpawnerBlockEntity spawner = (MobSpawnerBlockEntity) blockEntity;
                if (spawner.getLogic().getRenderedEntity(world) instanceof PigEntity) {
                    String message = "Pig spawner located! "+blockEntity.getPos().toShortString();
                    System.out.println(message);

                    for (ServerPlayerEntity serverPlayerEntity : server.getPlayerManager().getPlayerList()) {
                        if (server.getPlayerManager().isOperator(serverPlayerEntity.getGameProfile())) {
                            serverPlayerEntity.sendMessage(Text.literal(message));
                        }
                    }
                }
            }
        });
    }
}
