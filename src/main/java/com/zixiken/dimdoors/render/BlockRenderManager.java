package com.zixiken.dimdoors.render;

import com.zixiken.dimdoors.DimDoors;
import net.minecraft.block.BlockDoor;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraftforge.client.model.ModelLoader;

public class BlockRenderManager {
    public static void addCustomStateMappers() {
        StateMap map = new StateMap.Builder().ignore(BlockDoor.POWERED).build();

        ModelLoader.setCustomStateMapper(DimDoors.goldenDoor, map);
        ModelLoader.setCustomStateMapper(DimDoors.quartzDoor, map);
        ModelLoader.setCustomStateMapper(DimDoors.goldenDimensionalDoor, map);
        ModelLoader.setCustomStateMapper(DimDoors.dimensionalDoor, map);
        ModelLoader.setCustomStateMapper(DimDoors.personalDimDoor, map);
        ModelLoader.setCustomStateMapper(DimDoors.unstableDoor, map);
        ModelLoader.setCustomStateMapper(DimDoors.warpDoor, map);

        ModelLoader.setCustomStateMapper(DimDoors.transientDoor, new StateMap.Builder().ignore(
                BlockDoor.FACING, BlockDoor.HALF, BlockDoor.HINGE, BlockDoor.OPEN, BlockDoor.POWERED).build());
    }
}
