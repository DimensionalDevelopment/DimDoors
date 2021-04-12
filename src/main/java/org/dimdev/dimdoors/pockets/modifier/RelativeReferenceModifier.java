package org.dimdev.dimdoors.pockets.modifier;

import java.util.Optional;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;

import com.google.common.base.MoreObjects;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.rift.targets.LocalReference;
import org.dimdev.dimdoors.rift.targets.RiftReference;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

public class RelativeReferenceModifier implements Modifier {
	public static final String KEY = "relative";

	private int point_a, point_b;

	@Override
	public Modifier fromNbt(NbtCompound nbt) {
		point_a = nbt.getInt("point_a");
		point_b = nbt.getInt("point_b");
		return this;
	}

	@Override
	public NbtCompound toNbt(NbtCompound nbt) {
		Modifier.super.toNbt(nbt);
		nbt.putInt("point_a", point_a);
		nbt.putInt("point_b", point_b);
		return nbt;
	}

	@Override
	public ModifierType<? extends Modifier> getType() {
		return ModifierType.RELATIVE_REFERENCE_MODIFIER_TYPE;
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public void apply(PocketGenerationContext parameters, RiftManager manager) {
		Optional<Location> riftA = manager.get(point_a).map(rift -> new Location((ServerWorld) rift.getWorld(), rift.getPos()));
		Optional<Location> riftB = manager.get(point_b).map(rift -> new Location((ServerWorld) rift.getWorld(), rift.getPos()));

		if(riftA.isPresent() && riftB.isPresent()) {
			RiftReference link1 = LocalReference.tryMakeRelative(riftA.get(), riftB.get());
			RiftReference link2 = LocalReference.tryMakeRelative(riftB.get(), riftA.get());

			manager.consume(point_a, rift -> addLink(rift, link1));
			manager.consume(point_b, rift -> addLink(rift, link2));
		}
	}

	@Override
	public void apply(PocketGenerationContext parameters, Pocket.PocketBuilder<?, ?> builder) {

	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("point_a", point_a)
				.add("point_b", point_b)
				.toString();
	}

	private boolean addLink(RiftBlockEntity rift, RiftReference link) {
		rift.setDestination(link);
		return true;
	}
}
