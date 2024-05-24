package org.dimdev.dimdoors.rift.targets;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Rotations;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.dimdev.dimdoors.api.rift.target.EntityTarget;
import org.dimdev.dimdoors.api.util.EntityUtils;
import org.dimdev.dimdoors.api.util.Location;

public class PocketEntranceMarker extends VirtualTarget implements EntityTarget {
	public static final MapCodec<PocketEntranceMarker> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(Codec.FLOAT.fieldOf("weight").forGetter(PocketEntranceMarker::getWeight), VirtualTarget.CODEC.fieldOf("ifDestination").forGetter(PocketEntranceMarker::getIfDestination), VirtualTarget.CODEC.fieldOf("otherwiseDestination").forGetter(PocketEntranceMarker::getOtherwiseDestination)).apply(inst, PocketEntranceMarker::new));
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
	public boolean receiveEntity(Entity entity, Vec3 relativePos, Rotations relativeAngle, Vec3 relativeVelocity, Location location) {
		EntityUtils.chat(entity, Component.translatable("The entrance of this dungeon has not been converted. If this is a normally generated pocket, please report this bug."));
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
		return VirtualTargetType.POCKET_ENTRANCE.get();
	}

	@Override
	public VirtualTarget copy() {
		return new PocketEntranceMarker(weight, ifDestination, otherwiseDestination);
	}

	public static CompoundTag toNbt(PocketEntranceMarker target) {
		CompoundTag nbt = new CompoundTag();
		nbt.putFloat("weight", target.weight);
		nbt.put("ifDestination", VirtualTarget.toNbt(target.ifDestination));
		nbt.put("otherwiseDestination", VirtualTarget.toNbt(target.otherwiseDestination));
		return nbt;
	}

	public static PocketEntranceMarker fromNbt(CompoundTag nbt) {
		return PocketEntranceMarker.builder()
				.weight(nbt.getFloat("weight"))
				.ifDestination(nbt.contains("ifDestination") ? VirtualTarget.fromNbt(nbt.getCompound("ifDestination")) : NoneTarget.INSTANCE)
				.otherwiseDestination(nbt.contains("otherwiseDestination") ? VirtualTarget.fromNbt(nbt.getCompound("otherwiseDestination")) : NoneTarget.INSTANCE)
				.build();
	}

	public static class PocketEntranceMarkerBuilder {
		private float weight;
		private VirtualTarget ifDestination = NoneTarget.INSTANCE;
		private VirtualTarget otherwiseDestination = NoneTarget.INSTANCE;

		private PocketEntranceMarkerBuilder() {
		}

		public PocketEntranceMarkerBuilder weight(float weight) {
			this.weight = weight;
			return this;
		}

		public PocketEntranceMarkerBuilder ifDestination(VirtualTarget ifDestination) {
			this.ifDestination = ifDestination;
			return this;
		}

		public PocketEntranceMarkerBuilder otherwiseDestination(VirtualTarget otherwiseDestination) {
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
