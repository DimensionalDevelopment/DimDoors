package org.dimdev.dimdoors.rift.targets;

import com.mojang.serialization.Codec;
import org.dimdev.dimdoors.api.util.Location;

public class GlobalReference extends RiftReference {
	public static Codec<GlobalReference> CODEC = Location.CODEC.fieldOf("target").xmap(GlobalReference::new, GlobalReference::getReferencedLocation).codec();

	private final Location target;

	public GlobalReference(Location target) {
		this.target = target;
	}

	@Override
	public Location getReferencedLocation() {
		return this.target;
	}

	@Override
	public VirtualTargetType<? extends VirtualTarget> getType() {
		return VirtualTargetType.GLOBAL.get();
	}

	@Override
	public VirtualTarget copy() {
		return new GlobalReference(target);
	}
}
