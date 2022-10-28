package com.terriblefriends.serverutilities.mixin.dupefix;

import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemFrameEntity.class)
public class ItemFrameEntityMixin {
    ItemFrameEntity IFE_instance = (ItemFrameEntity) (Object) this;
    @Shadow private boolean fixed;


    @Inject(at=@At("HEAD"),method="Lnet/minecraft/entity/decoration/ItemFrameEntity;interact(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;",cancellable = true)
    private void setHeldStackReorder(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        ItemStack itemStack = player.getStackInHand(hand);
        boolean bl = !IFE_instance.getHeldItemStack().isEmpty();
        boolean bl2 = !itemStack.isEmpty();
        if (fixed) {
            cir.setReturnValue(ActionResult.PASS);
        } else if (!IFE_instance.world.isClient) {
            if (!bl) {
                if (bl2 && !IFE_instance.isRemoved()) {
                    if (itemStack.isOf(Items.FILLED_MAP)) {
                        MapState mapState = FilledMapItem.getOrCreateMapState(itemStack, IFE_instance.world);
                        if (mapState != null && mapState.method_37343(256)) {
                            cir.setReturnValue(ActionResult.FAIL);
                            if (cir.isCancellable()) {cir.cancel();}
                        }
                    }
                    ItemStack copyStack = itemStack.copy();
                    if (!player.getAbilities().creativeMode) {
                        itemStack.decrement(1);
                    }
                    IFE_instance.setHeldItemStack(copyStack);
                }
            } else {
                IFE_instance.playSound(IFE_instance.getRotateItemSound(), 1.0F, 1.0F);
                IFE_instance.setRotation(IFE_instance.getRotation() + 1);
            }

            cir.setReturnValue(ActionResult.CONSUME);
        } else {
            cir.setReturnValue(!bl && !bl2 ? ActionResult.PASS : ActionResult.SUCCESS);
        }
        if (cir.isCancellable()) {cir.cancel();}
    }
}
