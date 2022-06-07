package com.terriblefriends.serverutilities.mixin.command;

import com.terriblefriends.serverutilities.access.SetPlayerAbilitiesAccess;
import net.minecraft.entity.player.PlayerAbilities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PlayerAbilities.class)
public class SetPlayerAbilitiesMixin implements SetPlayerAbilitiesAccess {
    @Shadow public boolean allowFlying;
    @Shadow public boolean flying;
    @Shadow public boolean invulnerable;
    @Shadow private float flySpeed;
    @Shadow private float walkSpeed;

    //allows ability commands like /fly and /god to work

    public void setAllowFlying(boolean bool) {
        allowFlying = bool;
    }

    public void setFlying(boolean bool) {
        flying = bool;
    }

    public void setInvulnerable(boolean bool) {
        invulnerable = bool;
    }

    public void setFlySpeed(float flo) {
        flySpeed = flo;
    }

    public void setWalkSpeed(float flo) {
        walkSpeed = flo;
    }
}
