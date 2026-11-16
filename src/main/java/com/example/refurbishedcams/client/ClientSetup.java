package com.example.refurbishedcams.client;

import com.example.refurbishedcams.RefurbishedCams;
import com.example.refurbishedcams.compat.CameraProgram;
import com.mrcrayfish.furniture.refurbished.computer.Display;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = RefurbishedCams.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent e) {
        // Binds the program to its UI (docs: Display.get().bind(Program.class, Graphics::new))
        e.enqueueWork(() -> Display.get().bind(CameraProgram.class, CameraGraphics::new));
    }
}
