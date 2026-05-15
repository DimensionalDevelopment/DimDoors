package org.dimdev.dimdoors;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.block.entity.RiftData;
import org.dimdev.dimdoors.item.door.data.condition.Condition;
import org.dimdev.dimdoors.pockets.generator.PocketGenerator;
import org.dimdev.dimdoors.pockets.modifier.Modifier;
import org.dimdev.dimdoors.pockets.virtual.ImplementedVirtualPocket;
import org.dimdev.dimdoors.pockets.virtual.VirtualPocket;
import org.dimdev.dimdoors.world.pocket.type.AbstractPocket;

public class ModRegistryKeys {
    public static final ResourceKey<Registry<PocketGenerator.PocketGeneratorType<?>>> POCKET_GENERATOR_TYPE = ResourceKey.createRegistryKey(DimensionalDoors.id("pocket_generator_type"));
    public static final ResourceKey<Registry<AbstractPocket.AbstractPocketType<? extends AbstractPocket<?, ?>, ? extends AbstractPocket.AbstractPocketBuilder<?, ?>>>> POCKET_TYPE = ResourceKey.createRegistryKey(DimensionalDoors.id("abstract_pocket_type"));
    public static final ResourceKey<Registry<ImplementedVirtualPocket.VirtualPocketType<?>>> VIRTUAL_POCKET_TYPE = ResourceKey.createRegistryKey(DimensionalDoors.id("virtual_pocket_type"));
    public static final ResourceKey<Registry<Modifier.ModifierType<?>>> MODIFIER_TYPE = ResourceKey.createRegistryKey(DimensionalDoors.id("virtual_pocket_type"));
    public static final ResourceKey<Registry<Condition.ConditionType<?>>> CONDITION_TYPE = ResourceKey.createRegistryKey(DimensionalDoors.id("rift_data_condition"));
    public static final ResourceKey<Registry<Modifier>> MODIFIER = ResourceKey.createRegistryKey(DimensionalDoors.id("virtual_pocket_type"));
    public static final ResourceKey<Registry<PocketGenerator<?>>> POCKET_GENERATOR = ResourceKey.createRegistryKey(ResourceLocation.withDefaultNamespace("pockets/generators"));
    public static final ResourceKey<Registry<VirtualPocket>> VIRTUAL_POCKET = ResourceKey.createRegistryKey(ResourceLocation.withDefaultNamespace("pockets/virtual"));
    public static final ResourceKey<Registry<VirtualPocket>> POCKET_GROUPS = ResourceKey.createRegistryKey(ResourceLocation.withDefaultNamespace("pockets/groups"));
    public static final ResourceKey<Registry<RiftData>> RIFT_DATA = ResourceKey.createRegistryKey(ResourceLocation.withDefaultNamespace("pockets/rift_data"));


    public static void register() {

    }
}
