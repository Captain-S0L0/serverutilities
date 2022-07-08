package com.terriblefriends.serverutilities.mixin.ghost;

import com.terriblefriends.serverutilities.ServerUtilities;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameMode.class)
public abstract class GameModeMixin {
    @Shadow abstract boolean isBlockBreakingRestricted();
    GameMode gm = (GameMode) (Object) this;
    @Inject(at=@At("HEAD"),method="setAbilities(Lnet/minecraft/entity/player/PlayerAbilities;)V",cancellable = true)
    private void setAbilitiesMixin(PlayerAbilities abilities, CallbackInfo ci) {//make it so adventure mode players have invulnerable = true
        if (gm == GameMode.CREATIVE) {
            abilities.allowFlying = true;
            abilities.creativeMode = true;
            abilities.invulnerable = true;
        } else if (gm == GameMode.SPECTATOR) {
            abilities.allowFlying = true;
            abilities.creativeMode = false;
            abilities.invulnerable = true;
            abilities.flying = true;
        } else if (gm == GameMode.ADVENTURE && ServerUtilities.server.getOverworld().getGameRules().getBoolean(ServerUtilities.ADVENTURE_GHOST_MODE)) {
            abilities.allowFlying = false;
            abilities.creativeMode = false;
            abilities.invulnerable = true;
            abilities.flying = false;
        } else if (gm == GameMode.ADVENTURE && !ServerUtilities.server.getOverworld().getGameRules().getBoolean(ServerUtilities.ADVENTURE_GHOST_MODE)) {
            abilities.allowFlying = false;
            abilities.creativeMode = false;
            abilities.invulnerable = false;
            abilities.flying = false;
        } else {
            abilities.allowFlying = false;
            abilities.creativeMode = false;
            abilities.invulnerable = false;
            abilities.flying = false;
        }

        abilities.allowModifyWorld = !this.isBlockBreakingRestricted();
        if (ci.isCancellable()) {ci.cancel();}
    }
}
