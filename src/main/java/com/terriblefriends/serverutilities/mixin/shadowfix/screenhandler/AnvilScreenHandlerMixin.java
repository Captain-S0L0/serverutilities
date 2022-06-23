package com.terriblefriends.serverutilities.mixin.shadowfix.screenhandler;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AnvilScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AnvilScreenHandler.class)
public class AnvilScreenHandlerMixin {
    @Redirect(at=@At(value="INVOKE",target="Lnet/minecraft/inventory/Inventory;setStack(ILnet/minecraft/item/ItemStack;)V"),method="onTakeOutput")
    private void removeShadowsOnAnvil(Inventory instance, int i, ItemStack itemStack) {
        if (itemStack == ItemStack.EMPTY) {
            instance.getStack(i).setCount(0);
        }
        instance.setStack(i,itemStack);
    }
}
