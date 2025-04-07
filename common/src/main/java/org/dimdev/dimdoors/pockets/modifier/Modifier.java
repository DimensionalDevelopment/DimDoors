package org.dimdev.dimdoors.pockets.modifier;

import com.google.common.collect.Multimap;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.util.ReferenceSerializable;
import org.dimdev.dimdoors.api.util.ResourceUtil;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

import java.util.Collection;
import java.util.function.Supplier;

public interface Modifier extends ReferenceSerializable {
	Registrar<ModifierType<? extends Modifier>> REGISTRY = RegistrarManager.get(DimensionalDoors.MOD_ID).<ModifierType<? extends Modifier>>builder(DimensionalDoors.id("modifier_type")).build();

	String RESOURCE_STARTING_PATH = "pockets/modifier"; //TODO: might want to restructure data packs

	static Modifier deserialize(Tag nbt, ResourceManager manager) {
		return switch (nbt.getId()) {
			case Tag.TAG_COMPOUND -> // It's a serialized Modifier
					Modifier.deserialize((CompoundTag) nbt, manager);
			case Tag.TAG_STRING -> // It's a reference to a resource location
				// TODO: throw if manager is null
					ResourceUtil.loadReferencedResource(manager, RESOURCE_STARTING_PATH, nbt.getAsString(), ResourceUtil.NBT_READER.andThenComposable(Tag -> deserialize(Tag, manager)));
			default -> throw new RuntimeException(String.format("Unexpected NbtType %d!", nbt.getId()));
		};
	}

	static Modifier deserialize(Tag nbt) {
		return deserialize(nbt, null);
	}

	static Modifier deserialize(CompoundTag nbt, ResourceManager manager) {
		ResourceLocation id = ResourceLocation.tryParse(nbt.getString("type")); // TODO: return some NONE Modifier if type cannot be found or deserialization fails.
		return REGISTRY.get(id).fromNbt(nbt, manager);
	}

	static Modifier deserialize(CompoundTag nbt) {
		return deserialize(nbt, null);
	}

	static Tag serialize(Modifier modifier, boolean allowReference) {
		return modifier.toNbt(new CompoundTag(), allowReference);
	}

	static Tag serialize(Modifier modifier) {
		return serialize(modifier, false);
	}


	Modifier fromNbt(CompoundTag nbt, ResourceManager manager);

	default Modifier fromNbt(CompoundTag nbt) {
		return fromNbt(nbt, null);
	}

	default Tag toNbt(CompoundTag nbt, boolean allowReference) {
		return this.getType().toNbt(nbt);
	}

	default Tag toNbt(CompoundTag nbt) {
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
		RegistrySupplier<ModifierType<ShellModifier>> SHELL_MODIFIER_TYPE = register(DimensionalDoors.id(ShellModifier.KEY), ShellModifier::new);
		RegistrySupplier<ModifierType<DimensionalDoorModifier>> DIMENSIONAL_DOOR_MODIFIER_TYPE = register(DimensionalDoors.id(DimensionalDoorModifier.KEY), DimensionalDoorModifier::new);
		RegistrySupplier<ModifierType<Modifier>> PUBLIC_MODIFIER_TYPE = register(DimensionalDoors.id(PocketEntranceModifier.KEY), PocketEntranceModifier::new);
		RegistrySupplier<ModifierType<RiftDataModifier>> RIFT_DATA_MODIFIER_TYPE = register(DimensionalDoors.id(RiftDataModifier.KEY), RiftDataModifier::new);
		RegistrySupplier<ModifierType<RelativeReferenceModifier>> RELATIVE_REFERENCE_MODIFIER_TYPE = register(DimensionalDoors.id(RelativeReferenceModifier.KEY), RelativeReferenceModifier::new);
		RegistrySupplier<ModifierType<OffsetModifier>> OFFSET_MODIFIER_TYPE = register(DimensionalDoors.id(OffsetModifier.KEY), OffsetModifier::new);
		RegistrySupplier<ModifierType<Modifier>> ABSOLUTE_RIFT_BLOCK_ENTITY_MODIFIER_TYPE = register(DimensionalDoors.id(AbsoluteRiftBlockEntityModifier.KEY), AbsoluteRiftBlockEntityModifier::new);

		RegistrySupplier<ModifierType<TemplateModifier>> TEMPLATE_MODIFIER_TYPE = register(DimensionalDoors.id(TemplateModifier.KEY), TemplateModifier::new);

		Modifier fromNbt(CompoundTag nbt, ResourceManager manager);

		default Modifier fromNbt(CompoundTag nbt) {
			return fromNbt(nbt, null);
		}

		CompoundTag toNbt(CompoundTag nbt);

		static void register() {}

		static <U extends Modifier> RegistrySupplier<ModifierType<U>> register(ResourceLocation id, Supplier<U> factory) {
			return REGISTRY.register(id, () -> new ModifierType<U>() {
				@Override
				public Modifier fromNbt(CompoundTag nbt, ResourceManager manager) {
					return factory.get().fromNbt(nbt, manager);
				}

				@Override
				public CompoundTag toNbt(CompoundTag nbt) {
					nbt.putString("type", id.toString());
					return nbt;
				}
			});
		}
	}
}
