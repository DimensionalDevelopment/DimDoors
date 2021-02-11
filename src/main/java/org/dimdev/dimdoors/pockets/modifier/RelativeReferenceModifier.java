package org.dimdev.dimdoors.pockets.modifier;

import java.util.Optional;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;

import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.rift.targets.LocalReference;
import org.dimdev.dimdoors.rift.targets.RiftReference;
import org.dimdev.dimdoors.util.Location;
import org.dimdev.dimdoors.util.PocketGenerationParameters;

public class RelativeReferenceModifier implements Modifier {
	public static final String KEY = "relative";

	private int point_a, point_b;

	@Override
	public Modifier fromTag(CompoundTag tag) {
		point_a = tag.getInt("point_a");
		point_b = tag.getInt("point_b");
		return this;
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		Modifier.super.toTag(tag);
		tag.putInt("point_a", point_a);
		tag.putInt("point_b", point_b);
		return tag;
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
	public void apply(PocketGenerationParameters parameters, RiftManager manager) {
		Optional<Location> riftA = manager.get(point_a).map(rift -> new Location((ServerWorld) rift.getWorld(), rift.getPos()));
		Optional<Location> riftB = manager.get(point_b).map(rift -> new Location((ServerWorld) rift.getWorld(), rift.getPos()));

		if(riftA.isPresent() && riftB.isPresent()) {
			RiftReference link1 = LocalReference.tryMakeRelative(riftA.get(), riftB.get());
			RiftReference link2 = LocalReference.tryMakeRelative(riftB.get(), riftA.get());

			manager.consume(point_a, rift -> addLink(rift, link1));
			manager.consume(point_b, rift -> addLink(rift, link2));
		}
	}

	private boolean addLink(RiftBlockEntity rift, RiftReference link) {
		rift.setDestination(link);
		return true;
	}
}
