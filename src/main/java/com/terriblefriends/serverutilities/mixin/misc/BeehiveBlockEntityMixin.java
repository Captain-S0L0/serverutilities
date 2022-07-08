package com.terriblefriends.serverutilities.mixin.misc;

import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BeehiveBlockEntity.class)
public class BeehiveBlockEntityMixin {
    @Redirect(at=@At(value="INVOKE",target="Lnet/minecraft/world/World;isRaining()Z"),method="releaseBee")
    private static boolean beeFixNetherEndRainBug(World instance) {
        if (!instance.getDimension().hasSkyLight()) {
            return false;
        }
        else {
            return instance.isRaining();
        }
    }
}
