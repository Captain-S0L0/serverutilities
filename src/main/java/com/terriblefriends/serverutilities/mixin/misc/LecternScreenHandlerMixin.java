package com.terriblefriends.serverutilities.mixin.misc;

import net.minecraft.screen.LecternScreenHandler;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LecternScreenHandler.class)
public class LecternScreenHandlerMixin {
    //patch lectern crash hack
    public boolean isValid(int slot) {
        return false;
    }
}
