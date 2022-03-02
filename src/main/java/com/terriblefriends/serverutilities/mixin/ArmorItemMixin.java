package com.terriblefriends.serverutilities.mixin;

import com.terriblefriends.serverutilities.ServerUtilities;
import com.terriblefriends.serverutilities.access.ServerPlayerEntityAccess;
import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ArmorItem.class)
public class ArmorItemMixin {
    @Inject(at=@At("HEAD"),method="dispenseArmor(Lnet/minecraft/util/math/BlockPointer;Lnet/minecraft/item/ItemStack;)Z",cancellable = true)
    private static void dispenseArmorMixin(BlockPointer pointer, ItemStack armor, CallbackInfoReturnable<Boolean> cir) { //prevent dispensers from putting armor on adventure mode
        BlockPos blockPos = pointer.getPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
        List<LivingEntity> list = pointer.getWorld().getEntitiesByClass(LivingEntity.class, new Box(blockPos), EntityPredicates.EXCEPT_SPECTATOR.and(new EntityPredicates.Equipable(armor)));
        if (list.isEmpty()) {
            cir.setReturnValue(false);
        } else {
            LivingEntity livingEntity = list.get(0);
            EquipmentSlot equipmentSlot = MobEntity.getPreferredEquipmentSlot(armor);
            if (!(livingEntity instanceof ServerPlayerEntity)) {
                if (!pointer.getWorld().getGameRules().getBoolean(ServerUtilities.ADVENTURE_GHOST_MODE) || !((ServerPlayerEntityAccess)livingEntity).isAdventure()){
                    ItemStack itemStack = armor.split(1);
                    livingEntity.equipStack(equipmentSlot, itemStack);
                }
            }
            if (livingEntity instanceof MobEntity) {
                ItemStack itemStack = armor.split(1);
                livingEntity.equipStack(equipmentSlot, itemStack);
                ((MobEntity) livingEntity).setEquipmentDropChance(equipmentSlot, 2.0F);
                ((MobEntity) livingEntity).setPersistent();
            }

            cir.setReturnValue(true);
        }
        if (cir.isCancellable()) {cir.cancel();}
    }
}
