package com.zixiken.dimdoors.tileentities;

import com.zixiken.dimdoors.shared.RiftRegistry;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class DDTileEntityBase extends TileEntity {

    public boolean isPaired = false;
    public int riftID = -1; //should not start at 0
    public int pairedRiftID = -1;

    /**
     *
     * @return an array of floats representing RGBA color where 1.0 = 255.
     */
    public abstract float[] getRenderColor(Random rand);

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return oldState.getBlock() != newSate.getBlock();
    }

    public void pair(int otherRiftID) {
        if (isPaired) {
            if (otherRiftID == pairedRiftID) {
                return;
            } else {
                RiftRegistry.Instance.unpair(pairedRiftID);
            }
        }
        pairedRiftID = otherRiftID;
        RiftRegistry.Instance.pair(pairedRiftID, riftID);
        isPaired = true;
        this.markDirty();
    }

    public void unpair() {
        if (!isPaired) {
            return;
        } else {
            isPaired = false;
            RiftRegistry.Instance.unpair(pairedRiftID);
        }
        this.markDirty();
    }

    public void register() {
        if (riftID == -1) {
            riftID = RiftRegistry.Instance.registerNewRift(this);
            this.markDirty();
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        try {
            this.isPaired = nbt.getBoolean("isPaired");
            this.riftID = nbt.getInteger("riftID");
            this.pairedRiftID = nbt.getInteger("pairedRiftID");
        } catch (Exception e) {
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setBoolean("isPaired", this.isPaired);
        nbt.setInteger("riftID", this.riftID);
        nbt.setInteger("pairedRiftID", this.pairedRiftID);
        return nbt;
    }

    public void loadDataFrom(DDTileEntityBase rift2) {
        isPaired = rift2.isPaired;
        riftID = rift2.riftID; //should not start at 0
        pairedRiftID = rift2.pairedRiftID;
        this.markDirty();
    }
}
