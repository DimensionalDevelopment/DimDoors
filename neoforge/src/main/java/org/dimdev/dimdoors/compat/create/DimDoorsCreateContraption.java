package org.dimdev.dimdoors.compat.create;

import net.minecraft.core.BlockPos;
import org.dimdev.dimdoors.api.util.Location;

import java.util.Map;

public interface DimDoorsCreateContraption {
    Map<BlockPos, Location> dimdoors$getTrackedRifts();
}
