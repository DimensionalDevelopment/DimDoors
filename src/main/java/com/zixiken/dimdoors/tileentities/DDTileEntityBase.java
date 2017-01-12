package com.zixiken.dimdoors.tileentities;

import com.zixiken.dimdoors.world.RiftHandler;
import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class DDTileEntityBase extends TileEntity
{
    public boolean isPaired = false;
    public int riftID;
    public int pairedRiftID;
    
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
            RiftHandler rifthandler;
            if (isPaired) {
                if (otherRiftID == pairedRiftID) {
                    return;
                }
                else {
                    rifthandler.unpair(pairedRiftID);
                }
            }
            pairedRiftID = otherRiftID;
            rifthandler.pair(pairedRiftID, riftID);                    
            isPaired = true;
        }
        
        public void unpair() {
            RiftHandler rifthandler;
            if (!isPaired) {
                return;
            }
            else {
                isPaired = false;
                rifthandler.unpair(pairedRiftID);
            }
        }
        
        public static NBTTagCompound writeToNBT(DDTileEntityBase rift) {
            
        }
        
        public static DDTileEntityBase readFromNBT(int dim, NBTTagCompound riftNBT) {
            
        }
}
