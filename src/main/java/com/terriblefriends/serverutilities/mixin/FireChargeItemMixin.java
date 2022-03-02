package com.terriblefriends.serverutilities.mixin;

import com.terriblefriends.serverutilities.ServerUtilities;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.item.FireChargeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = FireChargeItem.class)
public class FireChargeItemMixin extends Item{
    public FireChargeItemMixin(Settings settings) {
        super(settings);
    }

    FireChargeItem fci = (FireChargeItem) (Object) this;
    @Shadow private void playUseSound(World world, BlockPos pos) {};

    @Inject(method="useOnBlock(Lnet/minecraft/item/ItemUsageContext;)Lnet/minecraft/util/ActionResult;",at=@At("HEAD"),cancellable = true)
    private void useOnBlockMixin(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        PlayerEntity player = context.getPlayer();
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        BlockState blockState = world.getBlockState(blockPos);
        boolean bl = false;
        if (!CampfireBlock.canBeLit(blockState) && !CandleBlock.canBeLit(blockState) && !CandleCakeBlock.canBeLit(blockState)) {
            blockPos = blockPos.offset(context.getSide());
            if ((!world.getGameRules().getBoolean(ServerUtilities.THROWABLE_FIREBALLS) || player.isSneaking()) && AbstractFireBlock.canPlaceAt(world, blockPos, context.getPlayerFacing())) { //keep old fire creation mechanic by shift clicking
                playUseSound(world, blockPos);
                world.setBlockState(blockPos, AbstractFireBlock.getState(world, blockPos));
                world.emitGameEvent(context.getPlayer(), GameEvent.BLOCK_PLACE, blockPos);
                bl = true;
            }
            else if (!player.isSneaking() && world.getGameRules().getBoolean(ServerUtilities.THROWABLE_FIREBALLS)) { //throwable fireballs, baby!
                playUseSound(world, blockPos);
                createFireball(player, world);
                bl = true;
            }
        }

        if (bl) {
            context.getStack().decrement(1);
            cir.setReturnValue(ActionResult.success(world.isClient));
        } else {
            cir.setReturnValue(ActionResult.FAIL);
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (!player.isSneaking() && world.getGameRules().getBoolean(ServerUtilities.THROWABLE_FIREBALLS)) {
            playUseSound(world, player.getBlockPos());
            createFireball(player, world);
            itemStack.decrement(1);
            return TypedActionResult.success(itemStack, world.isClient());
        }
        else {return TypedActionResult.fail(itemStack);}
    }

    private void createFireball(PlayerEntity player, World world) { //shitty spawn code, it probably could be better, but it works
        Vec3d vec3d = player.getRotationVec(1.0F);
        FireballEntity fireballEntity = new FireballEntity(world, player, vec3d.getX(),vec3d.getY(),vec3d.getZ() , world.getGameRules().getInt(ServerUtilities.THROWABLE_FIREBALL_POWER));
        fireballEntity.setPosition(player.getX() + vec3d.x, player.getEyeY(), fireballEntity.getZ() + vec3d.z);
        world.spawnEntity(fireballEntity);
        player.getItemCooldownManager().set(fci, 10);
    }
}
