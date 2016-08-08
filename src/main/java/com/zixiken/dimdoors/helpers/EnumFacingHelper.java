package com.zixiken.dimdoors.helpers;

import net.minecraft.block.BlockDoor;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;

public class EnumFacingHelper {
    public static EnumFacing getFacingFromBlockState(IBlockState state) {
        for (IProperty prop : (java.util.Set<IProperty>)state.getProperties().keySet()) {
            if (prop.getName().equals("facing") || prop.getName().equals("rotation")) {
                return state.getValue((PropertyDirection) prop);
            }
        }
        return EnumFacing.NORTH;
    }
}
