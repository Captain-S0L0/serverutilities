package com.terriblefriends.serverutilities;

import com.terriblefriends.serverutilities.command.*;
import com.terriblefriends.serverutilities.config.Config;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;

public class ServerUtilities implements ModInitializer {
    public static MinecraftServer server;

    public static final GameRules.Key<GameRules.BooleanRule> ADVENTURE_GHOST_MODE =
            GameRuleRegistry.register("adventureGhostMode", GameRules.Category.PLAYER,GameRuleFactory.createBooleanRule(false));
    public static final GameRules.Key<GameRules.BooleanRule> END_DISABLED =
            GameRuleRegistry.register("endDisabled", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(false));
    public static final GameRules.Key<GameRules.BooleanRule> HARDCORE_DEATH_BAN =
            GameRuleRegistry.register("hardcoreDeathBan", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(false));
    public static final GameRules.Key<GameRules.IntRule> HARDCORE_DEATH_BAN_DURATION =
            GameRuleRegistry.register("hardcoreDeathBanDuration", GameRules.Category.MOBS,GameRuleFactory.createIntRule(0,0));
    public static final GameRules.Key<GameRules.IntRule> HORSE_SPEED_MULTIPLIER =
            GameRuleRegistry.register("horseSpeedMultiplier", GameRules.Category.MOBS,GameRuleFactory.createIntRule(1));
    public static final GameRules.Key<GameRules.BooleanRule> LOBOTOMIZE_PHANTOMS =
            GameRuleRegistry.register("lobotomizePhantoms", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(false));
    //public static final GameRules.Key<GameRules.IntRule> MINECART_SPEED_MULTIPLIER =
            //GameRuleRegistry.register("minecartSpeedMultiplier", GameRules.Category.MOBS,GameRuleFactory.createIntRule(1));
    public static final GameRules.Key<GameRules.BooleanRule> THROWABLE_FIREBALLS =
            GameRuleRegistry.register("throwableFireballs", GameRules.Category.PLAYER,GameRuleFactory.createBooleanRule(false));
    public static final GameRules.Key<GameRules.IntRule> THROWABLE_FIREBALL_POWER =
            GameRuleRegistry.register("throwableFireballPower", GameRules.Category.MISC,GameRuleFactory.createIntRule(1));
    public static final GameRules.Key<GameRules.IntRule> VOTEBAN_PERCENTAGE =
            GameRuleRegistry.register("voteBanPercentage", GameRules.Category.MISC,GameRuleFactory.createIntRule(66,1,100));
    public static final GameRules.Key<GameRules.IntRule> VOTEBAN_MINIMUM =
            GameRuleRegistry.register("voteBanMinimum", GameRules.Category.MISC,GameRuleFactory.createIntRule(3));
    public static final GameRules.Key<GameRules.IntRule> VOTEBAN_DURATION =
            GameRuleRegistry.register("voteBanDuration", GameRules.Category.MISC,GameRuleFactory.createIntRule(1,1));


    @Override
    public void onInitialize() {
        Config.load();
        Config.save();
        //Everyone Commands
        if (Config.get().enableDiscordCommand) {
            CommandRegistrationCallback.EVENT.register(DiscordCommand::register); // discord
        }
        if (Config.get().enableDynmapCommand) {
            CommandRegistrationCallback.EVENT.register(DynmapCommand::register); // dynmap
        }
        if (Config.get().enableGetWeatherCommand) {
            CommandRegistrationCallback.EVENT.register(GetWeatherCommand::register); // getweather
        }
        if (Config.get().enableSetScoreboardCommand) {
            CommandRegistrationCallback.EVENT.register(SetScoreboardCommand::register); // setscoreboard
        }
        if (Config.get().enableSuicideCommand) {
            CommandRegistrationCallback.EVENT.register(SuicideCommand::register); // suicide
        }
        if (Config.get().enableVoteCommand) {
            CommandRegistrationCallback.EVENT.register(VoteCommand::register); //vote
        }
        if (Config.get().enableVoteBanCommand) {
            CommandRegistrationCallback.EVENT.register(VoteBanCommand::register); //voteban
        }

        //Admin Commands
        CommandRegistrationCallback.EVENT.register(FlyCommand::register); // fly
        CommandRegistrationCallback.EVENT.register(FlySpeedCommand::register); // flyspeed
        CommandRegistrationCallback.EVENT.register(GodCommand::register); // god
        CommandRegistrationCallback.EVENT.register(OperatorCommand::register); // opwp
        CommandRegistrationCallback.EVENT.register(SetWeatherTimeCommand::register); // setweathertime
        CommandRegistrationCallback.EVENT.register(ShadowCommand::register); //shadow
        CommandRegistrationCallback.EVENT.register(TempBanCommand::register); //tempban
        CommandRegistrationCallback.EVENT.register(WhereIsCommand::register); //whereis

        ServerLifecycleEvents.SERVER_STARTED.register(serverArg -> {
            server = serverArg;
        });
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            VoteBanCommand.tickBanVoter();
        });
    }
}
