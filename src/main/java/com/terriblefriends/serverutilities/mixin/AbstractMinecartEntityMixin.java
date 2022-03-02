package com.terriblefriends.serverutilities.mixin;

import com.terriblefriends.serverutilities.ServerUtilities;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractMinecartEntity.class)
public abstract class AbstractMinecartEntityMixin extends Entity {
    AbstractMinecartEntity ame = (AbstractMinecartEntity) (Object) this;

    public AbstractMinecartEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method="getVelocityMultiplier()F",at=@At("HEAD"),cancellable = true)
    private void velocityMixin(CallbackInfoReturnable<Float> cir) { //this allows for making minecarts faster based on gamerules
        BlockState blockState = ame.getWorld().getBlockState(ame.getBlockPos());
        cir.setReturnValue(blockState.isIn(BlockTags.RAILS) ? this.world.getGameRules().getInt(ServerUtilities.MINECART_SPEED_MULTIPLIER) : super.getVelocityMultiplier());
        if (cir.isCancellable()) {cir.cancel();}
    }
}
