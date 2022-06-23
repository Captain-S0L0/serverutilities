package com.terriblefriends.serverutilities.mixin.misc;

/*import net.minecraft.SharedConstants;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerMetadata;
import net.minecraft.server.ServerNetworkIo;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.profiling.jfr.FlightProfiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    private long tickReference;
    private long nextTickTimestamp;
    private MinecraftServer server;

    @Redirect(at=@At(value="INVOKE",target="Lnet/minecraft/server/ServerNetworkIo;tick()V"),method="tickWorlds")
    private void cancelOldNetworkTick(ServerNetworkIo instance) {
    }

    @Redirect(at=@At(value = "INVOKE",target="Lnet/minecraft/server/MinecraftServer;setFavicon(Lnet/minecraft/server/ServerMetadata;)V"),method="runServer")
    private void injectNewNetworkThread(MinecraftServer instance, ServerMetadata metadata) {
        if (server == null) {
            server = instance;
        }
        instance.setFavicon(metadata);
        Thread networking = new Thread(() -> {
            try {
                this.tickReference = Util.getMeasuringTimeMs();
                this.nextTickTimestamp = Util.getMeasuringTimeMs();
                while(instance.isRunning()) {
                    if (this.nextTickTimestamp <= Util.getMeasuringTimeMs()) {
                        this.tickReference = Util.getMeasuringTimeMs();
                        server.getNetworkIo().tick();
                        this.nextTickTimestamp = Math.max(Util.getMeasuringTimeMs(), this.tickReference + 50L);
                        System.out.println(this.tickReference + 50L-Util.getMeasuringTimeMs());
                    }

                }
            } catch (Throwable var44) {
                System.out.println("Encountered an unexpected exception");
            }
        }, "Network thread");
        networking.start();
    }
}*/
