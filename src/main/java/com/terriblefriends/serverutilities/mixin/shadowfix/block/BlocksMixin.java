package com.terriblefriends.serverutilities.mixin.shadowfix.block;

import net.minecraft.block.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(Blocks.class)
public abstract class BlocksMixin {
    @ModifyArg(
            slice = @Slice(
                    from = @At(value = "CONSTANT", args = "stringValue=spawner")
            ),
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/Blocks;register(Ljava/lang/String;Lnet/minecraft/block/Block;)Lnet/minecraft/block/Block;",
                    ordinal = 0
            ),
            index = 1)
    private static Block spawnerCode(Block block) {
        ((SetTickingInterface)block).setRandomTicks(true);
        return block;
    }

    /*@ModifyArg(
            slice = @Slice(
                    from = @At(value = "CONSTANT", args = "stringValue=chest")
            ),
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/Blocks;register(Ljava/lang/String;Lnet/minecraft/block/Block;)Lnet/minecraft/block/Block;",
                    ordinal = 0
            ),
            index = 1)
    private static Block chestCode(Block block) {
        ((SetTickingInterface)block).setRandomTicks(true);
        return block;
    }*/
}
