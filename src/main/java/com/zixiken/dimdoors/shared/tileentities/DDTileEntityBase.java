package com.zixiken.dimdoors.shared.tileentities;

import com.zixiken.dimdoors.shared.util.Location;
import com.zixiken.dimdoors.shared.RiftRegistry;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class DDTileEntityBase extends TileEntity {

    private boolean isPaired = false;
    private int riftID = -1; //should not start at 0
    private int pairedRiftID = -1;
    private boolean isInPocket = false;
    private int pocketID = -1;
    private int depth = 0; //depth of the pocket it is in (not in a pocket -> 0)

    /**
     *
     * @return an array of floats representing RGBA color where 1.0 = 255.
     */
    public abstract float[] getRenderColor(Random rand);

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return oldState.getBlock() != newSate.getBlock();
    }

    public boolean pair(int otherRiftID) { //should only ever be called from the RiftRegistry.pair method
        if (isPaired) {
            if (otherRiftID == pairedRiftID) {
                return true;
            } else {
                RiftRegistry.Instance.unpair(pairedRiftID);
            }
        }
        pairedRiftID = otherRiftID;
        isPaired = true;
        RiftRegistry.Instance.pair(pairedRiftID, riftID);
        this.markDirty();
        return false;
    }

    public boolean unpair() { //should only ever be called from the RiftRegistry.unpair method
        if (!isPaired) {
            return true;
        } else {
            isPaired = false;
            RiftRegistry.Instance.unpair(pairedRiftID);
        }
        this.markDirty();
        return false;
    }

    public void register(int depth) {
        if (riftID == -1) {
            riftID = RiftRegistry.Instance.registerNewRift(this, depth);
            this.markDirty();
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        try {
            isPaired = nbt.getBoolean("isPaired");
            riftID = nbt.getInteger("riftID");
            pairedRiftID = nbt.getInteger("pairedRiftID");
            isInPocket = nbt.getBoolean("isInPocket");
            pocketID = nbt.getInteger("pocketID");
            depth = nbt.getInteger("depth");
        } catch (Exception e) {
            //reading these values should only fail on loading old saves, or loading old schematics, in which case the default values will do 
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setBoolean("isPaired", this.isPaired);
        nbt.setInteger("riftID", this.riftID);
        nbt.setInteger("pairedRiftID", this.pairedRiftID);
        nbt.setBoolean("isInPocket", this.isInPocket);
        nbt.setInteger("pocketID", this.pocketID);
        nbt.setInteger("depth", this.depth);
        return nbt;
    }

    public void loadDataFrom(DDTileEntityBase rift2) {
        if (rift2 != null && rift2.riftID != -1) {
            isPaired = rift2.isPaired;
            riftID = rift2.riftID; //should not start at 0
            pairedRiftID = rift2.pairedRiftID;
            this.markDirty();
        }
    }

    public int getRiftID() {
        return riftID;
    }

    public int getPairedRiftID() {
        return pairedRiftID;
    }

    public boolean isPaired() {
        return isPaired;
    }

    public int getDepth() {
        return depth;
    }

    public Location getTeleportTargetLocation() {
        return new Location(this.getWorld().provider.getDimension(), this.getPos());
    }

    public abstract boolean tryTeleport(Entity entity);

    public void setPocketID(int ID) {
        pocketID = ID;
    }

    public void setIsInPocket() {
        isInPocket = true;
    }
}
