package org.dimdev.dimdoors.shared.world;

import lombok.Getter;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import org.dimdev.dimdoors.shared.ModConfig;
import org.dimdev.dimdoors.shared.world.limbo.WorldProviderLimbo;
import org.dimdev.dimdoors.shared.world.pocketdimension.WorldProviderDungeonPocket;
import org.dimdev.dimdoors.shared.world.pocketdimension.WorldProviderPersonalPocket;
import org.dimdev.dimdoors.shared.world.pocketdimension.WorldProviderPublicPocket;

public final class ModDimensions {

    // These are ok to register on class load, the DimensionType.register method only adds an enum value to DimensionTypes
    // Lowercase names because all minecraft dimensions are lowercase, _pockets suffix to make it clear what the world is
    public static final DimensionType LIMBO = DimensionType.register("limbo", "_limbo", 684, WorldProviderLimbo.class, false);
    public static final DimensionType PRIVATE = DimensionType.register("private_pockets", "_private", 685, WorldProviderPersonalPocket.class, false);
    public static final DimensionType PUBLIC = DimensionType.register("public_pockets", "_public", 686, WorldProviderPublicPocket.class, false);
    public static final DimensionType DUNGEON = DimensionType.register("dungeon_pockets", "_dungeon", 687, WorldProviderDungeonPocket.class, false);

    @Getter private static int limboDim;
    @Getter private static int privateDim;
    @Getter private static int publicDim;
    @Getter private static int dungeonDim;
    @Getter private static int dungeonMakingDim;

    @SuppressWarnings("UnusedAssignment")
    public static void registerDimensions() {
        // TODO: more than 1 dimension/dimension type
        int dim = ModConfig.general.baseDimensionID;
        limboDim = dim++;
        privateDim = dim++;
        publicDim = dim++;
        dungeonDim = dim++;
        dungeonMakingDim = dim++;
        DimensionManager.registerDimension(limboDim, LIMBO);
        DimensionManager.registerDimension(privateDim, PRIVATE);
        DimensionManager.registerDimension(publicDim, PUBLIC);
        DimensionManager.registerDimension(dungeonDim, DUNGEON);
        DimensionManager.registerDimension(dungeonMakingDim, DUNGEON);
    }

    public static boolean isDimDoorsPocketDimension(int id) {
        return id == privateDim || id == publicDim || id == dungeonDim;
    }

    public static boolean isDimDoorsPocketDimension(World world) {
        return world.provider instanceof WorldProviderPublicPocket
               || world.provider instanceof WorldProviderPersonalPocket
               || world.provider instanceof WorldProviderDungeonPocket;
    }
}
