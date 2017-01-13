package com.zixiken.dimdoors.tileentities;

import com.zixiken.dimdoors.shared.RiftRegistry;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
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
            if (isPaired) {
                if (otherRiftID == pairedRiftID) {
                    return;
                }
                else {
                    RiftRegistry.Instance.unpair(pairedRiftID);
                }
            }
            pairedRiftID = otherRiftID;
            RiftRegistry.Instance.pair(pairedRiftID, riftID);                    
            isPaired = true;
        }
        
        public void unpair() {
            if (!isPaired) {
                return;
            }
            else {
                isPaired = false;
                RiftRegistry.Instance.unpair(pairedRiftID);
            }
        }
        
        private void register() {
            riftID = RiftRegistry.Instance.registerNewRift(this);
        }
}
