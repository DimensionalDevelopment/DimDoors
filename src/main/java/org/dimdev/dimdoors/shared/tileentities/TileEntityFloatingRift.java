package org.dimdev.dimdoors.shared.tileentities;

import lombok.Setter;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.dimdev.annotatednbt.NBTSerializable;
import org.dimdev.annotatednbt.Saved;
import org.dimdev.ddutils.Location;
import org.dimdev.ddutils.TeleportUtils;
import org.dimdev.ddutils.lsystem.LSystem;
import org.dimdev.ddutils.nbt.NBTUtils;
import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.ModConfig;
import org.dimdev.dimdoors.shared.blocks.BlockFloatingRift;
import org.dimdev.dimdoors.shared.blocks.ModBlocks;
import org.dimdev.dimdoors.shared.world.RiftDecay;

import java.util.List;
import java.util.Random;

@NBTSerializable public class TileEntityFloatingRift extends TileEntityRift implements ITickable {

    private static final int ENDERMAN_SPAWNING_CHANCE = 1;
    private static final int MAX_ENDERMAN_SPAWNING_CHANCE = 32;
    private static final int HOSTILE_ENDERMAN_CHANCE = 1;
    private static final int MAX_HOSTILE_ENDERMAN_CHANCE = 3;
    private static final int UPDATE_PERIOD = 200; //10 seconds

    private static final Random random = new Random();

    // Need to be saved:
    @Saved public boolean closing = false; // TODO: maybe we could have a closingSpeed instead?
    @Saved public boolean stabilized = false;
    @Saved public int spawnedEndermenID = 0;
    @Saved public float size = 0; // TODO: store size in blocks
    @Saved public float riftYaw = random.nextInt(360);
    @Saved protected float teleportTargetPitch; // TODO: also render rift rotated on pitch axis?
    @Saved public int curveId = random.nextInt(LSystem.curves.size());

    // Don't need to be saved
    @Setter private boolean unregisterDisabled = false;
    int updateTimer;

    @SideOnly(Side.CLIENT) public double renderAngle; // This is @SideOnly(Side.CLIENT), don't initialize the field ( = 0), or class initialization won't work on the server!
    @SideOnly(Side.CLIENT) private int cachedCurveId;
    @SideOnly(Side.CLIENT) private LSystem.PolygonInfo curve; // Cache the curve for efficiency

    public TileEntityFloatingRift() {
        updateTimer = random.nextInt(UPDATE_PERIOD);
        if (DimDoors.proxy.isClient()) cachedCurveId = -1;
    }

    @Override
    public void update() {
        if (world.getBlockState(pos).getBlock() != ModBlocks.RIFT) {
            invalidate();
            return;
        }

        if (updateTimer >= UPDATE_PERIOD) {
            spawnEndermen();
            updateTimer = 0;
        } else if (updateTimer == UPDATE_PERIOD / 2) {
            updateNearestRift();
        }
        updateTimer++;

        // Check if this rift should render white closing particles and
        // spread the closing effect to other rifts nearby.
        if (closing) {
            if (size > 0) {
                size -= ModConfig.general.riftCloseSpeed;
            } else {
                world.setBlockToAir(pos);
            }
        } else if (!stabilized) {
            // Logarithmic growth
            for (int n = 0; n < 10; n++) {
                // TODO: growthSpeed and growthSize config options
                size += 1F / (size + 1);
            }
            //Make blocks around the rift have a random chance of being "unwoven" into world thread
            if(!world.isRemote && ModConfig.general.enableRiftDecay) RiftDecay.applySpreadDecay(this.world,this.pos,this);
        }
    }

