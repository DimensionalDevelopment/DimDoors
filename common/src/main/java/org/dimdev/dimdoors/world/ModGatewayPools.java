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

    public static void bootstrap(BootstapContext<StructureTemplatePool> context) {
        var empty = context.lookup(Registries.TEMPLATE_POOL).getOrThrow(EMPTY);

        context.register(TWO_PILLARS, new StructureTemplatePool(
                empty,
                ImmutableList.of(
                        Pair.of(StructurePoolElement.single("dimdoors:gateways/two_pillars"), 50)),
                StructureTemplatePool.Projection.RIGID));
    }

}
