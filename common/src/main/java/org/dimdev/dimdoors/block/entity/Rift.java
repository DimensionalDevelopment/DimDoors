package org.dimdev.dimdoors.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.dimdev.dimdoors.api.rift.target.Target;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.api.util.RGBA;
import org.dimdev.dimdoors.rift.registry.LinkProperties;
import org.dimdev.dimdoors.rift.targets.VirtualTarget;

public interface Rift extends Target {
    RiftData getData();

    void setData(RiftData data);

    VirtualTarget<?> getDestination();

    void setDestination(VirtualTarget<?> destination);

    LinkProperties getProperties();

    void setProperties(LinkProperties properties);

    void updateProperties();

    boolean isAlwaysDelete();

    boolean isForcedColor();

    RGBA getColor();

    void setColor(RGBA color);

    Target getTarget();

    boolean isRegistered();

    void register();

    void unregister();

    void updateType();

    void markStateChanged();

    void handleSourceMoved(Location location);

    void handleTargetGone(Location location);

    void handleSourceGone(Location location);

    void updateColor();

    void copyFrom(Rift rift);

    void setDeleteRift(boolean deleteRift);

    void detach();

    BlockPos getBlockPos();

    Level getLevel();
}
