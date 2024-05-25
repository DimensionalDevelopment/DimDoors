package org.dimdev.dimdoors.pockets.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.util.CodecUtil;
import org.dimdev.dimdoors.api.util.ResourceUtil;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

import java.util.function.Supplier;

public interface Modifier {
	String RESOURCE_STARTING_PATH = "pockets/modifier"; //TODO: might want to restructure data packs
	Registrar<ModifierType<? extends Modifier>> REGISTRY = RegistrarManager.get(DimensionalDoors.MOD_ID).<ModifierType<? extends Modifier>>builder(DimensionalDoors.id("modifier_type")).build();
	Codec<Modifier> CODEC = CodecUtil.registrarCodec(RESOURCE_STARTING_PATH, REGISTRY, Modifier::getType, ModifierType::mapCodec, Modifier::codec);


	static Codec<Modifier> codec() {
		return CODEC;
	}

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

	String getResourceKey();

	ModifierType<? extends Modifier> getType();

	String getKey();

	void apply(PocketGenerationContext parameters, RiftManager manager);

	void apply(PocketGenerationContext parameters, Pocket.PocketBuilder<?, ?> builder);

	interface ModifierType<T extends Modifier> {
		RegistrySupplier<ModifierType<ShellModifier>> SHELL_MODIFIER_TYPE = register(DimensionalDoors.id(ShellModifier.KEY), ShellModifier::new, ShellModifier.CODEC);
		RegistrySupplier<ModifierType<DimensionalDoorModifier>> DIMENSIONAL_DOOR_MODIFIER_TYPE = register(DimensionalDoors.id(DimensionalDoorModifier.KEY), DimensionalDoorModifier::new, mapCodec);
		RegistrySupplier<ModifierType<PocketEntranceModifier>> PUBLIC_MODIFIER_TYPE = register(DimensionalDoors.id(PocketEntranceModifier.KEY), PocketEntranceModifier::new, mapCodec);
		RegistrySupplier<ModifierType<RiftDataModifier>> RIFT_DATA_MODIFIER_TYPE = register(DimensionalDoors.id(RiftDataModifier.KEY), RiftDataModifier::new, mapCodec);
		RegistrySupplier<ModifierType<RelativeReferenceModifier>> RELATIVE_REFERENCE_MODIFIER_TYPE = register(DimensionalDoors.id(RelativeReferenceModifier.KEY), RelativeReferenceModifier::new, mapCodec);
		RegistrySupplier<ModifierType<OffsetModifier>> OFFSET_MODIFIER_TYPE = register(DimensionalDoors.id(OffsetModifier.KEY), OffsetModifier::new, mapCodec);
		RegistrySupplier<ModifierType<AbsoluteRiftBlockEntityModifier>> ABSOLUTE_RIFT_BLOCK_ENTITY_MODIFIER_TYPE = register(DimensionalDoors.id(AbsoluteRiftBlockEntityModifier.KEY), AbsoluteRiftBlockEntityModifier::new, mapCodec);

		RegistrySupplier<ModifierType<TemplateModifier>> TEMPLATE_MODIFIER_TYPE = register(DimensionalDoors.id(TemplateModifier.KEY), TemplateModifier::new, mapCodec);

		Modifier fromNbt(CompoundTag nbt, ResourceManager manager);

		default Modifier fromNbt(CompoundTag nbt) {
			return fromNbt(nbt, null);
		}

		CompoundTag toNbt(CompoundTag nbt);

		MapCodec<ShellModifier> mapCodec();

		static void register() {}

		static <U extends Modifier> RegistrySupplier<ModifierType<U>> register(ResourceLocation id, Supplier<U> factory, MapCodec<ShellModifier> mapCodec) {
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

				@Override
				public MapCodec<ShellModifier> mapCodec() {
					return mapCodec;
				}
			});
		}
	}
}
