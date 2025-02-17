package org.dimdev.dimdoors.pockets.modifier;

import com.google.common.base.MoreObjects;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.rift.targets.PocketEntranceMarker;
import org.dimdev.dimdoors.rift.targets.PocketExitMarker;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

public class PocketEntranceModifier extends AbstractModifier {
	public static final MapCodec<PocketEntranceModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(Codec.INT.fieldOf("id").forGetter(PocketEntranceModifier::getId)).apply(instance, PocketEntranceModifier::new));

	public static final String KEY = "pocket_entrance";

	private int id;

	public PocketEntranceModifier(int id) {
		this.id = id;
	}

	public PocketEntranceModifier() {

	}

	public Integer getId() {
		return id;
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
	public void apply(PocketGenerationContext parameters, RiftManager manager) {
		manager.consume(id, rift -> {
			rift.setDestination(PocketEntranceMarker.builder().ifDestination(PocketExitMarker.INSTANCE).weight(1.0f).build());
			return true;
		});
	}

	@Override
	public void apply(PocketGenerationContext parameters, Pocket.PocketBuilder<?, ?> builder) {

	}
}
