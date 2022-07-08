package com.terriblefriends.serverutilities.mixin.command;

import com.terriblefriends.serverutilities.ServerUtilities;
import net.minecraft.entity.mob.PhantomEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PhantomEntity.StartAttackGoal.class)
public class PhantomEntityMixin {

    @Inject(at=@At("HEAD"),method="canStart",cancellable = true)
    private void removePhantomAggression(CallbackInfoReturnable<Boolean> cir) {
        if (ServerUtilities.server.getOverworld().getGameRules().getBoolean(ServerUtilities.LOBOTOMIZE_PHANTOMS) && cir.isCancellable()) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}
