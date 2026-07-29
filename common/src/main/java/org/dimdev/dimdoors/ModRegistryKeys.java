package org.dimdev.dimdoors;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.block.entity.RiftData;
import org.dimdev.dimdoors.item.door.data.RiftDataList;
import org.dimdev.dimdoors.item.door.data.condition.Condition;
import org.dimdev.dimdoors.pockets.generator.PocketGenerator;
import org.dimdev.dimdoors.pockets.modifier.Modifier;
import org.dimdev.dimdoors.pockets.virtual.ImplementedVirtualPocket;
import org.dimdev.dimdoors.pockets.virtual.VirtualPocket;
import org.dimdev.dimdoors.rift.registry.SubSystem;
import org.dimdev.dimdoors.rift.targets.VirtualTarget;
import org.dimdev.dimdoors.world.pocket.type.AbstractPocket;

public class ModRegistryKeys {
    public static final ResourceKey<Registry<PocketGenerator.PocketGeneratorType<?>>> POCKET_GENERATOR_TYPE = createType("pocket_generator_type");
    public static final ResourceKey<Registry<AbstractPocket.AbstractPocketType<?, ?>>> POCKET_TYPE = createType("abstract_pocket_type");
    public static final ResourceKey<Registry<ImplementedVirtualPocket.VirtualPocketType<?>>> VIRTUAL_POCKET_TYPE = createType("virtual_pocket_type");
    public static final ResourceKey<Registry<VirtualTarget.VirtualTargetType<?>>> VIRTUAL_TYPE = createType("virtual_type");
    public static final ResourceKey<Registry<Modifier.ModifierType<?>>> MODIFIER_TYPE = createType("modifier_type");
    public static final ResourceKey<Registry<Condition.ConditionType<?>>> CONDITION_TYPE = createType("rift_data_condition");
    public static final ResourceKey<Registry<SubSystem.Type<?>>> SUBSYSTEM_TYPE = createType("subsystem_type");
    public static final ResourceKey<Registry<Modifier>> MODIFIER = createType("virtual_pocket_type");
    public static final ResourceKey<Registry<PocketGenerator<?>>> POCKET_GENERATOR = createDynamic("pockets/generators");
    public static final ResourceKey<Registry<VirtualPocket>> VIRTUAL_POCKET = createDynamic("pockets/virtual");
    public static final ResourceKey<Registry<VirtualPocket>> POCKET_GROUPS = createDynamic("pockets/groups");
    public static final ResourceKey<Registry<RiftData>> RIFT_DATA = createDynamic("pockets/rift_data");
    public static final ResourceKey<Registry<RiftDataList>> DOOR_DATA = createDynamic("door/data");

    private static <T> ResourceKey<Registry<T>> createDynamic(String name) {
        return ResourceKey.createRegistryKey(ResourceLocation.withDefaultNamespace(name));
    }

    private static <T> ResourceKey<Registry<T>> createType(String name) {
        return ResourceKey.createRegistryKey(DimensionalDoors.id(name));
    }

    public static void register() {

    }
}
