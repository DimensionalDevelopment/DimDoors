package com.zixiken.dimdoors.shared.tileentities;

import com.zixiken.dimdoors.shared.blocks.ModBlocks;
import com.zixiken.dimdoors.shared.RiftRegistry;
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

    private static Random random = new Random();

    private int updateTimer;
    public BlockPos offset = BlockPos.ORIGIN;
    public boolean shouldClose = false;
    public int spawnedEndermenID = 0;

    public int riftRotation = random.nextInt(360);
    public float growth = 0;

    private static int temp = 0;

    public TileEntityRift() {
        super();
        this.loadDataFrom(RiftRegistry.Instance.getLastChangedRift());

        // Vary the update times of rifts to prevent all the rifts in a cluster
        // from updating at the same time.
        updateTimer = random.nextInt(UPDATE_PERIOD);
    }

    @Override
    public void update() {
        if (world.getBlockState(pos).getBlock() != ModBlocks.blockRift) {
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
        Entity entity = world.getEntityByID(this.spawnedEndermenID);
        if (entity != null && entity instanceof EntityEnderman) {
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
                        EntityPlayer player = this.world.getClosestPlayerToEntity(enderman, 50);
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
        this.updateTimer = nbt.getInteger("updateTimer");
        this.offset = new BlockPos(nbt.getInteger("xOffset"), nbt.getInteger("yOffset"), nbt.getInteger("zOffset"));
        this.shouldClose = nbt.getBoolean("shouldClose");
        this.spawnedEndermenID = nbt.getInteger("spawnedEndermenID");
        this.riftRotation = nbt.getInteger("riftRotation");
        this.growth = nbt.getFloat("growth");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("updateTimer", this.updateTimer);
        nbt.setInteger("xOffset", this.offset.getX());
        nbt.setInteger("yOffset", this.offset.getY());
        nbt.setInteger("zOffset", this.offset.getZ());
        nbt.setBoolean("shouldClose", this.shouldClose);
        nbt.setInteger("spawnedEndermenID", this.spawnedEndermenID);
        nbt.setInteger("riftRotation", this.riftRotation);
        nbt.setFloat("growth", this.growth);

        return nbt;
    }

    @Override
    public float[] getRenderColor(Random rand) {
        return null;
    }

    @Override
    public boolean tryTeleport(Entity entity) {
        return false; //@todo, rift blade functionality?
    }
}
