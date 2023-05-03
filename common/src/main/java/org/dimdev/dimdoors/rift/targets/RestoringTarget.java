package org.dimdev.dimdoors.rift.targets;

import org.dimdev.dimdoors.api.rift.target.Target;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.api.util.RGBA;

public abstract class RestoringTarget extends VirtualTarget {
	public RestoringTarget() {
	}

	@Override
	public Target receiveOther() {
		if (this.getTarget() != null) {
			return this.getTarget();
		}

		Location linkTarget = this.makeLinkTarget();
		if (linkTarget != null) {
			this.setTarget(RiftReference.tryMakeLocal(this.location, linkTarget));
			this.getTarget().setLocation(linkTarget);
			this.getTarget().register();

			return this.getTarget();
		} else {
			return null;
		}
	}

	@Override
	public boolean shouldInvalidate(Location deletedRift) {
		if (this.getTarget().shouldInvalidate(deletedRift)) {
			this.getTarget().unregister();
		}
		return false;
	}

	@Override
	public void setLocation(Location location) {
		super.setLocation(location);
		if (this.getTarget() != null) {
			this.getTarget().setLocation(location);
		}
	}

	@Override
	public void unregister() {
		if (this.getTarget() != null) this.getTarget().unregister();
	}

	protected abstract VirtualTarget getTarget();

	protected abstract void setTarget(VirtualTarget target);

	@Override
	public RGBA getColor() {
		if (this.getTarget() != null) {
			this.getTarget().location = this.location;
			return this.getTarget().getColor();
		} else {
			return this.getUnlinkedColor(this.location);
		}
	}

	protected RGBA getUnlinkedColor(Location location) {
		return new RGBA(0, 1, 1, 1);
	}

	public abstract Location makeLinkTarget();
}
