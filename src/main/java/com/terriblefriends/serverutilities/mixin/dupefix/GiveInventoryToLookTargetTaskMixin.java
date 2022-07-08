package com.terriblefriends.serverutilities.mixin.dupefix;

import net.minecraft.entity.InventoryOwner;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.task.GiveInventoryToLookTargetTask;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GiveInventoryToLookTargetTask.class)
public class GiveInventoryToLookTargetTaskMixin<E extends LivingEntity & InventoryOwner> {
    @Inject(at=@At("HEAD"),method="Lnet/minecraft/entity/ai/brain/task/GiveInventoryToLookTargetTask;hasItemAndTarget(Lnet/minecraft/entity/LivingEntity;)Z",cancellable = true)
    private void antiDupeTest(E entity, CallbackInfoReturnable<Boolean> cir) {
        if (entity.isRemoved() && cir.isCancellable()) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}
