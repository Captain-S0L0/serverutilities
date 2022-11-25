package com.terriblefriends.serverutilities.mixin.command;

import com.mojang.authlib.GameProfile;
import com.terriblefriends.serverutilities.ServerUtilities;
import com.terriblefriends.serverutilities.config.Config;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.server.BannedPlayerEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Date;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
    ServerPlayNetworkHandler spnh = (ServerPlayNetworkHandler) (Object) this;
    @Shadow public ServerPlayerEntity player;
    @Shadow @Final private MinecraftServer server;

    private static final String BAN_REASON = "You died! Game over man, game over.";

    @Inject(at=@At("HEAD"),method="Lnet/minecraft/server/network/ServerPlayNetworkHandler;onClientStatus(Lnet/minecraft/network/packet/c2s/play/ClientStatusC2SPacket;)V",cancellable = true)
    private void hardcoreBanMixin(ClientStatusC2SPacket packet, CallbackInfo ci) {
        NetworkThreadUtils.forceMainThread(packet, spnh, this.player.getWorld());
        this.player.updateLastActionTime();
        ClientStatusC2SPacket.Mode mode = packet.getMode();
        switch (mode) {
            case PERFORM_RESPAWN:
                if (this.player.notInAnyWorld) {
                    this.player.notInAnyWorld = false;
                    this.player = this.server.getPlayerManager().respawnPlayer(this.player, true);
                    Criteria.CHANGED_DIMENSION.trigger(this.player, World.END, World.OVERWORLD);
                } else {
                    if (this.player.getHealth() > 0.0F) {
                        return;
                    }

                    this.player = this.server.getPlayerManager().respawnPlayer(this.player, false);
                    if (this.server.isHardcore()) {
                        if (this.player.getWorld().getGameRules().get(ServerUtilities.HARDCORE_DEATH_BAN).get()) {
                            Date expiry;
                            if (this.player.getWorld().getGameRules().get(ServerUtilities.HARDCORE_DEATH_BAN_DURATION).get() == 0) {
                                expiry = null;
                            }
                            else {
                                expiry = new Date(System.currentTimeMillis()+(this.player.getWorld().getGameRules().get(ServerUtilities.HARDCORE_DEATH_BAN_DURATION).get()* 86400000L));
                            }
                            BannedPlayerEntry newBan = new BannedPlayerEntry(new GameProfile(this.player.getUuid(), null),new Date(System.currentTimeMillis()), "HARDCORE", expiry,BAN_REASON);
                            this.server.getPlayerManager().getUserBanList().add(newBan);
                            this.player.networkHandler.disconnect(Text.literal(BAN_REASON));
                        }
                        else {
                            this.player.changeGameMode(GameMode.SPECTATOR);
                        }
                        //this.player.getWorld().getGameRules().get(GameRules.SPECTATORS_GENERATE_CHUNKS).set(false, this.server);
                    }
                }
                break;
            case REQUEST_STATS:
                this.player.getStatHandler().sendStats(this.player);
        }
        if (ci.isCancellable()) {ci.cancel();}
    }
}
