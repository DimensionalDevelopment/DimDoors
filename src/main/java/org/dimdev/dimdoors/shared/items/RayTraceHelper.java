package org.dimdev.dimdoors.shared.items;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.dimdev.dimdoors.shared.blocks.IRiftProvider;
import org.dimdev.dimdoors.shared.tileentities.TileEntityFloatingRift;
import org.dimdev.dimdoors.shared.tileentities.TileEntityRift;

public final class RayTraceHelper {

    public static boolean isFloatingRift(RayTraceResult hit, World world) {
        return hit != null && hit.typeOfHit == RayTraceResult.Type.BLOCK && world.getTileEntity(hit.getBlockPos()) instanceof TileEntityFloatingRift;
    }

    public static boolean isRift(RayTraceResult hit, World world) {
        return hit != null && hit.typeOfHit == RayTraceResult.Type.BLOCK && world.getTileEntity(hit.getBlockPos()) instanceof TileEntityRift;
    }

    public static boolean isLivingEntity(RayTraceResult hit) {
        return hit != null && hit.typeOfHit == RayTraceResult.Type.ENTITY && hit.entityHit instanceof EntityLivingBase;
    }

    //copied from MC source code, because Vanilla MC assumes that everything without a hitbox either can never be hit OR is a liquid
    //TODO: clean this up?
    public static RayTraceResult rayTraceForRiftTools(World worldIn, EntityPlayer playerIn) {
        float f = playerIn.rotationPitch;
        float f1 = playerIn.rotationYaw;
        double d0 = playerIn.posX;
        double d1 = playerIn.posY + (double) playerIn.getEyeHeight();
        double d2 = playerIn.posZ;
        Vec3d playerVector = new Vec3d(d0, d1, d2);
        float f2 = MathHelper.cos(-f1 * 0.017453292F - (float) Math.PI);
        float f3 = MathHelper.sin(-f1 * 0.017453292F - (float) Math.PI);
        float f4 = -MathHelper.cos(-f * 0.017453292F);
        float f5 = MathHelper.sin(-f * 0.017453292F);
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        double d3 = playerIn.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();
        Vec3d cumulativeLookVector = playerVector.addVector((double) f6 * d3, (double) f5 * d3, (double) f7 * d3);

        if (!Double.isNaN(playerVector.x) && !Double.isNaN(playerVector.y) && !Double.isNaN(playerVector.z)) {
            if (!Double.isNaN(cumulativeLookVector.x) && !Double.isNaN(cumulativeLookVector.y) && !Double.isNaN(cumulativeLookVector.z)) {
                int i = MathHelper.floor(cumulativeLookVector.x);
                int j = MathHelper.floor(cumulativeLookVector.y);
                int k = MathHelper.floor(cumulativeLookVector.z);
                int l = MathHelper.floor(playerVector.x);
                int i1 = MathHelper.floor(playerVector.y);
                int j1 = MathHelper.floor(playerVector.z);
                BlockPos blockpos = new BlockPos(l, i1, j1);
                IBlockState iblockstate = worldIn.getBlockState(blockpos);
                Block block = iblockstate.getBlock();

                if (block instanceof IRiftProvider) {
                    RayTraceResult raytraceresult = iblockstate.collisionRayTrace(worldIn, blockpos, playerVector, cumulativeLookVector);

                    if (raytraceresult != null) {
                        return raytraceresult;
                    }
                }

                RayTraceResult raytraceresult2 = null;
                int k1 = 200;

                while (k1-- >= 0) {
                    if (Double.isNaN(playerVector.x) || Double.isNaN(playerVector.y) || Double.isNaN(playerVector.z) || (l == i && i1 == j && j1 == k)) {
                        return null;
                    }

                    boolean flag0 = true;
                    boolean flag1 = true;
                    boolean flag2 = true;
                    double d4 = 999.0D;
                    double d5 = 999.0D;
                    double d6 = 999.0D;

                    if (i > l) {
                        d4 = (double) l + 1.0D;
                    } else if (i < l) {
                        d4 = (double) l + 0.0D;
                    } else {
                        flag0 = false;
                    }

                    if (j > i1) {
                        d5 = (double) i1 + 1.0D;
                    } else if (j < i1) {
                        d5 = (double) i1 + 0.0D;
                    } else {
                        flag1 = false;
                    }

                    if (k > j1) {
                        d6 = (double) j1 + 1.0D;
                    } else if (k < j1) {
                        d6 = (double) j1 + 0.0D;
                    } else {
                        flag2 = false;
                    }

                    double d7 = 999.0D;
                    double d8 = 999.0D;
                    double d9 = 999.0D;
                    double d10 = cumulativeLookVector.x - playerVector.x;
                    double d11 = cumulativeLookVector.y - playerVector.y;
                    double d12 = cumulativeLookVector.z - playerVector.z;

                    if (flag0) {
                        d7 = (d4 - playerVector.x) / d10;
                    }

                    if (flag1) {
                        d8 = (d5 - playerVector.y) / d11;
                    }

                    if (flag2) {
                        d9 = (d6 - playerVector.z) / d12;
                    }

                    if (d7 == -0.0D) {
                        d7 = -1.0E-4D;
                    }

                    if (d8 == -0.0D) {
                        d8 = -1.0E-4D;
                    }

                    if (d9 == -0.0D) {
                        d9 = -1.0E-4D;
                    }

                    EnumFacing enumfacing;

                    if (d7 < d8 && d7 < d9) {
                        enumfacing = i > l ? EnumFacing.WEST : EnumFacing.EAST;
                        playerVector = new Vec3d(d4, playerVector.y + d11 * d7, playerVector.z + d12 * d7);
                    } else if (d8 < d9) {
                        enumfacing = j > i1 ? EnumFacing.DOWN : EnumFacing.UP;
                        playerVector = new Vec3d(playerVector.x + d10 * d8, d5, playerVector.z + d12 * d8);
                    } else {
                        enumfacing = k > j1 ? EnumFacing.NORTH : EnumFacing.SOUTH;
                        playerVector = new Vec3d(playerVector.x + d10 * d9, playerVector.y + d11 * d9, d6);
                    }

                    l = MathHelper.floor(playerVector.x) - (enumfacing == EnumFacing.EAST ? 1 : 0);
                    i1 = MathHelper.floor(playerVector.y) - (enumfacing == EnumFacing.UP ? 1 : 0);
                    j1 = MathHelper.floor(playerVector.z) - (enumfacing == EnumFacing.SOUTH ? 1 : 0);
                    blockpos = new BlockPos(l, i1, j1);
                    IBlockState iblockstate1 = worldIn.getBlockState(blockpos);
                    Block block1 = iblockstate1.getBlock();

                    if (block1.canCollideCheck(iblockstate1, false) || block1 instanceof IRiftProvider) {
                        RayTraceResult raytraceresult1 = iblockstate1.collisionRayTrace(worldIn, blockpos, playerVector, cumulativeLookVector);

                        if (raytraceresult1 != null) {
                            return raytraceresult1;
                        }
                    } else {
                        raytraceresult2 = new RayTraceResult(RayTraceResult.Type.MISS, playerVector, enumfacing, blockpos);
                    }
                }
            }
        }
        return null;
    }
}
