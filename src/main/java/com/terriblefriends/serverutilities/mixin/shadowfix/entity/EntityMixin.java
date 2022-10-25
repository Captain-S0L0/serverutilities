package com.terriblefriends.serverutilities.mixin.shadowfix.entity;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.entity.EntityChangeListener;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin{
    Entity E_instance = (Entity) (Object) this;

    @Shadow private @Nullable Entity.RemovalReason removalReason;
    @Shadow private EntityChangeListener changeListener;

    @Shadow
    protected String getSavedEntityId() {
        System.out.println("this shouldn't run!!!");
        return null;
    }

    @Inject(method="setRemoved",at=@At("HEAD"),cancellable = true)
    private void setRemovedPassengerFix(Entity.RemovalReason reason, CallbackInfo ci) {
        if (removalReason == null) {
            removalReason = reason;
        }

        if (this.removalReason.shouldDestroy()) {
            E_instance.stopRiding();
            E_instance.getPassengerList().forEach(Entity::stopRiding);
        }

        changeListener.remove(reason);

        if (ci.isCancellable()) {ci.cancel();}
    }

    @Redirect(at=@At(value="INVOKE",target="Lnet/minecraft/entity/Entity;copyFrom(Lnet/minecraft/entity/Entity;)V"),method="moveToWorld")
    private void changeDimensionClearShadows(Entity instance, Entity original) {
        E_instance.setRemoved(Entity.RemovalReason.CHANGED_DIMENSION);
        instance.copyFrom(original);
    }

    @Inject(at=@At("HEAD"),method="Lnet/minecraft/entity/Entity;saveSelfNbt(Lnet/minecraft/nbt/NbtCompound;)Z",cancellable = true)
    private void forceWritePassengerNBT(NbtCompound nbt, CallbackInfoReturnable<Boolean> cir) {
        if (this.removalReason != null && !this.removalReason.shouldSave() && this.removalReason != Entity.RemovalReason.UNLOADED_WITH_PLAYER) {
            cir.setReturnValue(false);
        } else {
            String string = getSavedEntityId();
            if (string == null) {
                cir.setReturnValue(false);
            } else {
                nbt.putString("id", string);
                E_instance.writeNbt(nbt);
                cir.setReturnValue(true);
            }
        }
        if (cir.isCancellable()) {cir.cancel();}
    }
}
