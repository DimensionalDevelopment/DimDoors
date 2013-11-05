package StevenDimDoors.mod_pocketDim;
import java.io.File;
import java.io.FileOutputStream;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IGuiHandler;
public class CommonProxy implements IGuiHandler
{
    public static String BLOCK_PNG = "/PocketBlockTextures.png";
    public static String ITEM_PNG = "/PocketItemTextures.png";
    public static String RIFT_PNG = "/RIFT.png";
    public static String RIFT2_PNG = "/RIFT2.png";
    public static String WARP_PNG = "/WARP.png";

    public  void registerRenderers()

    {
    }
    public void registerEntity(Class <? extends Entity > entity, String entityname, int id, Object mod, int trackingrange, int updateFreq, boolean updatevelo)
    {
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        return null;
    }

    public void loadTextures()
    {
    }

    public void writeNBTToFile(World world)
    {
        boolean flag = true;

        try
        {
            File dataStore = world.getSaveHandler().getMapFileFromName("idcounts");
            String dirFolder = dataStore.getCanonicalPath();
            dirFolder = dirFolder.replace("idcounts.dat", "");

            if (!flag)
            {
                dirFolder.replace("saves/", FMLCommonHandler.instance().getMinecraftServerInstance().getFolderName());
            }

            File file = new File(dirFolder, "GGMData.dat");

            if (!file.exists())
            {
                file.createNewFile();
            }

            FileOutputStream fileoutputstream = new FileOutputStream(file);
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            

            
            CompressedStreamTools.writeCompressed(nbttagcompound, fileoutputstream);
            fileoutputstream.close();
        }
        catch (Exception exception)
        {
         //   exception.printStackTrace();

            if (!(exception instanceof NullPointerException))
            {
            }

            flag = false;
        }
    }

    public void readNBTFromFile(World world)
    {
        boolean flag = true;

        try
        {
            File dataStore = world.getSaveHandler().getMapFileFromName("idcounts");
            String dirFolder = dataStore.getCanonicalPath();
            dirFolder = dirFolder.replace("idcounts.dat", "");

            if (!flag)
            {
                dirFolder.replace("saves/", FMLCommonHandler.instance().getMinecraftServerInstance().getFolderName());
            }

            File file = new File(dirFolder, "GGMData.dat");

            if (!file.exists())
            {
                file.createNewFile();
                FileOutputStream fileoutputstream = new FileOutputStream(file);
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                
                CompressedStreamTools.writeCompressed(nbttagcompound, fileoutputstream);
                fileoutputstream.close();
            }

            /*FileInputStream fileinputstream = new FileInputStream(file);
            NBTTagCompound nbttagcompound = CompressedStreamTools.readCompressed(fileinputstream);
            fileinputstream.close();*/
        }
        catch (Exception exception)
        {
           // exception.printStackTrace();

            if (!(exception instanceof NullPointerException))
            {
            }

            flag = false;
        }
    }

    public  void printStringClient(String string)
    {
    	
    }
   
    
}