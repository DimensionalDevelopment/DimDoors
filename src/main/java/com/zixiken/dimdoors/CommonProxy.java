package com.zixiken.dimdoors;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.zixiken.dimdoors.blocks.BaseDimDoor;
import com.zixiken.dimdoors.tileentities.TileEntityDimDoor;

import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CommonProxy {

    public void registerRenderers() {}

    public void writeNBTToFile(World world) {
        try {
            String dirFolder = world.getSaveHandler().getMapFileFromName("idcounts")
                    .getCanonicalPath().replace("idcounts.dat", "");

            File file = new File(dirFolder, "GGMData.dat");

            if (!file.exists()) file.createNewFile();

            FileOutputStream fileoutputstream = new FileOutputStream(file);
            NBTTagCompound nbttagcompound = new NBTTagCompound();

            CompressedStreamTools.writeCompressed(nbttagcompound, fileoutputstream);
            fileoutputstream.close();
        } catch (IOException e) {
            System.err.println("Could not write NBT data to file:\n" + e);
        }
    }

    public NBTTagCompound readNBTFromFile(World world) {
        try {
            String dirFolder = world.getSaveHandler().getMapFileFromName("idcounts")
                    .getCanonicalPath().replace("idcounts.dat", "");

            File file = new File(dirFolder, "GGMData.dat");

            if (!file.exists()) {
                file.createNewFile();
                FileOutputStream fileoutputstream = new FileOutputStream(file);
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                
                CompressedStreamTools.writeCompressed(nbttagcompound, fileoutputstream);
                fileoutputstream.close();
            }

            FileInputStream fileinputstream = new FileInputStream(file);
            NBTTagCompound nbttagcompound = CompressedStreamTools.readCompressed(fileinputstream);
            fileinputstream.close();
            return nbttagcompound;
        } catch (IOException e) {
            System.err.println("Could not read NBT data from file:\n" + e);
            return null;
        }
    }

	public void updateDoorTE(BaseDimDoor door, World world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileEntityDimDoor) {
            IBlockState state = world.getBlockState(pos);
			TileEntityDimDoor dimTile = (TileEntityDimDoor) tile;
			dimTile.openOrClosed = door.isDoorOnRift(world, pos) && door.isUpperDoorBlock(state);
			dimTile.orientation = state.getValue(BlockDoor.FACING).rotateY().getHorizontalIndex();
			dimTile.lockStatus = door.getLockStatus(world, pos);
		}
	}
    
    public void registerSidedHooks() {
        new ServerPacketHandler();
    }

    public EntityPlayer getMessagePlayer(MessageContext ctx) {
        return ctx.getServerHandler().playerEntity;
    }
}