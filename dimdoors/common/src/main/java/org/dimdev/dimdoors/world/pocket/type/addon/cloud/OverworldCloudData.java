package org.dimdev.dimdoors.world.pocket.type.addon.cloud;

import net.minecraft.world.phys.Vec3;

public interface OverworldCloudData extends CloudData {
    float getCloudHeight();
    Vec3 getCloudColor();

    default CloudDataType<?> type() {
        return CloudDataType.OVERWORLD;
    }

}
