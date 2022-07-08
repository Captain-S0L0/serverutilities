package com.terriblefriends.serverutilities.mixin.shadowfix.block;

import net.minecraft.block.AbstractBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractBlock.class)
public interface SetTickingInterface {
    @Accessor
    @Mutable
    void setRandomTicks(boolean bool);
}
