package com.zixiken.dimdoors.shared.world;

import com.zixiken.dimdoors.shared.DDConfig;
import com.zixiken.dimdoors.shared.EnumPocketType;
import com.zixiken.dimdoors.shared.world.limbodimension.WorldProviderLimbo;
import com.zixiken.dimdoors.shared.world.pocketdimension.WorldProviderPersonalPocket;
import com.zixiken.dimdoors.shared.world.pocketdimension.WorldProviderPublicPocket;
import com.zixiken.dimdoors.shared.world.pocketdimension.WorldProviderDungeonPocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.common.DimensionManager;

public class DimDoorDimensions {

    public static DimensionType LIMBO;
    private static int minPocketDimID;
    private static int maxPocketDimID;
    private static final List<EnumPocketType> pocketTypes = new ArrayList<>();
    private static final Map<EnumPocketType, DimensionType> pocketDimensionTypes = new HashMap<>();
    private static final List<DimensionType> CUSTOM = new ArrayList<>();

    public static void init() {
        int dimID = DDConfig.getBaseDimID();
        LIMBO = DimensionType.register("Limbo", "_limbo", dimID, WorldProviderLimbo.class, false);
        dimID++; //@todo make this a loop over a function
        minPocketDimID = dimID;
        pocketTypes.add(EnumPocketType.PRIVATE);
        pocketDimensionTypes.put(EnumPocketType.PRIVATE, DimensionType.register("Private", "_private", dimID, WorldProviderPersonalPocket.class, false));
        dimID++;
        pocketTypes.add(EnumPocketType.PUBLIC);
        pocketDimensionTypes.put(EnumPocketType.PUBLIC, DimensionType.register("Public", "_public", dimID, WorldProviderPublicPocket.class, false));
        dimID++;
        maxPocketDimID = dimID;
        pocketTypes.add(EnumPocketType.DUNGEON);
        pocketDimensionTypes.put(EnumPocketType.DUNGEON, DimensionType.register("Dungeon", "_dungeon", dimID, WorldProviderDungeonPocket.class, false));

        registerDimension(LIMBO);
        for (EnumPocketType pocketType : pocketDimensionTypes.keySet()) {
            registerDimension(pocketDimensionTypes.get(pocketType));
        }

        for (int i = 0; i < 0; i++) { //@todo: For future use? Like, server owners can add their own set of DimDoors DimensionTypes via the configs? Or is this nonsense?
            dimID++;
            DimensionType tempType = DimensionType.register("Name", "_name", dimID, WorldProvider.class, false);
            CUSTOM.add(tempType);
            registerDimension(tempType);
        }
    }

    public static void registerDimension(DimensionType dimension) {
        DimensionManager.registerDimension(dimension.getId(), dimension);
    }

    public static DimensionType getPocketDimensionType(EnumPocketType pocketType) {
        return pocketDimensionTypes.get(pocketType);
    }

    public static boolean isPocketDimensionID(int id) {
        return id >= minPocketDimID && id <= maxPocketDimID;
    }

    public static EnumPocketType getPocketType(int dimID) {
        int index = dimID - minPocketDimID;
        return pocketTypes.get(index);
    }
}
