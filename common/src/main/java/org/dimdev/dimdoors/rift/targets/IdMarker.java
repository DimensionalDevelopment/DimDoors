package org.dimdev.dimdoors.rift.targets;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Rotations;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.api.rift.target.EntityTarget;
import org.dimdev.dimdoors.api.util.EntityUtils;
import org.dimdev.dimdoors.api.util.Location;

public class IdMarker extends VirtualTarget implements EntityTarget {
	public static final MapCodec<IdMarker> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(Codec.INT.fieldOf("id").forGetter(IdMarker::getId)).apply(inst, IdMarker::new));
	private final int id;

	public IdMarker(int id) {
		this.id = id;
	}

	@Override
	public VirtualTargetType<? extends VirtualTarget> getType() {
		return VirtualTargetType.ID_MARKER.get();
	}

	@Override
	public VirtualTarget copy() {
		return new IdMarker(id);
	}

	public static CompoundTag toNbt(IdMarker target) {
		CompoundTag nbt = new CompoundTag();
		nbt.putInt("id", target.id);
		return nbt;
	}

	public static IdMarker fromNbt(CompoundTag nbt) {
		return new IdMarker(nbt.getInt("id"));
	}

	public int getId() {
		return this.id;
	}

	@Override
	public boolean receiveEntity(Entity entity, Vec3 relativePos, Rotations relativeAngle, Vec3 relativeVelocity, Location location) {
		EntityUtils.chat(entity, Component.literal("This rift is configured for pocket dungeons. Its id is " + this.id));
		return false;
	}
}
