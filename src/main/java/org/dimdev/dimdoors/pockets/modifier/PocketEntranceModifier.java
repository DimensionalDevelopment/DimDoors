package org.dimdev.dimdoors.pockets.modifier;

import java.util.List;
import java.util.Map;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.rift.registry.PocketEntrancePointer;
import org.dimdev.dimdoors.rift.targets.PocketEntranceMarker;
import org.dimdev.dimdoors.rift.targets.PocketExitMarker;
import org.dimdev.dimdoors.util.PocketGenerationParameters;
import org.dimdev.dimdoors.world.pocket.Pocket;

public class PocketEntranceModifier implements Modifier {
	public static final String KEY = "pocket_entrance";

	private int id;

	public PocketEntranceModifier(int id) {
		this.id = id;
	}

	public PocketEntranceModifier() {

	}

	@Override
	public Modifier fromTag(CompoundTag tag) {
		return new PocketEntranceModifier(tag.getInt("id"));
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		Modifier.super.toTag(tag);

		tag.putInt("id", id);

		return tag;
	}


	@Override
	public ModifierType<? extends Modifier> getType() {
		return ModifierType.PUBLIC_MODIFIER_TYPE;
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public void apply(PocketGenerationParameters parameters, RiftManager manager) {
		manager.consume(id, rift -> {
			rift.setDestination(PocketEntranceMarker.builder().ifDestination(new PocketExitMarker()).weight(1.0f).build());
			return true;
		});
	}
}
