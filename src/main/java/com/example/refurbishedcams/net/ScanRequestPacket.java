package com.example.refurbishedcams.net;

import com.example.refurbishedcams.Config;
import com.example.refurbishedcams.data.CameraData;
import com.example.refurbishedcams.data.CameraEntry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.List;
import java.util.function.Supplier;

/** Client -> server: "scan for cameras around me". */
public class ScanRequestPacket {
    public static void encode(ScanRequestPacket p, FriendlyByteBuf b) {}

    public static ScanRequestPacket decode(FriendlyByteBuf b) {
        return new ScanRequestPacket();
    }

    public static void handle(ScanRequestPacket p, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context c = ctx.get();
        c.enqueueWork(() -> {
            ServerPlayer sp = c.getSender();
            if (sp == null) return;
            ServerLevel level = sp.serverLevel();
            List<CameraEntry> list = CameraData.get(level).scan(level, sp.blockPosition(), Config.RADIUS.get());
            Net.CH.send(PacketDistributor.PLAYER.with(() -> sp), new ScanResultPacket(Config.RADIUS.get(), list));
        });
        c.setPacketHandled(true);
    }
}
