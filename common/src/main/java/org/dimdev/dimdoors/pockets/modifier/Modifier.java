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
import org.dimdev.dimdoors.api.util.ResourceUtil;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.pockets.PocketLoader;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

import java.util.function.Function;

public interface Modifier {
	Codec<Modifier> CODEC = Codec.unit(Modifier::new);
	Registrar<ModifierType<? extends Modifier>> REGISTRY = RegistrarManager.get(DimensionalDoors.MOD_ID).<ModifierType<? extends Modifier>>builder(DimensionalDoors.id("modifier_type")).build();

	String RESOURCE_STARTING_PATH = "pockets/modifier"; //TODO: might want to restructure data packs

	Codec<Modifier> STRING_CODEC = Codec.STRING.xmap(s -> PocketLoader.getInstance().getModifiers().getOrDefault(s, NoneModifer.INSTANCE), new Function<Modifier, String>() {
		@Override
		public String apply(Modifier modifier) {
			throw new RuntimeException("Serialization of modifier reference not supported.");
		}
	});

	Codec<Modifier> CODEC = ResourceLocation.CODEC.dispatch(modifier -> REGISTRY.getId(modifier.getType()), resourceLocation -> REGISTRY.get(resourceLocation).mapCodec());

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


	default Tag toNbt(CompoundTag nbt, boolean allowReference) {
		return this.getType().toNbt(nbt);
	}

	String getResourceKey();

	ModifierType<? extends Modifier> getType();

	void apply(PocketGenerationContext parameters, RiftManager manager);

	void apply(PocketGenerationContext parameters, Pocket.PocketBuilder<?, ?> builder);

	record ModifierType<T extends Modifier>(MapCodec<T> mapCodec) {
		public static final RegistrySupplier<ModifierType<NoneModifer>> NONE_MODIFIER_TYPE = register(DimensionalDoors.id(NoneModifer.KEY), NoneModifer.CODEC);
		public static final RegistrySupplier<ModifierType<ShellModifier>> SHELL_MODIFIER_TYPE = register(DimensionalDoors.id(ShellModifier.KEY), ShellModifier.CODEC);
		public static final RegistrySupplier<ModifierType<DimensionalDoorModifier>> DIMENSIONAL_DOOR_MODIFIER_TYPE = register(DimensionalDoors.id(DimensionalDoorModifier.KEY), DimensionalDoorModifier.CODEC);
		public static final RegistrySupplier<ModifierType<PocketEntranceModifier>> PUBLIC_MODIFIER_TYPE = register(DimensionalDoors.id(PocketEntranceModifier.KEY), PocketEntranceModifier.CODEC);
		public static final RegistrySupplier<ModifierType<RiftDataModifier>> RIFT_DATA_MODIFIER_TYPE = register(DimensionalDoors.id(RiftDataModifier.KEY), RiftDataModifier.CODEC);
		public static final RegistrySupplier<ModifierType<RelativeReferenceModifier>> RELATIVE_REFERENCE_MODIFIER_TYPE = register(DimensionalDoors.id(RelativeReferenceModifier.KEY), RelativeReferenceModifier.CODEC);
		public static final RegistrySupplier<ModifierType<OffsetModifier>> OFFSET_MODIFIER_TYPE = register(DimensionalDoors.id(AbsoluteRiftBlockEntityModifier.KEY), OffsetModifier.CODEC);
//		public static final RegistrySupplier<ModifierType<AbsoluteRiftBlockEntityModifier>> ABSOLUTE_RIFT_BLOCK_ENTITY_MODIFIER_TYPE = register(DimensionalDoors.id(AbsoluteRiftBlockEntityModifier.KEY), AbsoluteRiftBlockEntityModifier.CODEC); TODO: Reenable

//		public static final RegistrySupplier<ModifierType<TemplateModifier>> TEMPLATE_MODIFIER_TYPE = register(DimensionalDoors.id(TemplateModifier.KEY), TemplateModifier.CODEC); //TODO: Renable

		public static void register() {}

		static <U extends Modifier> RegistrySupplier<ModifierType<U>> register(ResourceLocation id, MapCodec<U> mapCodec) {
			return REGISTRY.register(id, () -> new ModifierType<>(mapCodec));
		}
	}
}
