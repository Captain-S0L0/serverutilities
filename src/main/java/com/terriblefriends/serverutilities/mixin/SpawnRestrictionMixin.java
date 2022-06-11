package com.terriblefriends.serverutilities.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(SpawnRestriction.class)
public class SpawnRestrictionMixin {
    /*@Inject(method="canSpawn(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/ServerWorldAccess;Lnet/minecraft/entity/SpawnReason;Lnet/minecraft/util/math/BlockPos;Ljava/util/Random;)Z",at=@At("HEAD"),cancellable = true)
    private static <T extends Entity> void canSpawnMixin(EntityType<T> type, ServerWorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random, CallbackInfoReturnable<Boolean> cir) {
        if (world.toServerWorld().getRegistryKey() == World.OVERWORLD) {
            BlockPos $$3 = world.toServerWorld().getSpawnPos();
            int $$4 = MathHelper.abs(pos.getX() - $$3.getX());
            int $$5 = MathHelper.abs(pos.getZ() - $$3.getZ());
            int $$6 = Math.max($$4, $$5);
            boolean $$1 = $$6 <= world.getServer().getSpawnProtectionRadius();
            if ($$1) {
                cir.setReturnValue(false);
                if (cir.isCancellable()) {
                    cir.cancel();
                }
            }
        }
    }*/

}
