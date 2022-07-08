package com.terriblefriends.serverutilities.mixin.shadowfix.block;

import net.minecraft.world.MobSpawnerEntry;
import net.minecraft.world.MobSpawnerLogic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MobSpawnerLogic.class)
public interface MobSpawnerLogicAccess {
    @Accessor
    MobSpawnerEntry getSpawnEntry();
}
