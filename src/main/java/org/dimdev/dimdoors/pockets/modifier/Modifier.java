package org.dimdev.dimdoors.pockets.modifier;

import com.google.common.collect.Multimap;
import com.mojang.serialization.Lifecycle;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.util.ReferenceSerializable;
import org.dimdev.dimdoors.api.util.ResourceUtil;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

import java.util.Collection;
import java.util.function.Supplier;

public interface Modifier extends ReferenceSerializable {
	Registry<ModifierType<? extends Modifier>> REGISTRY = FabricRegistryBuilder.from(new SimpleRegistry<ModifierType<? extends Modifier>>(RegistryKey.ofRegistry(DimensionalDoors.id("modifier_type")), Lifecycle.stable(), false)).buildAndRegister();

	String RESOURCE_STARTING_PATH = "pockets/modifier"; //TODO: might want to restructure data packs

	static Modifier deserialize(NbtElement nbt, ResourceManager manager) {
		switch (nbt.getType()) {
			case NbtType.COMPOUND: // It's a serialized Modifier
				return Modifier.deserialize((NbtCompound) nbt, manager);
			case NbtType.STRING: // It's a reference to a resource location
				// TODO: throw if manager is null
				return ResourceUtil.loadReferencedResource(manager, RESOURCE_STARTING_PATH, nbt.asString(), ResourceUtil.NBT_READER.andThenComposable(nbtElement -> deserialize(nbtElement, manager)));
			default:
				throw new RuntimeException(String.format("Unexpected NbtType %d!", nbt.getType()));
		}
	}

	static Modifier deserialize(NbtElement nbt) {
		return deserialize(nbt, null);
	}

	static Modifier deserialize(NbtCompound nbt, ResourceManager manager) {
		Identifier id = Identifier.tryParse(nbt.getString("type")); // TODO: return some NONE Modifier if type cannot be found or deserialization fails.
		return REGISTRY.get(id).fromNbt(nbt, manager);
	}

	static Modifier deserialize(NbtCompound nbt) {
		return deserialize(nbt, null);
	}

	static NbtElement serialize(Modifier modifier, boolean allowReference) {
		return modifier.toNbt(new NbtCompound(), allowReference);
	}

	static NbtElement serialize(Modifier modifier) {
		return serialize(modifier, false);
	}


	Modifier fromNbt(NbtCompound nbt, ResourceManager manager);

	default Modifier fromNbt(NbtCompound nbt) {
		return fromNbt(nbt, null);
	}

	default NbtElement toNbt(NbtCompound nbt, boolean allowReference) {
		return this.getType().toNbt(nbt);
	}

	default NbtElement toNbt(NbtCompound nbt) {
		return toNbt(nbt, false);
	}

	void setResourceKey(String resourceKey);

	String getResourceKey();

	default void processFlags(Multimap<String, String> flags) {
		// TODO: discuss some flag standardization
		Collection<String> reference = flags.get("reference");
		if (reference.stream().findFirst().map(string -> string.equals("local") || string.equals("global")).orElse(false)) {
			setResourceKey(flags.get("resource_key").stream().findFirst().orElse(null));
		}
	}

	ModifierType<? extends Modifier> getType();

	String getKey();

	void apply(PocketGenerationContext parameters, RiftManager manager);

	void apply(PocketGenerationContext parameters, Pocket.PocketBuilder<?, ?> builder);

	interface ModifierType<T extends Modifier> {
		ModifierType<ShellModifier> SHELL_MODIFIER_TYPE = register(DimensionalDoors.id(ShellModifier.KEY), ShellModifier::new);
		ModifierType<DimensionalDoorModifier> DIMENSIONAL_DOOR_MODIFIER_TYPE = register(DimensionalDoors.id(DimensionalDoorModifier.KEY), DimensionalDoorModifier::new);
		ModifierType<PocketEntranceModifier> PUBLIC_MODIFIER_TYPE = register(DimensionalDoors.id(PocketEntranceModifier.KEY), PocketEntranceModifier::new);
		ModifierType<RiftDataModifier> RIFT_DATA_MODIFIER_TYPE = register(DimensionalDoors.id(RiftDataModifier.KEY), RiftDataModifier::new);
		ModifierType<RelativeReferenceModifier> RELATIVE_REFERENCE_MODIFIER_TYPE = register(DimensionalDoors.id(RelativeReferenceModifier.KEY), RelativeReferenceModifier::new);
		ModifierType<OffsetModifier> OFFSET_MODIFIER_TYPE = register(DimensionalDoors.id(OffsetModifier.KEY), OffsetModifier::new);
		ModifierType<AbsoluteRiftBlockEntityModifier> ABSOLUTE_RIFT_BLOCK_ENTITY_MODIFIER_TYPE = register(DimensionalDoors.id(AbsoluteRiftBlockEntityModifier.KEY), AbsoluteRiftBlockEntityModifier::new);

		Modifier fromNbt(NbtCompound nbt, ResourceManager manager);

		default Modifier fromNbt(NbtCompound nbt) {
			return fromNbt(nbt, null);
		}

		NbtCompound toNbt(NbtCompound nbt);

		static void register() {
			DimensionalDoors.apiSubscribers.forEach(d -> d.registerModifierTypes(REGISTRY));
		}

		static <U extends Modifier> ModifierType<U> register(Identifier id, Supplier<U> factory) {
			return Registry.register(REGISTRY, id, new ModifierType<U>() {
				@Override
				public Modifier fromNbt(NbtCompound nbt, ResourceManager manager) {
					return factory.get().fromNbt(nbt, manager);
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
