package org.dimdev.dimdoors.pockets.modifier;

import com.google.common.base.MoreObjects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.packs.resources.ResourceManager;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.rift.targets.PocketEntranceMarker;
import org.dimdev.dimdoors.rift.targets.PocketExitMarker;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

public class PocketEntranceModifier extends AbstractModifier {
	public static final String KEY = "pocket_entrance";

	private int id;

	public PocketEntranceModifier(int id) {
		this.id = id;
	}

	public PocketEntranceModifier() {

	}

	@Override
	public Modifier fromNbt(CompoundTag nbt, ResourceManager allowReference) {
		return new PocketEntranceModifier(nbt.getInt("id"));
	}

	@Override
	public CompoundTag toNbtInternal(CompoundTag nbt, boolean allowReference) {
		super.toNbtInternal(nbt, allowReference);

		nbt.putInt("id", id);

		return nbt;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("id", id)
				.toString();
	}

	@Override
	public ModifierType<? extends Modifier> getType() {
		return ModifierType.PUBLIC_MODIFIER_TYPE.get();
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public void apply(PocketGenerationContext parameters, RiftManager manager) {
		manager.consume(id, rift -> {
			rift.setDestination(PocketEntranceMarker.builder().ifDestination(new PocketExitMarker()).weight(1.0f).build());
			return true;
		});
	}

	@Override
	public void apply(PocketGenerationContext parameters, Pocket.PocketBuilder<?, ?> builder) {

	}
}
