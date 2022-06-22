package com.terriblefriends.serverutilities.mixin.misc;

import net.minecraft.entity.passive.BeeEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BeeEntity.class)
public class BeeEntityMixin {
    BeeEntity BE_instance = (BeeEntity) (Object) this;
    @Shadow private int cannotEnterHiveTicks;
    @Shadow BeeEntity.PollinateGoal pollinateGoal;
    @Shadow private boolean failedPollinatingTooLong() {return false;}
    @Shadow private boolean isHiveNearFire() {return false;}

    @Inject(at=@At("HEAD"),method="canEnterHive",cancellable = true)
    private void beeFixNetherEndBug(CallbackInfoReturnable<Boolean> cir) {
        if (this.cannotEnterHiveTicks <= 0 && !this.pollinateGoal.isRunning() && !BE_instance.hasStung() && BE_instance.getTarget() == null) {
            boolean bl;
            if (BE_instance.world.getDimension().hasSkyLight()) {
                bl = this.failedPollinatingTooLong() || BE_instance.world.isRaining() || BE_instance.world.isNight() || BE_instance.hasNectar();
            }
            else {
                bl = this.failedPollinatingTooLong() || BE_instance.hasNectar();
            }
            cir.setReturnValue( bl && !this.isHiveNearFire());
        } else {
            cir.setReturnValue(false);
        }
        if (cir.isCancellable()) {cir.cancel();}
    }
}
