package org.dimdev.dimdoors.pockets.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.ModRegistries;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;

public interface Modifier {
    Codec<Modifier> BASE_CODEC = ModifierType.CODEC.dispatch(Modifier::getType, ModifierType::codec);
    Codec<HolderSet<Modifier>> LIST_CODEC = RegistryCodecs.homogeneousList(ModRegistries.MODIFIERS, BASE_CODEC);

    ModifierType<? extends Modifier> getType();

    void apply(PocketGenerationContext parameters, RiftManager manager);

	void apply(PocketGenerationContext parameters, Pocket.PocketBuilder<?, ?> builder);

	public static record ModifierType<T extends Modifier>(MapCodec<T> codec) {
        public static final Registrar<ModifierType<? extends Modifier>> REGISTRY = RegistrarManager.get(DimensionalDoors.MOD_ID).<ModifierType<? extends Modifier>>builder(DimensionalDoors.id("modifier_type")).build();

        public static final Codec<ModifierType<?>> CODEC = ResourceLocation.CODEC.xmap(REGISTRY::get, REGISTRY::getId);

		public static final RegistrySupplier<ModifierType<ShellModifier>> SHELL_MODIFIER_TYPE = register(DimensionalDoors.id(ShellModifier.KEY), ShellModifier.CODEC);
        public static final RegistrySupplier<ModifierType<DimensionalDoorModifier>> DIMENSIONAL_DOOR_MODIFIER_TYPE = register(DimensionalDoors.id(DimensionalDoorModifier.KEY), DimensionalDoorModifier.CODEC);
        public static final RegistrySupplier<ModifierType<PocketEntranceModifier>> PUBLIC_MODIFIER_TYPE = register(DimensionalDoors.id(PocketEntranceModifier.KEY), PocketEntranceModifier.CODEC);
        public static final RegistrySupplier<ModifierType<RiftDataModifier>> RIFT_DATA_MODIFIER_TYPE = register(DimensionalDoors.id(RiftDataModifier.KEY), RiftDataModifier.CODEC);
        public static final RegistrySupplier<ModifierType<RelativeReferenceModifier>> RELATIVE_REFERENCE_MODIFIER_TYPE = register(DimensionalDoors.id(RelativeReferenceModifier.KEY), RelativeReferenceModifier.CODEC);
        public static final RegistrySupplier<ModifierType<OffsetModifier>> OFFSET_MODIFIER_TYPE = register(DimensionalDoors.id(OffsetModifier.KEY), OffsetModifier.CODEC);
//        public static final RegistrySupplier<ModifierType<Modifier>> ABSOLUTE_RIFT_BLOCK_ENTITY_MODIFIER_TYPE = register(DimensionalDoors.id(AbsoluteRiftBlockEntityModifier.KEY), AbsoluteRiftBlockEntityModifier::new);

        public static final RegistrySupplier<ModifierType<TemplateModifier>> TEMPLATE_MODIFIER_TYPE = register(DimensionalDoors.id(TemplateModifier.KEY), TemplateModifier.CODEC);

        public static void register() {}

		static <U extends Modifier> RegistrySupplier<ModifierType<U>> register(ResourceLocation id, MapCodec<U> codec) {
			return REGISTRY.register(id, () -> new ModifierType<U>(codec));
		}
	}
}