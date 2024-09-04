package org.dimdev.dimdoors.rift.targets;

<<<<<<< HEAD
import net.minecraft.nbt.CompoundTag;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.pockets.PocketGenerator;
import org.dimdev.dimdoors.forge.world.level.registry.DimensionalRegistry;
import org.dimdev.dimdoors.forge.world.pocket.VirtualLocation;
import org.dimdev.dimdoors.forge.world.pocket.type.Pocket;

=======
>>>>>>> merge-branch
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
}
