package com.zixiken.dimdoors.shared.world;

import com.zixiken.dimdoors.shared.DDConfig;
import com.zixiken.dimdoors.shared.world.limbo.WorldProviderLimbo;
import com.zixiken.dimdoors.shared.world.personalpocket.WorldProviderPersonalPocket;
import com.zixiken.dimdoors.shared.world.pocket.WorldProviderPocket;
import com.zixiken.dimdoors.shared.world.pocket.WorldProviderPublicPocket;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;

public class DimDoorDimensions {
    public static DimensionType LIMBO; //@WaterPicker: Why is there no consist ordering...
    public static DimensionType DUNGEON;
    public static DimensionType PRIVATE;
    public static DimensionType PUBLIC;

    public static void init() {
        int dimID = DDConfig.getBaseDimID();
        LIMBO = DimensionType.register("Limbo", "_limbo", dimID, WorldProviderLimbo.class, false);
        dimID++;
        PRIVATE = DimensionType.register("Private", "_private", dimID, WorldProviderPersonalPocket.class, false);
        dimID++;
        DUNGEON = DimensionType.register("Dungeon", "_dungeon", dimID, WorldProviderPocket.class, false);
        dimID++;
        PUBLIC = DimensionType.register("Public", "_public", dimID, WorldProviderPublicPocket.class, false);

        registerDimension(LIMBO); //@WaterPicker: ...in these lists?
        registerDimension(PRIVATE);
        registerDimension(DUNGEON);
        registerDimension(PUBLIC);
    }

    public static void registerDimension(DimensionType dimension) {
        DimensionManager.registerDimension(dimension.getId(), dimension);
    }
}
