package org.dimdev.dimdoors.rift.targets;

import net.minecraft.nbt.CompoundTag;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.pockets.PocketGenerator;
import org.dimdev.dimdoors.forge.world.level.registry.DimensionalRegistry;
import org.dimdev.dimdoors.forge.world.pocket.VirtualLocation;
import org.dimdev.dimdoors.forge.world.pocket.type.Pocket;

public abstract class WrappedDestinationTarget extends RestoringTarget {
    protected VirtualTarget wrappedDestination = null;

    public WrappedDestinationTarget(VirtualTarget wrappedDestination) {
        this.wrappedDestination = wrappedDestination;
    }

    public WrappedDestinationTarget() {
    }

    @Override
    protected VirtualTarget getTarget() {
        return this.wrappedDestination;
    }

    @Override
    protected void setTarget(VirtualTarget target) {
        this.wrappedDestination = target;
    }

    public static CompoundTag toNbt(WrappedDestinationTarget target) {
        CompoundTag nbt = new CompoundTag();
        if (target.wrappedDestination != null)
            nbt.put("wrappedDestination", VirtualTarget.toNbt(target.wrappedDestination));
        return nbt;
    }


    public static <T extends WrappedDestinationTarget> T fromNbt(CompoundTag nbt, T target) {
        if (nbt.contains("wrappedDestination"))
            target.wrappedDestination = VirtualTarget.fromNbt(nbt.getCompound("wrappedDestination"));
        return target;
    }
}
