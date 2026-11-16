package com.example.refurbishedcams;

import com.example.refurbishedcams.block.CameraBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRegistry {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, RefurbishedCams.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, RefurbishedCams.MODID);

    public static final RegistryObject<Block> CAMERA = BLOCKS.register("security_camera",
            () -> new CameraBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY)
                    .strength(1.5F).sound(SoundType.METAL).noOcclusion()));

    public static final RegistryObject<Item> CAMERA_ITEM = ITEMS.register("security_camera",
            () -> new BlockItem(CAMERA.get(), new Item.Properties()));
}
