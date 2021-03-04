package org.dimdev.dimdoors.pockets.modifier;

import com.mojang.serialization.Lifecycle;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;

import org.dimdev.dimdoors.util.PocketGenerationParameters;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

import java.util.function.Supplier;

public interface Modifier {
	Registry<ModifierType<? extends Modifier>> REGISTRY = FabricRegistryBuilder.from(new SimpleRegistry<ModifierType<? extends Modifier>>(RegistryKey.ofRegistry(new Identifier("dimdoors", "modifier_type")), Lifecycle.stable())).buildAndRegister();

	static Modifier deserialize(CompoundTag tag) {
		Identifier id = Identifier.tryParse(tag.getString("type")); // TODO: return some NONE Modifier if type cannot be found or deserialization fails.
		return REGISTRY.get(id).fromTag(tag);
	}

	static CompoundTag serialize(Modifier modifier) {
		return modifier.toTag(new CompoundTag());
	}


	Modifier fromTag(CompoundTag tag);

	default CompoundTag toTag(CompoundTag tag) {
		return this.getType().toTag(tag);
	}

	ModifierType<? extends Modifier> getType();

	String getKey();

	void apply(PocketGenerationParameters parameters, RiftManager manager);

	void apply(PocketGenerationParameters parameters, Pocket.PocketBuilder<?, ?> builder);

	interface ModifierType<T extends Modifier> {
		ModifierType<ShellModifier> SHELL_MODIFIER_TYPE = register(new Identifier("dimdoors", ShellModifier.KEY), ShellModifier::new);
		ModifierType<DimensionalDoorModifier> DIMENSIONAL_DOOR_MODIFIER_TYPE = register(new Identifier("dimdoors", DimensionalDoorModifier.KEY), DimensionalDoorModifier::new);
		ModifierType<PocketEntranceModifier> PUBLIC_MODIFIER_TYPE = register(new Identifier("dimdoors", PocketEntranceModifier.KEY), PocketEntranceModifier::new);
		ModifierType<RiftDataModifier> RIFT_DATA_MODIFIER_TYPE = register(new Identifier("dimdoors", RiftDataModifier.KEY), RiftDataModifier::new);
		ModifierType<RelativeReferenceModifier> RELATIVE_REFERENCE_MODIFIER_TYPE = register(new Identifier("dimdoors", RelativeReferenceModifier.KEY), RelativeReferenceModifier::new);
		ModifierType<OffsetModifier> OFFSET_MODIFIER_TYPE = register(new Identifier("dimdoors", OffsetModifier.KEY), OffsetModifier::new);
		ModifierType<AbsoluteRiftBlockEntityModifier> ABSOLUTE_RIFT_BLOCK_ENTITY_MODIFIER_TYPE = register(new Identifier("dimdoors", AbsoluteRiftBlockEntityModifier.KEY), AbsoluteRiftBlockEntityModifier::new);

		Modifier fromTag(CompoundTag tag);

		CompoundTag toTag(CompoundTag tag);

		static void register() {
		}

		static <U extends Modifier> ModifierType<U> register(Identifier id, Supplier<U> factory) {
			return Registry.register(REGISTRY, id, new ModifierType<U>() {
				@Override
				public Modifier fromTag(CompoundTag tag) {
					return factory.get().fromTag(tag);
				}

				@Override
				public CompoundTag toTag(CompoundTag tag) {
					tag.putString("type", id.toString());
					return tag;
				}
			});
		}
	}
}
