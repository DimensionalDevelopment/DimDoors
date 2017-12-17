package com.zixiken.dimdoors.shared.world;

import com.zixiken.dimdoors.shared.DDConfig;
import com.zixiken.dimdoors.shared.pockets.EnumPocketType;
import com.zixiken.dimdoors.shared.world.limbodimension.WorldProviderLimbo;
import com.zixiken.dimdoors.shared.world.pocketdimension.WorldProviderPersonalPocket;
import com.zixiken.dimdoors.shared.world.pocketdimension.WorldProviderPublicPocket;
import com.zixiken.dimdoors.shared.world.pocketdimension.WorldProviderDungeonPocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;

public class DimDoorDimensions {

    public static DimensionType LIMBO = null;
    @Getter private static int minPocketDimID;
    @Getter private static int maxPocketDimID;
    private static final List<EnumPocketType> pocketTypes = new ArrayList<>();
    private static final Map<EnumPocketType, DimensionType> pocketDimensionTypes = new HashMap<>();
    private static final List<DimensionType> CUSTOM = new ArrayList<>();

    @Getter private static int limboDimID;
    @Getter private static int privateDimID;
    @Getter private static int publicDimID;
    @Getter private static int dungeonDimID;

    public static void init() {
        // Lowercase names because all minecraft dimensions are lowercase, _pockets suffix to make it clear what the world is
        minPocketDimID = DDConfig.getBaseDimID();
        int dimID = minPocketDimID;
        LIMBO = DimensionType.register("limbo", "_limbo", dimID, WorldProviderLimbo.class, false);
        limboDimID = dimID++;
        pocketTypes.add(EnumPocketType.PRIVATE);
        pocketDimensionTypes.put(EnumPocketType.PRIVATE, DimensionType.register("private_pockets", "_private", dimID, WorldProviderPersonalPocket.class, false));
        privateDimID = dimID++;
        pocketTypes.add(EnumPocketType.PUBLIC);
        pocketDimensionTypes.put(EnumPocketType.PUBLIC, DimensionType.register("public_pockets", "_public", dimID, WorldProviderPublicPocket.class, false));
        publicDimID = dimID++;
        maxPocketDimID = dimID;
        pocketTypes.add(EnumPocketType.DUNGEON);
        pocketDimensionTypes.put(EnumPocketType.DUNGEON, DimensionType.register("dungeon_pockets", "_dungeon", dimID, WorldProviderDungeonPocket.class, false));
        dungeonDimID = dimID;

        registerDimension(LIMBO);
        for (EnumPocketType pocketType : pocketDimensionTypes.keySet()) {
            registerDimension(pocketDimensionTypes.get(pocketType));
        }

        // TODO: For future use? Like, server owners can add their own set of DimDoors DimensionTypes via the configs? Or is this nonsense?
        // for (int i = 0; i < 0; i++) {
        //    dimID++;
        //    DimensionType tempType = DimensionType.register("Name", "_name", dimID, WorldProvider.class, false);
        //    CUSTOM.add(tempType);
        //    registerDimension(tempType);
        //}
    }

    public static void registerDimension(DimensionType dimension) {
        DimensionManager.registerDimension(dimension.getId(), dimension);
    }

    public static DimensionType getPocketDimensionType(EnumPocketType pocketType) {
        return pocketDimensionTypes.get(pocketType);
    }

    public static boolean isPocketDimension(int id) {
        return id >= minPocketDimID && id <= maxPocketDimID;
    }

    public static EnumPocketType getPocketType(int dimID) {
        int index = dimID - minPocketDimID;
        return pocketTypes.get(index);
    }
}
