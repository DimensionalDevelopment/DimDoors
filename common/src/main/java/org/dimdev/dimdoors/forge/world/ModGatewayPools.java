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
    public static ResourceKey<StructureTemplatePool> ENCLOSED_GATEWAY = key("enclosed_gateway");
    public static ResourceKey<StructureTemplatePool> ENCLOSED_ENDSTONE_GATEWAY = key("enclosed_endstone_gateway");
    public static ResourceKey<StructureTemplatePool> ENCLOSED_MUD_GATEWAY = key("enclosed_mud_gateway");
    public static ResourceKey<StructureTemplatePool> ENCLOSED_PRISMARINE_GATEWAY = key("enclosed_prismarine_gateway");
    public static ResourceKey<StructureTemplatePool> ENCLOSED_QUARTZ_GATEWAY = key("enclosed_quartz_gateway");
    public static ResourceKey<StructureTemplatePool> ENCLOSED_RED_SANDSTONE_GATEWAY = key("enclosed_red_sandstone_gateway");
    public static ResourceKey<StructureTemplatePool> ENCLOSED_SANDSTONE_GATEWAY = key("enclosed_sandstone_gateway");
//    public static ResourceKey<StructureTemplatePool> LIMBO_GATEWAY = key("limbo_gateway");

    private static ResourceKey<StructureTemplatePool> key(String name) {
        return ResourceKey.create(Registries.TEMPLATE_POOL, DimensionalDoors.id(name));
    }

    public static void bootstrap(BootstapContext<StructureTemplatePool> context) {
        var empty = context.lookup(Registries.TEMPLATE_POOL).getOrThrow(EMPTY);
        var processorLists = context.lookup(Registries.PROCESSOR_LIST);

        var dungeon = processorLists.getOrThrow(ModProcessorLists.DUNGEON);
        context.register(ENCLOSED_GATEWAY, new StructureTemplatePool(empty, ImmutableList.of(Pair.of(StructurePoolElement.single("dimdoors:gateways/enclosed", dungeon), 50)), StructureTemplatePool.Projection.RIGID));
        context.register(ENCLOSED_ENDSTONE_GATEWAY, new StructureTemplatePool(empty, ImmutableList.of(Pair.of(StructurePoolElement.single("dimdoors:gateways/enclosed_endstone", dungeon), 50)), StructureTemplatePool.Projection.RIGID));
        context.register(ENCLOSED_MUD_GATEWAY, new StructureTemplatePool(empty, ImmutableList.of(Pair.of(StructurePoolElement.single("dimdoors:gateways/enclosed_mud", dungeon), 50)), StructureTemplatePool.Projection.RIGID));
        context.register(ENCLOSED_PRISMARINE_GATEWAY, new StructureTemplatePool(empty, ImmutableList.of(Pair.of(StructurePoolElement.single("dimdoors:gateways/enclosed_prismarine", dungeon), 50)), StructureTemplatePool.Projection.RIGID));
        context.register(ENCLOSED_QUARTZ_GATEWAY, new StructureTemplatePool(empty, ImmutableList.of(Pair.of(StructurePoolElement.single("dimdoors:gateways/enclosed_quartz", dungeon), 50)), StructureTemplatePool.Projection.RIGID));
        context.register(ENCLOSED_RED_SANDSTONE_GATEWAY, new StructureTemplatePool(empty, ImmutableList.of(Pair.of(StructurePoolElement.single("dimdoors:gateways/enclosed_red_sandstone", dungeon), 50)), StructureTemplatePool.Projection.RIGID));
        context.register(ENCLOSED_SANDSTONE_GATEWAY, new StructureTemplatePool(empty, ImmutableList.of(Pair.of(StructurePoolElement.single("dimdoors:gateways/enclosed_sandstone", dungeon), 50)), StructureTemplatePool.Projection.RIGID));
//        context.register(LIMBO_GATEWAY, new StructureTemplatePool(empty, ImmutableList.of(Pair.of(StructurePoolElement.single("dimdoors:gateways/limbo", dungeon), 50)), StructureTemplatePool.Projection.RIGID));

//        context.register(TWO_PILLARS, new StructureTemplatePool(empty, ImmutableList.of(Pair.of(StructurePoolElement.single("dimdoors:gateways/two_pillars", dungeon), 50)), StructureTemplatePool.Projection.RIGID));
//        context.register(SANDSTONE_PILLARS, new StructureTemplatePool(empty, ImmutableList.of(Pair.of(StructurePoolElement.single("dimdoors:gateways/sandstone_pillars", dungeon), 50)), StructureTemplatePool.Projection.RIGID));
//        context.register(RED_SANDSTONE_PILLARS, new StructureTemplatePool(empty, ImmutableList.of(Pair.of(StructurePoolElement.single("dimdoors:gateways/red_sandstone_pillars", dungeon), 50)), StructureTemplatePool.Projection.RIGID));
//        context.register(ICE_PILLARS, new StructureTemplatePool(empty, ImmutableList.of(Pair.of(StructurePoolElement.single("dimdoors:gateways/red_sandstone_pillars", dungeon), 50)), StructureTemplatePool.Projection.RIGID));
    }

}
