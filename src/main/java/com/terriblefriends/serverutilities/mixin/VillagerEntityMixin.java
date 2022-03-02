package com.terriblefriends.serverutilities.mixin;

import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.village.TradeOffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin {
    @Inject(method="afterUsing(Lnet/minecraft/village/TradeOffer;)V", at=@At("HEAD"), cancellable = true)
    private void afterUsing(TradeOffer offer, CallbackInfo cir) { //this fixes void trading generating xp, just because it pisses me off.
        VillagerEntity villagerEntity1 = (VillagerEntity) (Object) this;
        if (villagerEntity1.isRemoved()) {
            cir.cancel();
        }
    }
}
