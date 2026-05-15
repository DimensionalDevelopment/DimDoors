package org.dimdev.dimdoors.pockets.modifier;

import com.google.common.collect.Multimap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.ModRegistries;
import org.dimdev.dimdoors.ModRegistryKeys;
import org.dimdev.dimdoors.api.util.ReferenceSerializable;
import org.dimdev.dimdoors.api.util.ResourceUtil;
import org.dimdev.dimdoors.pockets.PocketGenerationContext;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Modifier {
    public Codec<Modifier> BASE_CODEC = ModRegistries.MODIFIER_TYPE.byNameCodec().dispatch(Modifier::getType, ModifierType::codec);
    Codec<HolderSet<Modifier>> LIST_CODEC = RegistryCodecs.homogeneousList(ModRegistryKeys.MODIFIER, BASE_CODEC);
    Codec<Holder<Modifier>> HOLDER_CODEC = RegistryFileCodec.create(ModRegistryKeys.MODIFIER, BASE_CODEC);

    ModifierType<? extends Modifier> getType();

    void apply(PocketGenerationContext parameters, RiftManager manager);

    void apply(PocketGenerationContext parameters, Pocket.PocketBuilder<?, ?> builder);

    record ModifierType<T extends Modifier>(MapCodec<T> codec) {
        public static final ModifierType<ShellModifier> SHELL_MODIFIER_TYPE = register(DimensionalDoors.id(ShellModifier.KEY), ShellModifier.CODEC);
        public static final ModifierType<DimensionalDoorModifier> DIMENSIONAL_DOOR_MODIFIER_TYPE = register(DimensionalDoors.id(DimensionalDoorModifier.KEY), DimensionalDoorModifier.CODEC);
        public static final ModifierType<PocketEntranceModifier> PUBLIC_MODIFIER_TYPE = register(DimensionalDoors.id(PocketEntranceModifier.KEY), PocketEntranceModifier.CODEC);
        public static final ModifierType<RiftDataModifier> RIFT_DATA_MODIFIER_TYPE = register(DimensionalDoors.id(RiftDataModifier.KEY), RiftDataModifier.CODEC);
        public static final ModifierType<RelativeReferenceModifier> RELATIVE_REFERENCE_MODIFIER_TYPE = register(DimensionalDoors.id(RelativeReferenceModifier.KEY), RelativeReferenceModifier.CODEC);
        public static final ModifierType<OffsetModifier> OFFSET_MODIFIER_TYPE = register(DimensionalDoors.id(OffsetModifier.KEY), OffsetModifier.CODEC);
//        public static final ModifierType<Modifier> ABSOLUTE_RIFT_BLOCK_ENTITY_MODIFIER_TYPE = register(DimensionalDoors.id(AbsoluteRiftBlockEntityModifier.KEY), AbsoluteRiftBlockEntityModifier.CODEC);

        public static final ModifierType<TemplateModifier> TEMPLATE_MODIFIER_TYPE = register(DimensionalDoors.id(TemplateModifier.KEY), TemplateModifier.CODEC);

        public static void register() {
        }

        static <U extends Modifier> ModifierType<U> register(ResourceLocation id, MapCodec<U> codec) {
            return DimensionalDoors.getSided().register(ModRegistryKeys.MODIFIER_TYPE, id, new ModifierType<U>(codec));
        }
    }
}