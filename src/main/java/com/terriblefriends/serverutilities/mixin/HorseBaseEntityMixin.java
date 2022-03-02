package com.terriblefriends.serverutilities.mixin;

import com.terriblefriends.serverutilities.ServerUtilities;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HorseBaseEntity.class)
public abstract class HorseBaseEntityMixin extends LivingEntity {

    protected HorseBaseEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method="Lnet/minecraft/entity/passive/HorseBaseEntity;getChildMovementSpeedBonus()D",at=@At("HEAD"),cancellable = true)
    private void movementMixin(CallbackInfoReturnable<Double> cir) {
        cir.setReturnValue((0.44999998807907104D + this.random.nextDouble() * 0.3D + this.random.nextDouble() * 0.3D + this.random.nextDouble() * 0.3D) * 0.25D * this.world.getGameRules().getInt(ServerUtilities.HORSE_SPEED_MULTIPLIER));
        if (cir.isCancellable()) {cir.cancel();}
    }

    @Inject(method="Lnet/minecraft/entity/passive/HorseBaseEntity;writeCustomDataToNbt(Lnet/minecraft/nbt/NbtCompound;)V",at=@At("TAIL"))
    private void NBTWriteMixin(NbtCompound nbt, CallbackInfo ci) {
        if (this.getAttributes().getValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) > this.world.getGameRules().getInt(ServerUtilities.HORSE_SPEED_MULTIPLIER)*0.3375) {
            this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(this.world.getGameRules().getInt(ServerUtilities.HORSE_SPEED_MULTIPLIER)*0.3375);
            nbt.put("Attributes", this.getAttributes().toNbt());
        }
    }
}
