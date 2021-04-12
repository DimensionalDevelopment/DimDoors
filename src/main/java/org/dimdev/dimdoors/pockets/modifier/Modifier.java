package org.dimdev.dimdoors.pockets.modifier;

import com.mojang.serialization.Lifecycle;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;

import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

import java.util.function.Supplier;

public interface Modifier {
	Registry<ModifierType<? extends Modifier>> REGISTRY = FabricRegistryBuilder.from(new SimpleRegistry<ModifierType<? extends Modifier>>(RegistryKey.ofRegistry(new Identifier("dimdoors", "modifier_type")), Lifecycle.stable())).buildAndRegister();

	static Modifier deserialize(NbtCompound nbt) {
		Identifier id = Identifier.tryParse(nbt.getString("type")); // TODO: return some NONE Modifier if type cannot be found or deserialization fails.
		return REGISTRY.get(id).fromNbt(nbt);
	}

	static NbtCompound serialize(Modifier modifier) {
		return modifier.toNbt(new NbtCompound());
	}


	Modifier fromNbt(NbtCompound nbt);

	default NbtCompound toNbt(NbtCompound nbt) {
		return this.getType().toNbt(nbt);
	}

	ModifierType<? extends Modifier> getType();

	String getKey();

	void apply(PocketGenerationContext parameters, RiftManager manager);

	void apply(PocketGenerationContext parameters, Pocket.PocketBuilder<?, ?> builder);

	interface ModifierType<T extends Modifier> {
		ModifierType<ShellModifier> SHELL_MODIFIER_TYPE = register(new Identifier("dimdoors", ShellModifier.KEY), ShellModifier::new);
		ModifierType<DimensionalDoorModifier> DIMENSIONAL_DOOR_MODIFIER_TYPE = register(new Identifier("dimdoors", DimensionalDoorModifier.KEY), DimensionalDoorModifier::new);
		ModifierType<PocketEntranceModifier> PUBLIC_MODIFIER_TYPE = register(new Identifier("dimdoors", PocketEntranceModifier.KEY), PocketEntranceModifier::new);
		ModifierType<RiftDataModifier> RIFT_DATA_MODIFIER_TYPE = register(new Identifier("dimdoors", RiftDataModifier.KEY), RiftDataModifier::new);
		ModifierType<RelativeReferenceModifier> RELATIVE_REFERENCE_MODIFIER_TYPE = register(new Identifier("dimdoors", RelativeReferenceModifier.KEY), RelativeReferenceModifier::new);
		ModifierType<OffsetModifier> OFFSET_MODIFIER_TYPE = register(new Identifier("dimdoors", OffsetModifier.KEY), OffsetModifier::new);
		ModifierType<AbsoluteRiftBlockEntityModifier> ABSOLUTE_RIFT_BLOCK_ENTITY_MODIFIER_TYPE = register(new Identifier("dimdoors", AbsoluteRiftBlockEntityModifier.KEY), AbsoluteRiftBlockEntityModifier::new);

		Modifier fromNbt(NbtCompound nbt);

		NbtCompound toNbt(NbtCompound nbt);

		static void register() {
			DimensionalDoorsInitializer.apiSubscribers.forEach(d -> d.registerModifierTypes(REGISTRY));
		}

		static <U extends Modifier> ModifierType<U> register(Identifier id, Supplier<U> factory) {
			return Registry.register(REGISTRY, id, new ModifierType<U>() {
				@Override
				public Modifier fromNbt(NbtCompound nbt) {
					return factory.get().fromNbt(nbt);
				}

				@Override
				public NbtCompound toNbt(NbtCompound nbt) {
					nbt.putString("type", id.toString());
					return nbt;
				}
			});
		}
	}
}
