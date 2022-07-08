package com.terriblefriends.serverutilities.mixin.misc;

import com.terriblefriends.serverutilities.ServerUtilities;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.explosion.EntityExplosionBehavior;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityExplosionBehavior.class)
public class EntityExplosionBehaviorMixin {
    @Shadow @Final private Entity entity;
    @Inject(at=@At("HEAD"),method="canDestroyBlock",cancellable = true)
    private void preventExplosionsInSpawn(Explosion explosion, BlockView world, BlockPos pos, BlockState state, float power, CallbackInfoReturnable<Boolean> cir) {
        boolean isSpawnProtected = false;
        if (world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)world;
            if (serverWorld.getRegistryKey() != World.OVERWORLD) {
                isSpawnProtected = false;
            } else if (serverWorld.getServer().getSpawnProtectionRadius() <= 0) {
                isSpawnProtected = false;
            } else {
                BlockPos blockPos = serverWorld.getSpawnPos();
                int i = MathHelper.abs(pos.getX() - blockPos.getX());
                int j = MathHelper.abs(pos.getZ() - blockPos.getZ());
                int k = Math.max(i, j);
                isSpawnProtected = k <= serverWorld.getServer().getSpawnProtectionRadius();
            }
        }

        cir.setReturnValue(!isSpawnProtected && entity.canExplosionDestroyBlock(explosion, world, pos, state, power));
        if (cir.isCancellable()) {
            cir.cancel();
        }
    }
}
