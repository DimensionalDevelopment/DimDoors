package com.zixiken.dimdoors.shared.tileentities;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.blocks.ModBlocks;
import com.zixiken.dimdoors.shared.RiftRegistry;
import com.zixiken.dimdoors.shared.TeleporterDimDoors;
import com.zixiken.dimdoors.shared.util.Location;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class TileEntityRift extends DDTileEntityBase implements ITickable {

    private static final int ENDERMAN_SPAWNING_CHANCE = 1;
    private static final int MAX_ENDERMAN_SPAWNING_CHANCE = 32;
    private static final int HOSTILE_ENDERMAN_CHANCE = 1;
    private static final int MAX_HOSTILE_ENDERMAN_CHANCE = 3;
    private static final int UPDATE_PERIOD = 200; //10 seconds

    public boolean placingDoorOnRift = false; //to track whether a Rift is getting broken because it is replaced by a door (do not unregister in this case) or because it is being broken another way (do unregister in this case)

    private static final Random random = new Random();

    //Need to be saved:
    private int updateTimer;
    public BlockPos offset = BlockPos.ORIGIN;
    public boolean shouldClose = false;
    public int spawnedEndermenID = 0;
    public int riftRotation = random.nextInt(360);
    public float growth = 0;

    public TileEntityRift() {
        loadDataFrom(RiftRegistry.INSTANCE.getLastChangedRift()); //@todo this should absolutely not be done in this constructor...

        // Vary the update times of rifts to prevent all the rifts in a cluster
        // from updating at the same time.
        updateTimer = random.nextInt(UPDATE_PERIOD);
    }

    @Override
    public void update() {
        if (world.getBlockState(pos).getBlock() != ModBlocks.RIFT) {
            invalidate();
            return;
        }

        // Check if this rift should render white closing particles and
        // spread the closing effect to other rifts nearby.
        if (shouldClose) {
            closeRift();
            return;
        }

        if (updateTimer >= UPDATE_PERIOD) {
            spawnEndermen();
            updateTimer = 0;
        } else if (updateTimer == UPDATE_PERIOD / 2) {
            updateNearestRift();
        }
        growth += 1F / (growth + 1);
        updateTimer++;
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
                List<EntityEnderman> list = world.getEntitiesWithinAABB(EntityEnderman.class,
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

    private void closeRift() {
        world.setBlockToAir(pos);
        growth--; //@todo?
    }

    public boolean updateNearestRift() {
        return false;
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return pass == 1;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        updateTimer = nbt.getInteger("updateTimer");
        offset = new BlockPos(nbt.getInteger("xOffset"), nbt.getInteger("yOffset"), nbt.getInteger("zOffset"));
        shouldClose = nbt.getBoolean("shouldClose");
        spawnedEndermenID = nbt.getInteger("spawnedEndermenID");
        riftRotation = nbt.getInteger("riftRotation");
        growth = nbt.getFloat("growth");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("updateTimer", updateTimer);
        nbt.setInteger("xOffset", offset.getX());
        nbt.setInteger("yOffset", offset.getY());
        nbt.setInteger("zOffset", offset.getZ());
        nbt.setBoolean("shouldClose", shouldClose);
        nbt.setInteger("spawnedEndermenID", spawnedEndermenID);
        nbt.setInteger("riftRotation", riftRotation);
        nbt.setFloat("growth", growth);

        return nbt;
    }

    @Override
    public float[] getRenderColor(Random rand) {
        return null;
    }

    @Override
    public boolean tryTeleport(Entity entity) {
        int otherRiftID;
        if (!isPaired()) {
            DimDoors.warn(getClass(), "Rift " + getRiftID() + " was not paired and thus, should not exist as a Rift, unless it was unpaired after the door was destroyed, in which case you should contact the developers and tell them to fix stuff.");
            return false;
        } else {
            otherRiftID = getPairedRiftID();
        }
        Location tpLocation = RiftRegistry.INSTANCE.getTeleportLocation(otherRiftID);
        RiftRegistry.INSTANCE.validatePlayerPocketEntry(entity, otherRiftID);
        return TeleporterDimDoors.instance().teleport(entity, tpLocation); //@todo this seems to return false?
    }
}
