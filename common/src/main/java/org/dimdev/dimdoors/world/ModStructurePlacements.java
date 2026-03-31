package org.dimdev.dimdoors.world;

import com.mojang.serialization.MapCodec;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.world.structure.PocketPlacement;

public class ModStructurePlacements {
    public static final Registrar<StructurePlacementType<?>> STRUCTURE_PLACEMENTS = RegistrarManager.get(DimensionalDoors.MOD_ID).get(Registries.STRUCTURE_PLACEMENT);

    public static final RegistrySupplier<StructurePlacementType<PocketPlacement>> POCKET = register("pocket", MapCodec.unit(PocketPlacement.INSTANCE));

    public static void register() {

    }

    private static <T extends StructurePlacement> RegistrySupplier<StructurePlacementType<T>> register(String name, MapCodec<T> codec) {
        return STRUCTURE_PLACEMENTS.register(DimensionalDoors.id(name), () -> () -> codec);
    }
}
