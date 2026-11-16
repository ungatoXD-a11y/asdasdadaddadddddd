package com.example.refurbishedcams;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.IntValue RADIUS;

    static {
        ForgeConfigSpec.Builder b = new ForgeConfigSpec.Builder();
        RADIUS = b.comment("Detection radius (blocks) of the Security Computer app")
                .defineInRange("detectionRadius", 500, 16, 5000);
        SPEC = b.build();
    }
}
