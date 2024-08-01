package org.dimdev.dimdoors.rift.targets;

import com.mojang.serialization.Codec;
import net.minecraft.core.Vec3i;
import org.dimdev.dimdoors.api.util.Location;

public class RelativeReference extends RiftReference {
	public static final Codec<RelativeReference> CODEC = Vec3i.CODEC.xmap(RelativeReference::new, RelativeReference::getOffset).fieldOf("offset").codec();
	private final Vec3i offset;

	public RelativeReference(Vec3i offset) {
		this.offset = offset;
	}

	@Override
	public Location getReferencedLocation() {
		return new Location(this.location.world, this.location.pos.offset(this.offset));
	}

	public Vec3i getOffset() {
		return this.offset;
	}

	@Override
	public VirtualTargetType<? extends VirtualTarget> getType() {
		return VirtualTargetType.RELATIVE.get();
	}

	@Override
	public VirtualTarget copy() {
		return new RelativeReference(offset);
	}
}
