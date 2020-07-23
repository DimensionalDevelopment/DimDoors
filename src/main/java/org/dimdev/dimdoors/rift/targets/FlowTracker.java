package org.dimdev.dimdoors.rift.targets;

import java.util.HashMap;
import java.util.Map;

import org.dimdev.annotatednbt.AnnotatedNbt;
import org.dimdev.annotatednbt.Saved;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.Direction;

/**
 * Helps flow (fluid, redstone, power) senders to keep track of flow received by the
 * target rift.
 */

public class FlowTracker { // TODO
    //@Saved public Map<Direction, Map<Fluid, Integer>> fluids = new HashMap<>();
    @Saved
    public Map<Direction, Integer> redstone = new HashMap<>();
    @Saved
    public Map<Direction, Integer> power = new HashMap<>();

    public void fromTag(CompoundTag nbt) {
        AnnotatedNbt.load(this, nbt);
    }

    public CompoundTag toTag(CompoundTag nbt) {
        return AnnotatedNbt.serialize(this);
    }
}
