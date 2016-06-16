package com.zixiken.dimdoors;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.zixiken.dimdoors.blocks.BaseDimDoor;
import com.zixiken.dimdoors.config.DDProperties;
import com.zixiken.dimdoors.tileentities.TileEntityDimDoor;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class CommonProxy {
    public static String BLOCK_PNG = "/PocketBlockTextures.png";
    public static String ITEM_PNG = "/PocketItemTextures.png";
    public static String RIFT_PNG = "/RIFT.png";
    public static String RIFT2_PNG = "/RIFT2.png";
    public static String WARP_PNG = "/WARP.png";

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

    public void readNBTFromFile(World world) {
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

            /*FileInputStream fileinputstream = new FileInputStream(file);
            NBTTagCompound nbttagcompound = CompressedStreamTools.readCompressed(fileinputstream);
            fileinputstream.close();*/
        } catch (IOException e) {
            System.err.println("Could not read NBT data from file:\n" + e);
        }
    }

    public  void printStringClient(String string) {}

	public void updateDoorTE(BaseDimDoor door, World world, int x, int y, int z) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof TileEntityDimDoor)
		{
			int metadata = world.getBlockMetadata(x, y, z);
			TileEntityDimDoor dimTile = (TileEntityDimDoor) tile;
			dimTile.openOrClosed = door.isDoorOnRift(world, x, y, z)&&door.isUpperDoorBlock(metadata);
			dimTile.orientation = door.func_150012_g(world, x,y,z) & 7;
			dimTile.lockStatus = door.getLockStatus(world, x, y, z);
		}
	}
    
    public void registerSidedHooks(DDProperties properties) {
        new ServerPacketHandler();
    }

    public EntityPlayer getMessagePlayer(MessageContext ctx) {
        return ctx.getServerHandler().playerEntity;
    }
}