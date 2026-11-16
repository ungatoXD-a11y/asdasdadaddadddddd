package com.example.refurbishedcams.net;

import com.example.refurbishedcams.client.ClientHooks;
import com.example.refurbishedcams.data.CameraEntry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/** Server -> client: the cameras found (and the radius used). */
public class ScanResultPacket {
    private final int radius;
    private final List<CameraEntry> cams;

    public ScanResultPacket(int radius, List<CameraEntry> cams) {
        this.radius = radius;
        this.cams = cams;
    }

    public static void encode(ScanResultPacket p, FriendlyByteBuf b) {
        b.writeVarInt(p.radius);
        b.writeVarInt(p.cams.size());
        for (CameraEntry c : p.cams) {
            b.writeBlockPos(c.pos());
            b.writeFloat(c.yaw());
        }
    }

    public static ScanResultPacket decode(FriendlyByteBuf b) {
        int radius = b.readVarInt();
        int n = b.readVarInt();
        List<CameraEntry> l = new ArrayList<>(n);
        for (int i = 0; i < n; i++) l.add(new CameraEntry(b.readBlockPos(), b.readFloat()));
        return new ScanResultPacket(radius, l);
    }

    public static void handle(ScanResultPacket p, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context c = ctx.get();
        c.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> ClientHooks.onScanResult(p.radius, p.cams)));
        c.setPacketHandled(true);
    }
}
