package org.dimdev.dimdoors;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.dimdev.dimdoors.block.entity.RiftData;
import org.dimdev.dimdoors.item.door.data.RiftDataList;
import org.dimdev.dimdoors.item.door.data.condition.Condition;
import org.dimdev.dimdoors.pockets.generator.PocketGenerator;
import org.dimdev.dimdoors.pockets.modifier.Modifier;
import org.dimdev.dimdoors.pockets.virtual.ImplementedVirtualPocket;
import org.dimdev.dimdoors.pockets.virtual.VirtualPocket;
import org.dimdev.dimdoors.rift.targets.VirtualTarget;
import org.dimdev.dimdoors.world.pocket.type.AbstractPocket;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

public class ModRegistries {
    public static final Registry<PocketGenerator.PocketGeneratorType<?>> POCKET_GENERATOR_TYPE = DimensionalDoors.getSided().createRegistry(ModRegistryKeys.POCKET_GENERATOR_TYPE);
    public static final Registry<AbstractPocket.AbstractPocketType<? extends AbstractPocket<?,?>, ? extends AbstractPocket.AbstractPocketBuilder<?, ?>>> POCKET_TYPE = DimensionalDoors.getSided().createRegistry(ModRegistryKeys.POCKET_TYPE);
    public static final Registry<ImplementedVirtualPocket.VirtualPocketType<?>> VIRTUAL_POCKET_TYPE = DimensionalDoors.getSided().createRegistry(ModRegistryKeys.VIRTUAL_POCKET_TYPE);
    public static final Registry<VirtualTarget.VirtualTargetType<?>> VIRTUAL_TYPE = DimensionalDoors.getSided().createRegistry(ModRegistryKeys.VIRTUAL_TYPE);
    public static final Registry<Modifier.ModifierType<?>> MODIFIER_TYPE = DimensionalDoors.getSided().createRegistry(ModRegistryKeys.MODIFIER_TYPE);
    public static final Registry<Condition.ConditionType<?>> CONDITION_TYPE = DimensionalDoors.getSided().createRegistry(ModRegistryKeys.CONDITION_TYPE);

    public static void register() {
        DimensionalDoors.getSided().createDynamicRegistry(ModRegistryKeys.POCKET_GENERATOR, PocketGenerator.CODEC);
        DimensionalDoors.getSided().createDynamicRegistry(ModRegistryKeys.VIRTUAL_POCKET, VirtualPocket.CODEC);
        DimensionalDoors.getSided().createDynamicRegistry(ModRegistryKeys.POCKET_GROUPS, VirtualPocket.CODEC);
        DimensionalDoors.getSided().createDynamicRegistry(ModRegistryKeys.RIFT_DATA, RiftData.CODEC);
        DimensionalDoors.getSided().createDynamicRegistry(ModRegistryKeys.DOOR_DATA, RiftDataList.CODEC);

    }
}
