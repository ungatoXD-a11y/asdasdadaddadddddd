package com.example.refurbishedcams.data;

import com.example.refurbishedcams.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.*;

/** Per-dimension registry of every placed camera (persisted with the world). */
public class CameraData extends SavedData {
    private static final String NAME = "refurbishedcams_cameras";
    private static final int MAX_RESULTS = 64;

    private final Map<BlockPos, Direction> cameras = new HashMap<>();

    public static CameraData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(CameraData::load, CameraData::new, NAME);
    }

    public static CameraData load(CompoundTag tag) {
        CameraData d = new CameraData();
        ListTag list = tag.getList("cams", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag t = list.getCompound(i);
            d.cameras.put(BlockPos.of(t.getLong("p")), Direction.from2DDataValue(t.getByte("f")));
        }
        return d;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag list = new ListTag();
        for (Map.Entry<BlockPos, Direction> e : cameras.entrySet()) {
            CompoundTag t = new CompoundTag();
            t.putLong("p", e.getKey().asLong());
            t.putByte("f", (byte) e.getValue().get2DDataValue());
            list.add(t);
        }
        tag.put("cams", list);
        return tag;
    }

    public void add(BlockPos pos, Direction facing) {
        cameras.put(pos.immutable(), facing);
        setDirty();
    }

    public void remove(BlockPos pos) {
        if (cameras.remove(pos) != null) setDirty();
    }

    /** Cameras within {@code radius} blocks (3D) of center, closest first. */
    public List<CameraEntry> scan(ServerLevel level, BlockPos center, int radius) {
        double r2 = (double) radius * radius;
        List<Map.Entry<BlockPos, Direction>> found = new ArrayList<>();
        Iterator<Map.Entry<BlockPos, Direction>> it = cameras.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<BlockPos, Direction> e = it.next();
            BlockPos p = e.getKey();
            // Clean stale entries (explosions, /setblock...) when the chunk is loaded
            if (level.hasChunkAt(p) && !level.getBlockState(p).is(ModRegistry.CAMERA.get())) {
                it.remove();
                setDirty();
                continue;
            }
            if (p.distSqr(center) <= r2) found.add(e);
        }
        found.sort(Comparator.comparingDouble(e -> e.getKey().distSqr(center)));
        List<CameraEntry> out = new ArrayList<>();
        for (Map.Entry<BlockPos, Direction> e : found) {
            if (out.size() >= MAX_RESULTS) break;
            out.add(new CameraEntry(e.getKey(), e.getValue().toYRot()));
        }
        return out;
    }
}
