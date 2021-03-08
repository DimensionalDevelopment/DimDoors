package org.dimdev.dimdoors.rift.targets;

import com.mojang.serialization.Codec;
import org.dimdev.dimdoors.util.Location;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;

public class LocalReference extends RiftReference {
	public static final Codec<LocalReference> CODEC = BlockPos.CODEC.xmap(LocalReference::new, LocalReference::getTarget).fieldOf("target").codec();

	private final BlockPos target;

	public LocalReference(BlockPos target) {
		this.target = target;
	}

	@Override
	public Location getReferencedLocation() {
		return new Location(this.location.world, this.target);
	}

	public BlockPos getTarget() {
		return this.target;
	}

	@Override
	public VirtualTargetType<? extends VirtualTarget> getType() {
		return VirtualTargetType.LOCAL;
	}

	public static CompoundTag toTag(LocalReference localReference) {
		CompoundTag tag = new CompoundTag();
		tag.putIntArray("target", new int[]{localReference.target.getX(), localReference.target.getY(), localReference.target.getZ()});
		return tag;
	}

	public static LocalReference fromTag(CompoundTag tag) {
		int[] pos = tag.getIntArray("target");
		return new LocalReference(
				new BlockPos(pos[0], pos[1], pos[2])
		);
	}
}
