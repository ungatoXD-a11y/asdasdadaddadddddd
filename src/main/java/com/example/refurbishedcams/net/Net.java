package com.example.refurbishedcams.net;

import com.example.refurbishedcams.RefurbishedCams;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class Net {
    private static final String VERSION = "1";
    public static final SimpleChannel CH = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(RefurbishedCams.MODID, "main"), () -> VERSION, VERSION::equals, VERSION::equals);

    public static void register() {
        int id = 0;
        CH.registerMessage(id++, ScanRequestPacket.class, ScanRequestPacket::encode, ScanRequestPacket::decode, ScanRequestPacket::handle);
        CH.registerMessage(id++, ScanResultPacket.class, ScanResultPacket::encode, ScanResultPacket::decode, ScanResultPacket::handle);
    }
}
