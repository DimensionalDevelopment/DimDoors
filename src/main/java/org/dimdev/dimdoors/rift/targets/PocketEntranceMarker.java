package org.dimdev.dimdoors.rift.targets;

import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.Vec3d;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.dimdev.dimdoors.api.rift.target.EntityTarget;
import org.dimdev.dimdoors.api.util.EntityUtils;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.TranslatableText;

public class PocketEntranceMarker extends VirtualTarget implements EntityTarget {
	private final float weight;
	private final VirtualTarget ifDestination;
	private final VirtualTarget otherwiseDestination;

	public PocketEntranceMarker() {
		this(1, NoneTarget.INSTANCE, NoneTarget.INSTANCE);
	}

	public PocketEntranceMarker(float weight, VirtualTarget ifDestination, VirtualTarget otherwiseDestination) {
		this.weight = weight;
		this.ifDestination = ifDestination;
		this.otherwiseDestination = otherwiseDestination;
	}

	public static PocketEntranceMarkerBuilder builder() {
		return new PocketEntranceMarkerBuilder();
	}

	@Override
	public boolean receiveEntity(Entity entity, Vec3d relativePos, EulerAngle relativeAngle, Vec3d relativeVelocity) {
		EntityUtils.chat(entity, new TranslatableText("The entrance of this dungeon has not been converted. If this is a normally generated pocket, please report this bug."));
		return false;
	}

	public float getWeight() {
		return this.weight;
	}

	public VirtualTarget getIfDestination() {
		return this.ifDestination;
	}

	public VirtualTarget getOtherwiseDestination() {
		return this.otherwiseDestination;
	}

	public String toString() {
		return "PocketEntranceMarker(weight=" + this.getWeight() + ", ifDestination=" + this.getIfDestination() + ", otherwiseDestination=" + this.getOtherwiseDestination() + ")";
	}

	public PocketEntranceMarkerBuilder toBuilder() {
		return new PocketEntranceMarkerBuilder().weight(this.weight).ifDestination(this.ifDestination).otherwiseDestination(this.otherwiseDestination);
	}

	@Override
	public VirtualTargetType<? extends VirtualTarget> getType() {
		return VirtualTargetType.POCKET_ENTRANCE;
	}

	public static NbtCompound toTag(PocketEntranceMarker target) {
		NbtCompound tag = new NbtCompound();
		tag.putFloat("weight", target.weight);
		tag.put("ifDestination", VirtualTarget.toTag(target.ifDestination));
		tag.put("otherwiseDestination", VirtualTarget.toTag(target.otherwiseDestination));
		return tag;
	}

	public static PocketEntranceMarker fromTag(NbtCompound tag) {
		return PocketEntranceMarker.builder()
				.weight(tag.getFloat("weight"))
				.ifDestination(tag.contains("ifDestination") ? VirtualTarget.fromTag(tag.getCompound("ifDestination")) : NoneTarget.INSTANCE)
				.otherwiseDestination(tag.contains("otherwiseDestination") ? VirtualTarget.fromTag(tag.getCompound("otherwiseDestination")) : NoneTarget.INSTANCE)
				.build();
	}

	public static class PocketEntranceMarkerBuilder {
		private float weight;
		private VirtualTarget ifDestination = NoneTarget.INSTANCE;
		private VirtualTarget otherwiseDestination = NoneTarget.INSTANCE;

		private PocketEntranceMarkerBuilder() {
		}

		public PocketEntranceMarker.PocketEntranceMarkerBuilder weight(float weight) {
			this.weight = weight;
			return this;
		}

		public PocketEntranceMarker.PocketEntranceMarkerBuilder ifDestination(VirtualTarget ifDestination) {
			this.ifDestination = ifDestination;
			return this;
		}

		public PocketEntranceMarker.PocketEntranceMarkerBuilder otherwiseDestination(VirtualTarget otherwiseDestination) {
			this.otherwiseDestination = otherwiseDestination;
			return this;
		}

		public PocketEntranceMarker build() {
			return new PocketEntranceMarker(this.weight, this.ifDestination, this.otherwiseDestination);
		}

		@Override
		public String toString() {
			return new ToStringBuilder(this)
					.append("weight", weight)
					.append("ifDestination", ifDestination)
					.append("otherwiseDestination", otherwiseDestination)
					.toString();
		}
	}
}
