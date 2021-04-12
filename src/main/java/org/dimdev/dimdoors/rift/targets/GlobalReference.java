package org.dimdev.dimdoors.rift.targets;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.NbtCompound;
import org.dimdev.dimdoors.api.util.Location;

public class GlobalReference extends RiftReference {
	public static Codec<GlobalReference> CODEC = Location.CODEC.fieldOf("location").xmap(GlobalReference::new, GlobalReference::getReferencedLocation).codec();

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
		return VirtualTargetType.GLOBAL;
	}

	public static NbtCompound toTag(GlobalReference virtualTarget) {
		NbtCompound tag = new NbtCompound();
		tag.put("target", Location.toTag(virtualTarget.getReferencedLocation()));
		return tag;
	}

	public static GlobalReference fromTag(NbtCompound nbt) {
		return new GlobalReference(Location.fromTag(nbt.getCompound("target")));
	}
}
