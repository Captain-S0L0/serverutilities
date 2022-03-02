package com.terriblefriends.serverutilities.mixin;

import com.mojang.authlib.GameProfile;
import com.terriblefriends.serverutilities.access.PlayerManagerAccess;
import net.minecraft.server.OperatorEntry;
import net.minecraft.server.OperatorList;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.UUID;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin implements PlayerManagerAccess {
    @Shadow OperatorList ops;
    @Shadow abstract ServerPlayerEntity getPlayer(UUID uuid);
    @Shadow abstract void sendCommandTree(ServerPlayerEntity player);

    public void addToOperatorsWithPower(GameProfile profile, int power) { //allows /opwp to work
        this.ops.add(new OperatorEntry(profile, power, false));
        ServerPlayerEntity serverPlayerEntity = this.getPlayer(profile.getId());
        if (serverPlayerEntity != null) {
            this.sendCommandTree(serverPlayerEntity);
        }
    }
}
