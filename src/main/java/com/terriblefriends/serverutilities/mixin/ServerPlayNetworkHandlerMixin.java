package com.terriblefriends.serverutilities.mixin;

import com.terriblefriends.serverutilities.ServerUtilities;
import com.terriblefriends.serverutilities.access.ServerPlayerEntityAccess;
import net.minecraft.client.option.ChatVisibility;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.Packet;
import net.minecraft.network.message.MessageDecorator;
import net.minecraft.network.message.MessageSignature;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.filter.FilteredMessage;
import net.minecraft.server.filter.TextStream;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

import static net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action.DROP_ITEM;
import static net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action.DROP_ALL_ITEMS;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {
    ServerPlayNetworkHandler spnh = (ServerPlayNetworkHandler) (Object) this;
    private int distantBlockInteractions;
    private boolean hasLoggedWarning;
    @Shadow public ServerPlayerEntity player;
    @Shadow public void disconnect(Text textComponent){}
    @Shadow @Final static Logger LOGGER;
    @Shadow public void sendPacket(Packet<?> packet){}
    @Shadow @Final private MinecraftServer server;
    @Shadow private int messageCooldown;

    @Inject(at=@At("HEAD"),method="onPlayerInteractEntity(Lnet/minecraft/network/packet/c2s/play/PlayerInteractEntityC2SPacket;)V",cancellable = true)
    private void onPlayerInteractEntityMixin(PlayerInteractEntityC2SPacket packet, CallbackInfo ci) { //no interacting with or attacking in adventure mode
        if (player.getWorld().getGameRules().getBoolean(ServerUtilities.ADVENTURE_GHOST_MODE) && ((ServerPlayerEntityAccess)spnh.player).isAdventure() && ci.isCancellable()) {ci.cancel();}
    }
    @Inject(at=@At("HEAD"),method="onPlayerAction(Lnet/minecraft/network/packet/c2s/play/PlayerActionC2SPacket;)V",cancellable = true)
    private void onPlayerActionMixin(PlayerActionC2SPacket packet, CallbackInfo ci) { //no dropping items out in adventure mode
        PlayerActionC2SPacket.Action action = packet.getAction();
        if (player.getWorld().getGameRules().getBoolean(ServerUtilities.ADVENTURE_GHOST_MODE) && ((ServerPlayerEntityAccess)spnh.player).isAdventure() && (action == DROP_ITEM || action == DROP_ALL_ITEMS) && ci.isCancellable()) {
            spnh.player.currentScreenHandler.updateToClient();
            ci.cancel();
        }
    }

    @Redirect(method="onPlayerInteractBlock",at=@At(value="INVOKE",target="Lnet/minecraft/server/world/ServerWorld;canPlayerModifyAt(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/math/BlockPos;)Z"))
    private boolean spawnProtectionFix(ServerWorld serverWorld, PlayerEntity player, BlockPos pos) {
        if (player.getMainHandStack().getItem() == Items.AIR && player.getOffHandStack().getItem() == Items.AIR) { //so you can open ender chests at spawn
            return true;
        }
        else return !serverWorld.getServer().isSpawnProtected(serverWorld, pos, player) && serverWorld.getWorldBorder().contains(pos);
    }

    @Inject(method="onPlayerInteractBlock",at=@At("HEAD"),cancellable = true)
    private void onPlayerInteractBlockMixin(PlayerInteractBlockC2SPacket packet, CallbackInfo ci) { //nocomcrash patch
        if (player.getBlockPos().getManhattanDistance(packet.getBlockHitResult().getBlockPos()) > 32) {
            distantBlockInteractions++;
            if (distantBlockInteractions > 400 && !hasLoggedWarning) {
                LOGGER.warn(player.getName().getString() + " might be trying to crash the server using the nocom exploit.");
                hasLoggedWarning = true;
            }
            ci.cancel();
        } else {
            distantBlockInteractions--;
        }
    }
    @Shadow private boolean checkChatEnabled() {return false;}
    @Shadow private void handleDecoratedMessage(FilteredMessage<SignedMessage> message) {}

    /*@Inject(method="handleMessage",at=@At("HEAD"),cancellable = true)
    private void handleMessageMixin(ChatMessageC2SPacket packet, FilteredMessage<String> message, CallbackInfo ci) { // implement clickable links into chat
        if (this.checkChatEnabled()) {
            MessageSignature messageSignature = packet.createSignatureInstance(this.player.getUuid());
            boolean bl = packet.isPreviewed();
            MessageDecorator messageDecorator = this.server.getMessageDecorator();

            messageDecorator.decorateChat(this.player, message.map(Text::literal), messageSignature, bl).thenAcceptAsync(this::handleDecoratedMessage, this.server);
        }
        if (ci.isCancellable()) {ci.cancel();}
    }*/

    /*if (this.player.getClientChatVisibility() == ChatVisibility.HIDDEN) {
            this.sendPacket(new GameMessageS2CPacket((new TranslatableText("chat.disabled.options")).formatted(Formatting.RED), MessageType.SYSTEM, Util.NIL_UUID));
        } else {
            this.player.updateLastActionTime();
            String string = message.getRaw();
            if (string.startsWith("/")) {
                this.executeCommand(string);
            } else {
                String string2 = message.getFiltered();
                String[] httpcheck = string2.split(" ");
                String link = null;
                for (int check=0;check<httpcheck.length;check++){
                    if (httpcheck[check].contains("http")) {
                        link = httpcheck[check];
                    }
                }
                Text text = string2.isEmpty() ? null : new TranslatableText("chat.type.text", this.player.getDisplayName(), string2);
                Text text2 = new TranslatableText("chat.type.text", this.player.getDisplayName(), string);
                if (link != null) {
                    text = text.copy().setStyle(text.getStyle().withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, link)));
                    text2 = text2.copy().setStyle(text.getStyle().withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,link)));
                }
                Text finalText = text;
                Text finalText1 = text2;
                this.server.getPlayerManager().broadcast(text2, (player) -> {
                    return this.player.shouldFilterMessagesSentTo(player) ? finalText : finalText1;
                }, MessageType.CHAT, this.player.getUuid());
            }

            this.messageCooldown += 20;
            if (this.messageCooldown > 200 && !this.server.getPlayerManager().isOperator(this.player.getGameProfile())) {
                this.disconnect(new TranslatableText("disconnect.spam"));
            }

        }*/

}
