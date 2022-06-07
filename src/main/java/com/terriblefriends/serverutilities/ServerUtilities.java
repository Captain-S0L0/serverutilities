package com.terriblefriends.serverutilities;

import com.terriblefriends.serverutilities.command.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameRules;

public class ServerUtilities implements ModInitializer {
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
            GameRuleRegistry.register("actionRateLimit", GameRules.Category.MISC,GameRuleFactory.createIntRule(20));
    public static int actionRateLimit = 20;
    public static ServerWorld overworld;


    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register(FlyCommand::register); // fly
        CommandRegistrationCallback.EVENT.register(GodCommand::register); // god
        CommandRegistrationCallback.EVENT.register(FlySpeedCommand::register); // flyspeed
        CommandRegistrationCallback.EVENT.register(SuicideCommand::register); // suicide
        CommandRegistrationCallback.EVENT.register(OperatorCommand::register); // opwp
        CommandRegistrationCallback.EVENT.register(GetWeatherCommand::register); // getweather
        CommandRegistrationCallback.EVENT.register(SetWeatherTimeCommand::register); // setweathertime
        CommandRegistrationCallback.EVENT.register(SetScoreboardCommand::register); // setscoreboard
        CommandRegistrationCallback.EVENT.register(DiscordCommand::register); // discord
        CommandRegistrationCallback.EVENT.register(DynmapCommand::register); // dynmaplink
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            overworld = server.getOverworld();
        });
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            actionRateLimit = server.getOverworld().getGameRules().getInt(ACTION_RATE_LIMIT);
        });
    }
}
