package com.example.refurbishedcams;

import com.example.refurbishedcams.compat.CameraProgram;
import com.example.refurbishedcams.net.Net;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(RefurbishedCams.MODID)
public class RefurbishedCams {
    public static final String MODID = "refurbishedcams";

    public RefurbishedCams() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModRegistry.BLOCKS.register(bus);
        ModRegistry.ITEMS.register(bus);
        bus.addListener(this::commonSetup);
        bus.addListener(this::buildTabs);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent e) {
        e.enqueueWork(() -> {
            Net.register();
            CameraProgram.install(); // adds the "Cameras" program to Refurbished's Computer
        });
    }

    private void buildTabs(BuildCreativeModeTabContentsEvent e) {
        if (e.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            e.accept(ModRegistry.CAMERA_ITEM);
        }
    }
}
