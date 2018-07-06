package org.dimdev.dimdoors.shared.items;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.blocks.IRiftProvider;
import org.dimdev.dimdoors.shared.tileentities.TileEntityFloatingRift;
import org.dimdev.dimdoors.shared.tileentities.TileEntityRift;

import java.util.List;

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

    /**
     * returns a {@code RayTraceResult} for the first {@code EntityLiving} the look vector of
     * {@code player} hits within {@code range} if it can find any. If the look
     * vector is blocked by a solid block, before it collides with an {@code EntityLiving}, a
     * {@code RayTraceResult} for this solid block is returned instead. If it fails to find
     * either, this method returns null.
     *
     * @param world
     * @param player
     * @param range
     * @param partialTicks
     * @return
     */
    public static RayTraceResult rayTraceEntity(World world, EntityPlayer player, double range, float partialTicks) {
        //TODO: make this work on a server
        Vec3d playerEyesVector = player.getPositionEyes(partialTicks);
        Vec3d playerLookVector = player.getLookVec();

        //Make sure that the raytrace stops if there's a block inbetween
        RayTraceResult firstHitBlock = RayTraceHelper.rayTraceForRiftTools(world, player, range);
        if (firstHitBlock != null) {
            Vec3d hitVector = firstHitBlock.hitVec;
            range = hitVector.distanceTo(playerEyesVector);
        }

        Vec3d tempVec = playerEyesVector;
        Vec3d tempVec2;
        for (double d = 0.2; d < range; d += 0.2) {
            tempVec2 = playerEyesVector.add(playerLookVector.scale(d));
            List<EntityLiving> entities = world.getEntitiesWithinAABB(EntityLiving.class, new AxisAlignedBB(tempVec.x, tempVec.y, tempVec.z, tempVec2.x, tempVec2.y, tempVec2.z));
            for (EntityLiving entity : entities) {
                if (entity == null) { //should never happen, but just in case
                    continue;
                }
                AxisAlignedBB entityHitbox = entity.getEntityBoundingBox();
                if (entityHitbox == null) {
                    DimDoors.log.warn("The hitbox of Entity: " + entity + " was null, somehow");
                    continue;
                }
                if (entityHitbox.contains(tempVec2)) {
                    return new RayTraceResult(entity);
                }
            }
            tempVec = tempVec2;
        }
        return firstHitBlock;
    }

    public static RayTraceResult rayTraceForRiftTools(World worldIn, EntityPlayer playerIn) {
        return rayTraceForRiftTools(worldIn, playerIn, playerIn.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue());
    }

    //partially copied from MC source code, because Vanilla MC assumes that everything without a hitbox either can never be hit OR is a liquid
    //TODO: clean this up?
    public static RayTraceResult rayTraceForRiftTools(World worldIn, EntityPlayer player, double range) {

        Vec3d playerLookVector = player.getLookVec();
        Vec3d tempVector = player.getPositionEyes(1.0F); //this is what constantly gets shifted in the while-loop later and it starts at the eyes
        Vec3d endVector = tempVector.add(playerLookVector.scale(range));

        if (!Double.isNaN(tempVector.x) && !Double.isNaN(tempVector.y) && !Double.isNaN(tempVector.z)) {
            if (!Double.isNaN(endVector.x) && !Double.isNaN(endVector.y) && !Double.isNaN(endVector.z)) {
                int endX = MathHelper.floor(endVector.x);
                int endY = MathHelper.floor(endVector.y);
                int endZ = MathHelper.floor(endVector.z);
                int playerX = MathHelper.floor(tempVector.x);
                int playerY = MathHelper.floor(tempVector.y);
                int playerZ = MathHelper.floor(tempVector.z);
                BlockPos blockpos = new BlockPos(playerX, playerY, playerZ);
                IBlockState iblockstate = worldIn.getBlockState(blockpos);
                Block block = iblockstate.getBlock();

                if (block.canCollideCheck(iblockstate, false) || block instanceof IRiftProvider) {
                    RayTraceResult raytraceresult = iblockstate.collisionRayTrace(worldIn, blockpos, tempVector, endVector);
                    if (raytraceresult != null) {
                        return raytraceresult;
                    }
                }
                
                int counter = 200; //TODO: should this really be a hardcoded value?
                while (counter-- >= 0) {
                    if (Double.isNaN(tempVector.x) || Double.isNaN(tempVector.y) || Double.isNaN(tempVector.z) || (playerX == endX && playerY == endY && playerZ == endZ)) {
                        return null;
                    }

                    boolean xFlag = true;
                    boolean yFlag = true;
                    boolean zFlag = true;
                    double x1 = 999.0D;
                    double y1 = 999.0D;
                    double z1 = 999.0D;

                    if (endX > playerX) {
                        x1 = (double) playerX + 1.0D;
                    } else if (endX < playerX) {
                        x1 = (double) playerX;
                    } else {
                        xFlag = false;
                    }

                    if (endY > playerY) {
                        y1 = (double) playerY + 1.0D;
                    } else if (endY < playerY) {
                        y1 = (double) playerY;
                    } else {
                        yFlag = false;
                    }

                    if (endZ > playerZ) {
                        z1 = (double) playerZ + 1.0D;
                    } else if (endZ < playerZ) {
                        z1 = (double) playerZ;
                    } else {
                        zFlag = false;
                    }

                    double x2 = endVector.x - tempVector.x;
                    double y2 = endVector.y - tempVector.y;
                    double z2 = endVector.z - tempVector.z;
                    double x3 = 999.0D;
                    double y3 = 999.0D;
                    double z3 = 999.0D;

                    if (xFlag) {
                        x3 = (x1 - tempVector.x) / x2;
                    }

                    if (yFlag) {
                        y3 = (y1 - tempVector.y) / y2;
                    }

                    if (zFlag) {
                        z3 = (z1 - tempVector.z) / z2;
                    }

                    if (x3 == -0.0D) {
                        x3 = -1.0E-4D;
                    }

                    if (y3 == -0.0D) {
                        y3 = -1.0E-4D;
                    }

                    if (z3 == -0.0D) {
                        z3 = -1.0E-4D;
                    }

                    EnumFacing enumfacing;

                    if (x3 < y3 && x3 < z3) {
                        enumfacing = endX > playerX ? EnumFacing.WEST : EnumFacing.EAST;
                        tempVector = new Vec3d(x1, tempVector.y + y2 * x3, tempVector.z + z2 * x3);
                    } else if (y3 < z3) {
                        enumfacing = endY > playerY ? EnumFacing.DOWN : EnumFacing.UP;
                        tempVector = new Vec3d(tempVector.x + x2 * y3, y1, tempVector.z + z2 * y3);
                    } else {
                        enumfacing = endZ > playerZ ? EnumFacing.NORTH : EnumFacing.SOUTH;
                        tempVector = new Vec3d(tempVector.x + x2 * z3, tempVector.y + y2 * z3, z1);
                    }

                    playerX = MathHelper.floor(tempVector.x) - (enumfacing == EnumFacing.EAST ? 1 : 0);
                    playerY = MathHelper.floor(tempVector.y) - (enumfacing == EnumFacing.UP ? 1 : 0);
                    playerZ = MathHelper.floor(tempVector.z) - (enumfacing == EnumFacing.SOUTH ? 1 : 0);
                    blockpos = new BlockPos(playerX, playerY, playerZ);
                    IBlockState iblockstate1 = worldIn.getBlockState(blockpos);
                    Block block1 = iblockstate1.getBlock();

                    if (block1.canCollideCheck(iblockstate1, false) || block1 instanceof IRiftProvider) {
                        RayTraceResult raytraceresult1 = iblockstate1.collisionRayTrace(worldIn, blockpos, tempVector, endVector);

                        if (raytraceresult1 != null) {
                            return raytraceresult1;
                        }
                    }
                }
            }
        }
        return null;
    }
}
