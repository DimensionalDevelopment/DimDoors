package org.dimdev.dimdoors.rift.targets;

import org.dimdev.dimdoors.api.rift.target.Target;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.api.util.RGBA;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;

public abstract class RestoringTarget<T extends VirtualTarget<T>> extends VirtualTarget<T> {

    @Override
    public Target receiveOther() {

        Location linkTarget = this.makeLinkTarget();

        if (linkTarget != null && this.location.getBlockEntity() instanceof RiftBlockEntity rift) {
            var reference = linkTarget.asTarget();

            rift.setDestination(reference);

            return reference;

        } else {
            return null;
        }
    }

    public abstract Location makeLinkTarget();
}