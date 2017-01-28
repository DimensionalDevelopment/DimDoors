package com.zixiken.dimdoors.shared.world;

import com.zixiken.dimdoors.shared.world.limbo.WorldProviderLimbo;
import com.zixiken.dimdoors.shared.world.personalpocket.WorldProviderPersonalPocket;
import com.zixiken.dimdoors.shared.world.pocket.WorldProviderPocket;
import com.zixiken.dimdoors.shared.world.pocket.WorldProviderPublicPocket;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;

public class DimDoorDimensions {
    public static DimensionType LIMBO;
    public static DimensionType DUNGEON;
    public static DimensionType PRIVATE;
    public static DimensionType PUBLIC;

    public static void init() {
        LIMBO = DimensionType.register("Limbo", "_limbo", 2, WorldProviderLimbo.class, false);
        PRIVATE = DimensionType.register("Private", "_private", 3, WorldProviderPersonalPocket.class, false); //TODO: Figure out how to consiently get proper dimension ids
        DUNGEON = DimensionType.register("Dungeon", "_dungeon", 4, WorldProviderPocket.class, false);
        PUBLIC = DimensionType.register("Public", "_public", 5, WorldProviderPublicPocket.class, false);

        registerDimension(LIMBO);
        registerDimension(PRIVATE);
        registerDimension(DUNGEON);
        registerDimension(PUBLIC);
    }

    public static void registerDimension(DimensionType dimension) {
        DimensionManager.registerDimension(dimension.getId(), dimension);
    }
}
