package com.example.refurbishedcams.data;

import net.minecraft.core.BlockPos;

/** A detected camera: position + the yaw it is looking at. */
public record CameraEntry(BlockPos pos, float yaw) {}
