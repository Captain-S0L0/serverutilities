package com.terriblefriends.serverutilities.mixin.shadowfix.player;

import com.terriblefriends.serverutilities.access.WorldSaveHandlerAccessor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.util.Util;
import net.minecraft.world.WorldSaveHandler;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.io.File;

@Mixin(WorldSaveHandler.class)
public class WorldSaveHandlerMixin implements WorldSaveHandlerAccessor {
    @Shadow @Final private File playerDataDir;
    @Shadow @Final private static Logger LOGGER;

    public void savePlayerDataFromNbt(PlayerEntity player, NbtCompound nbtCompound) {
        try {
            File file = File.createTempFile(player.getUuidAsString() + "-", ".dat", playerDataDir);
            NbtIo.writeCompressed(nbtCompound, file);
            File file2 = new File(playerDataDir, player.getUuidAsString() + ".dat");
            File file3 = new File(playerDataDir, player.getUuidAsString() + ".dat_old");
            Util.backupAndReplace(file2, file, file3);
        } catch (Exception var6) {
            LOGGER.warn("Failed to save player data for {}", player.getName().getString());
            StackTraceElement[] stack = var6.getStackTrace();
            for (StackTraceElement e : stack) {
                LOGGER.warn(e.toString());
            }
        }
    }
}
