package org.dimdev.dimdoors.pockets.modifier;

import com.alcatrazescapee.cyanide.codec.Codecs;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import dev.architectury.platform.Mod;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.pockets.PocketLoader;
import org.dimdev.dimdoors.util.CodecUtils;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public interface Modifier {
	Registrar<ModifierType<? extends Modifier>> REGISTRY = RegistrarManager.get(DimensionalDoors.MOD_ID).<ModifierType<? extends Modifier>>builder(DimensionalDoors.id("modifier_type")).build();

	Codec<Modifier> CODEC_BASE = ResourceLocation.CODEC.<ModifierType<?>>xmap(REGISTRY::get, REGISTRY::getId).dispatch(Modifier::getType, ModifierType::mapCodec);
	Codec<Modifier> CODEC = CodecUtils.codecWithMapFallback(CODEC_BASE, a -> PocketLoader.getInstance().getModifier(a));
	Codec<List<Modifier>> LIST_CODEC = Codec.PASSTHROUGH.<List<Modifier>>flatXmap(new Function<Dynamic<?>, DataResult<List<Modifier>>>() {
		@Override
		public DataResult<List<Modifier>> apply(Dynamic<?> dynamic) {
			DataResult<List<Modifier>> list = dynamic.asStreamOpt().map(stream -> {
				return stream.map(CODEC::parse).filter(a -> {
					return a.isSuccess();
				}).map(a -> {
					return a.result();
				}).filter(a -> {
					return a.isPresent();
				}).map(a -> {
					return a.get();
				}).collect(Collectors.toCollection(ArrayList::new));
			});

			return list;
		}
	}, new Function<List<Modifier>, DataResult<Dynamic<?>>>() {
		@Override
		public DataResult<Dynamic<?>> apply(List<Modifier> modifierList) {
			return DataResult.error(() -> "No encoding!");
		}
	});

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
		public static final RegistrySupplier<ModifierType<OffsetModifier>> OFFSET_MODIFIER_TYPE = register(DimensionalDoors.id(OffsetModifier.KEY), OffsetModifier.CODEC);
		public static final RegistrySupplier<ModifierType<AbsoluteRiftBlockEntityModifier>> ABSOLUTE_RIFT_BLOCK_ENTITY_MODIFIER_TYPE = register(DimensionalDoors.id(AbsoluteRiftBlockEntityModifier.KEY), AbsoluteRiftBlockEntityModifier.CODEC);

		public static final RegistrySupplier<ModifierType<TemplateModifier>> TEMPLATE_MODIFIER_TYPE = register(DimensionalDoors.id(TemplateModifier.KEY), TemplateModifier.CODEC); //TODO: Renable

		public static void register() {

		}

		static <U extends Modifier> RegistrySupplier<ModifierType<U>> register(ResourceLocation id, MapCodec<U> mapCodec) {
			return REGISTRY.register(id, () -> new ModifierType<>(mapCodec));
		}
	}
}