    private void spawnEndermen() {
        if (world.isRemote) {
            return;
        }

        // Ensure that this rift is only spawning one Enderman at a time, to prevent hordes of Endermen
        Entity entity = world.getEntityByID(spawnedEndermenID);
        if (entity instanceof EntityEnderman) {
            return;
        }

        if (random.nextInt(MAX_ENDERMAN_SPAWNING_CHANCE) < ENDERMAN_SPAWNING_CHANCE) {
            // Endermen will only spawn from groups of rifts
            if (updateNearestRift()) {
                List<EntityEnderman> list = world.getEntitiesWithinAABB(
                        EntityEnderman.class,
                        new AxisAlignedBB(pos.getX() - 9, pos.getY() - 3, pos.getZ() - 9, pos.getX() + 9, pos.getY() + 3, pos.getZ() + 9));

                if (list.isEmpty()) {
                    EntityEnderman enderman = new EntityEnderman(world);
                    enderman.setLocationAndAngles(pos.getX() + 0.5, pos.getY() - 1, pos.getZ() + 0.5, 5, 6);
                    world.spawnEntity(enderman);

                    if (random.nextInt(MAX_HOSTILE_ENDERMAN_CHANCE) < HOSTILE_ENDERMAN_CHANCE) {
                        EntityPlayer player = world.getClosestPlayerToEntity(enderman, 50);
                        if (player != null) {
                            enderman.setAttackTarget(player);
                        }
                    }
                }
            }
        }
    }

    public boolean updateNearestRift() {
        return false;
    }

    public void setClosing(boolean closing) {
        this.closing = closing;
        IBlockState state = world.getBlockState(pos);
        world.notifyBlockUpdate(pos, state, state, 0);
        markDirty();
    }

    public void setStabilized(boolean stabilized) {
        this.stabilized = stabilized;
        IBlockState state = world.getBlockState(pos);
        world.notifyBlockUpdate(pos, state, state, 0);
        markDirty();
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        // newState is not accurate if we change the state during onBlockBreak
        newSate = world.getBlockState(pos);
        return oldState.getBlock() != newSate.getBlock() &&
               !(world.getTileEntity(pos) instanceof TileEntityRift && // This is to prevent setBlockState clearing the tile entity right after we set it in breakBlock
                 newSate.getBlock() instanceof BlockFloatingRift);
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return pass == 1;
    }

    @Override public void readFromNBT(NBTTagCompound nbt) { super.readFromNBT(nbt); NBTUtils.readFromNBT(this, nbt); }

    @Override public NBTTagCompound writeToNBT(NBTTagCompound nbt) { nbt = super.writeToNBT(nbt); return NBTUtils.writeToNBT(this, nbt); }

    @Override
    public boolean isFloating() {
        return true;
    }

    @Override
    public void unregister() {
        if (!unregisterDisabled) super.unregister();
    }

    public void setTeleportTargetRotation(float yaw, float pitch) {
        riftYaw = yaw;
        teleportTargetPitch = pitch;
        markDirty();
    }

    @Override
    public boolean receiveEntity(Entity entity, float relativeYaw, float relativePitch) { // TODO
        if (relativeRotation) {
            float yaw = getDestinationYaw(entity.rotationYaw) + entity.rotationYaw - relativeYaw;
            float pitch = entity instanceof EntityLiving ? entity.rotationPitch : getDestinationPitch(entity.rotationPitch) + entity.rotationPitch - relativePitch;
            TeleportUtils.teleport(entity, new Location(world, pos), yaw, pitch);
            // TODO: velocity
        } else {
            TeleportUtils.teleport(entity, new Location(world, pos), getDestinationYaw(entity.rotationYaw), getDestinationPitch(entity.rotationPitch));
        }
        return true;
    }

    @Override
    public float getSourceYaw(float entityYaw) {
        return (int) (entityYaw / 90) * 90;
    }

    @Override
    public float getSourcePitch(float entityPitch) {
        return 0;
    }

    @Override public float getDestinationYaw(float entityYaw) {
        return riftYaw;
    }

    @Override public float getDestinationPitch(float entityPitch) {
        return teleportTargetPitch;
    }

    @SideOnly(Side.CLIENT)
    public LSystem.PolygonInfo getCurve() {
        if (curve != null && curveId == cachedCurveId) {
            return curve;
        }

        int numCurves = LSystem.curves.size();
        cachedCurveId = curveId;
        if (curveId < numCurves && curveId >= 0) {
            curve = LSystem.curves.get(curveId);
        } else {
            // Don't crash if the server sends an invaild curve ID
            DimDoors.log.error("Curve ID out of bounds (ID: " + curveId + ", number of curves: " + numCurves + ")");
            curve = LSystem.curves.get(0);
        }
        return curve;
    }
    @SideOnly(Side.CLIENT)
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(pos.add(-50, -50, -50), pos.add(50, 50, 50));
    }
}
