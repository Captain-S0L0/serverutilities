package com.terriblefriends.serverutilities.mixin.shadowfix.screenhandler;

import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.GrindstoneScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Iterator;
import java.util.Map;

@Mixin(GrindstoneScreenHandler.class)
public class GrindstoneScreenHandlerMixin extends ScreenHandler {
    @Shadow @Final Inventory input;
    @Shadow @Final private ScreenHandlerContext context;

    protected GrindstoneScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId) {
        super(type, syncId);
    }

    @Redirect(at=@At(value="INVOKE" ,target="Lnet/minecraft/screen/GrindstoneScreenHandler;addSlot(Lnet/minecraft/screen/slot/Slot;)Lnet/minecraft/screen/slot/Slot;"),method="<init>(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/screen/ScreenHandlerContext;)V")
    private Slot removeShadowsOnRepair(GrindstoneScreenHandler instance, Slot slot) {
        if (!slot.canInsert(new ItemStack(Items.ENCHANTED_BOOK,1))) {
            this.addSlot(new Slot(slot.inventory,slot.getIndex(),slot.x,slot.y) {
                public boolean canInsert(ItemStack stack) {
                    return false;
                }

                public void onTakeItem(PlayerEntity player, ItemStack stack) {
                    context.run((world, pos) -> {
                        if (world instanceof ServerWorld) {
                            ExperienceOrbEntity.spawn((ServerWorld)world, Vec3d.ofCenter(pos), this.getExperience(world));
                        }

                        world.syncWorldEvent(1042, pos, 0);
                    });
                    input.getStack(0).setCount(0);
                    input.getStack(1).setCount(0);
                    input.setStack(0, ItemStack.EMPTY);
                    input.setStack(1, ItemStack.EMPTY);
                }

                private int getExperience(World world) {
                    int ix = 0;
                    int i = ix + this.getExperience(input.getStack(0));
                    i += this.getExperience(input.getStack(1));
                    if (i > 0) {
                        int j = (int)Math.ceil((double)i / 2.0D);
                        return j + world.random.nextInt(j);
                    } else {
                        return 0;
                    }
                }

                private int getExperience(ItemStack stack) {
                    int i = 0;
                    Map<Enchantment, Integer> map = EnchantmentHelper.get(stack);
                    Iterator var4 = map.entrySet().iterator();

                    while(var4.hasNext()) {
                        Map.Entry<Enchantment, Integer> entry = (Map.Entry)var4.next();
                        Enchantment enchantment = entry.getKey();
                        Integer integer = entry.getValue();
                        if (!enchantment.isCursed()) {
                            i += enchantment.getMinPower(integer);
                        }
                    }

                    return i;
                }
            });
        }
        else {
            this.addSlot(slot);
        }
        return null;
    }

    public ItemStack transferSlot(PlayerEntity player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        ItemStack originalStack = slot.getStack();
        slot.setStack(originalStack.copy());
        originalStack.setCount(0);
        if (slot != null && slot.hasStack()) {
            ItemStack itemStack2 = slot.getStack();
            itemStack = itemStack2.copy();
            ItemStack itemStack3 = this.input.getStack(0);
            ItemStack itemStack4 = this.input.getStack(1);
            if (index == 2) {
                if (!this.insertItem(itemStack2, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickTransfer(itemStack2, itemStack);
            } else if (index != 0 && index != 1) {
                if (!itemStack3.isEmpty() && !itemStack4.isEmpty()) {
                    if (index >= 3 && index < 30) {
                        if (!this.insertItem(itemStack2, 30, 39, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (index >= 30 && index < 39 && !this.insertItem(itemStack2, 3, 30, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.insertItem(itemStack2, 0, 2, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(itemStack2, 3, 39, false)) {
                return ItemStack.EMPTY;
            }

            if (itemStack2.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }

            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTakeItem(player, itemStack2);
        }

        return itemStack;
    }

    public boolean canUse(PlayerEntity player) {
        return canUse(this.context, player, Blocks.GRINDSTONE);
    }
}
