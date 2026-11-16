package com.example.refurbishedcams.compat;

import com.example.refurbishedcams.RefurbishedCams;
import com.mrcrayfish.furniture.refurbished.blockentity.IComputer;
import com.mrcrayfish.furniture.refurbished.computer.Computer;
import com.mrcrayfish.furniture.refurbished.computer.Program;
import net.minecraft.resources.ResourceLocation;

/**
 * "Cameras" program for MrCrayfish's Furniture Mod: Refurbished Computer.
 * All the logic lives in packets (net/) and in the client UI (client/CameraGraphics).
 */
public class CameraProgram extends Program {
    public static final ResourceLocation ID = new ResourceLocation(RefurbishedCams.MODID, "cameras");

    public CameraProgram(ResourceLocation id, IComputer computer) {
        super(id, computer);
    }

    public static void install() {
        Computer.get().installProgram(ID, CameraProgram::new);
    }
}
