package org.dimdev.dimdoors.world;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import org.dimdev.dimdoors.DimensionalDoors;

import static net.minecraft.data.worldgen.Pools.EMPTY;

public class ModGatewayPools {
    public static ResourceKey<StructureTemplatePool> TWO_PILLARS = ResourceKey.create(Registries.TEMPLATE_POOL, DimensionalDoors.id("two_pillars"));
    public static ResourceKey<StructureTemplatePool> ICE_PILLARS = ResourceKey.create(Registries.TEMPLATE_POOL, DimensionalDoors.id("ice_pillars"));
    public static ResourceKey<StructureTemplatePool> SANDSTONE_PILLARS = ResourceKey.create(Registries.TEMPLATE_POOL, DimensionalDoors.id("sandstone_pillars"));
    public static ResourceKey<StructureTemplatePool> RED_SANDSTONE_PILLARS = ResourceKey.create(Registries.TEMPLATE_POOL, DimensionalDoors.id("red_sandstone_pillars"));

    public static void bootstrap(BootstapContext<StructureTemplatePool> context) {
        var empty = context.lookup(Registries.TEMPLATE_POOL).getOrThrow(EMPTY);
        var processorLists = context.lookup(Registries.PROCESSOR_LIST);

        var dungeon = processorLists.getOrThrow(ModProcessorLists.DUNGEON);

        context.register(TWO_PILLARS, new StructureTemplatePool(empty, ImmutableList.of(Pair.of(StructurePoolElement.single("dimdoors:gateways/two_pillars", dungeon), 50)), StructureTemplatePool.Projection.RIGID));
        context.register(SANDSTONE_PILLARS, new StructureTemplatePool(empty, ImmutableList.of(Pair.of(StructurePoolElement.single("dimdoors:gateways/sandstone_pillars", dungeon), 50)), StructureTemplatePool.Projection.RIGID));
        context.register(RED_SANDSTONE_PILLARS, new StructureTemplatePool(empty, ImmutableList.of(Pair.of(StructurePoolElement.single("dimdoors:gateways/red_sandstone_pillars", dungeon), 50)), StructureTemplatePool.Projection.RIGID));
        context.register(ICE_PILLARS, new StructureTemplatePool(empty, ImmutableList.of(Pair.of(StructurePoolElement.single("dimdoors:gateways/red_sandstone_pillars", dungeon), 50)), StructureTemplatePool.Projection.RIGID));
    }

}
