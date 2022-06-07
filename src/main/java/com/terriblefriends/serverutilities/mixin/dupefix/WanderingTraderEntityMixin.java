package com.terriblefriends.serverutilities.mixin.dupefix;

import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.village.TradeOffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WanderingTraderEntity.class)
public abstract class WanderingTraderEntityMixin {
    @Inject(method="afterUsing(Lnet/minecraft/village/TradeOffer;)V", at=@At("HEAD"), cancellable = true)
    private void afterUsing(TradeOffer offer, CallbackInfo cir) { //this fixes void trading generating xp, just because it pisses me off.
        WanderingTraderEntity traderEntity1 = (WanderingTraderEntity) (Object) this;
        if (traderEntity1.isRemoved()) {
            cir.cancel();
        }
    }
}