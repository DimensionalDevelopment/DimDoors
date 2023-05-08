package org.dimdev.dimdoors.rift.targets;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import org.dimdev.dimdoors.api.util.Location;

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
		return VirtualTargetType.LOCAL.get();
	}

	public static CompoundTag toNbt(LocalReference localReference) {
		CompoundTag nbt = new CompoundTag();
		nbt.putIntArray("target", new int[]{localReference.target.getX(), localReference.target.getY(), localReference.target.getZ()});
		return nbt;
	}

	public static LocalReference fromNbt(CompoundTag nbt) {
		int[] pos = nbt.getIntArray("target");
		return new LocalReference(
				new BlockPos(pos[0], pos[1], pos[2])
		);
	}
}
