package org.dimdev.dimdoors.rift.targets;

import org.dimdev.dimdoors.util.Location;
import org.dimdev.dimdoors.util.RGBA;

public abstract class RestoringTarget extends VirtualTarget {

    public RestoringTarget() {
    }

    @Override
    public Target receiveOther() {
        if (getTarget() != null) {
            getTarget().location = location;
            return getTarget();
        }

        Location linkTarget = makeLinkTarget();
        if (linkTarget != null) {
            setTarget(RiftReference.tryMakeLocal(location, linkTarget));
            getTarget().setLocation(location);
            getTarget().register();

            return getTarget();
        } else {
            return null;
        }
    }

    @Override
    public boolean shouldInvalidate(Location deletedRift) {
        if (getTarget().shouldInvalidate(deletedRift)) {
            getTarget().unregister();
        }
        return false;
    }

    @Override
    public void setLocation(Location location) {
        super.setLocation(location);
        if (getTarget() != null) {
            getTarget().setLocation(location);
        }
    }

    @Override
    public void unregister() {
        if (getTarget() != null) getTarget().unregister();
    }

    protected abstract VirtualTarget getTarget();

    protected abstract void setTarget(VirtualTarget target);

    @Override
    public RGBA getColor() {
        if (getTarget() != null) {
            getTarget().location = location;
            return getTarget().getColor();
        } else {
            return getUnlinkedColor(location);
        }
    }

    protected RGBA getUnlinkedColor(Location location) {
        return new RGBA(0, 1, 1, 1);
    }

    public abstract Location makeLinkTarget();
}
