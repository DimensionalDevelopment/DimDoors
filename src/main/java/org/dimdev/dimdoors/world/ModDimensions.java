package org.dimdev.dimdoors.world;

import net.fabricmc.fabric.api.dimension.v1.FabricDimensionType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.dimdev.dimdoors.world.limbo.LimboDimension;
import org.dimdev.dimdoors.world.pocketdimension.DungeonPocketDimension;
import org.dimdev.dimdoors.world.pocketdimension.PersonalPocketDimension;
import org.dimdev.dimdoors.world.pocketdimension.PublicPocketDimension;

public final class ModDimensions {
    public static final DimensionType LIMBO = FabricDimensionType.builder().factory(LimboDimension::new).defaultPlacer(new LimboEntityPlacer()).buildAndRegister(new Identifier("dimdoors:limbo"));
    public static final DimensionType PERSONAL = FabricDimensionType.builder().factory(PersonalPocketDimension::new).defaultPlacer(new PocketDimensionPlacer()).buildAndRegister(new Identifier("dimdoors:personal_pockets"));
    public static final DimensionType PUBLIC = FabricDimensionType.builder().factory(PublicPocketDimension::new).defaultPlacer(new PocketDimensionPlacer()).buildAndRegister(new Identifier("dimdoors:public_pockets"));
    public static final DimensionType DUNGEON = FabricDimensionType.builder().factory(DungeonPocketDimension::new).defaultPlacer(new PocketDimensionPlacer()).buildAndRegister(new Identifier("dimdoors:dungeon_pockets"));

    public static boolean isDimDoorsPocketDimension(World world) {
        return world.dimension instanceof PublicPocketDimension
               || world.dimension instanceof PersonalPocketDimension
               || world.dimension instanceof DungeonPocketDimension;
    }

    public static void init() {
        // just loads the class
    }
}
