package com.zixiken.dimdoors.shared;

import com.zixiken.dimdoors.shared.tileentities.DDTileEntityBase;
import com.zixiken.dimdoors.shared.tileentities.TileEntityRift;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class RayTraceHelper {
    public static boolean isRift(RayTraceResult hit, World world) {
        return isNotNull(hit) && hit.typeOfHit == RayTraceResult.Type.BLOCK && world.getTileEntity(hit.getBlockPos()) instanceof TileEntityRift;
    }
    
    public static boolean isAbstractRift(RayTraceResult hit, World world) {
        return isNotNull(hit) && hit.typeOfHit == RayTraceResult.Type.BLOCK && world.getTileEntity(hit.getBlockPos()) instanceof DDTileEntityBase;
    }

    public static boolean isLivingEntity(RayTraceResult hit) {
        return isNotNull(hit) && hit.typeOfHit == RayTraceResult.Type.ENTITY && hit.entityHit instanceof EntityLivingBase;
    }

    private static boolean isNotNull(RayTraceResult hit) {
        return hit != null;
    }
}
