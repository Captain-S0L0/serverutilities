package com.terriblefriends.serverutilities.mixin.command;

import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.network.message.MessageType;
import net.minecraft.server.command.MessageCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.Iterator;

@Mixin(MessageCommand.class)
public class MessageCommandMixin {
    @Inject(at=@At("HEAD"),method="execute",cancellable = true)
    private static void socialSpy(ServerCommandSource source, Collection<ServerPlayerEntity> targets, MessageArgumentType.SignedMessage signedMessage, CallbackInfoReturnable<Integer> cir) {
        if (targets.isEmpty()) {
            cir.setReturnValue(0);
        } else {
            signedMessage.decorate(source).thenAcceptAsync((decoratedMessage) -> {
                Text text = (decoratedMessage.raw()).getContent();
                Iterator var4 = targets.iterator();

                while(var4.hasNext()) {
                    ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)var4.next();
                    source.sendFeedback(Text.translatable("commands.message.display.outgoing", serverPlayerEntity.getDisplayName(), text).formatted(Formatting.GRAY, Formatting.ITALIC), true);
                    net.minecraft.network.message.SignedMessage signedMessage1 = decoratedMessage.getFilterableFor(source, serverPlayerEntity);
                    if (signedMessage1 != null) {
                        serverPlayerEntity.sendChatMessage(signedMessage1, source.getChatMessageSender(), MessageType.MSG_COMMAND);
                    }
                }

            }, source.getServer());
            cir.setReturnValue(targets.size());
            if (cir.isCancellable()) {cir.cancel();}
        }
    }
}
