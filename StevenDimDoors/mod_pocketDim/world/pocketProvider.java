package StevenDimDoors.mod_pocketDim.world;

import StevenDimDoors.mod_pocketDim.CloudRenderBlank;
import StevenDimDoors.mod_pocketDim.DDProperties;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.helpers.dimHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.WorldChunkManagerHell;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.DimensionManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class pocketProvider extends WorldProvider
{
	public int exitXCoord;
	public int exitYCoord;
	public int exitZCoord;
	public int exitDimID;
	public boolean hasNoSky = true;

	public boolean isSavingSchematic= false;
	public int dimToSave;
	private static DDProperties properties = null;

	public pocketProvider()
	{
		this.hasNoSky=true;
		if (properties == null)
			properties = DDProperties.instance();
	}

	@Override
	protected void registerWorldChunkManager()
	{
		super.worldChunkMgr = new WorldChunkManagerHell(mod_pocketDim.pocketBiome,1,1);
		//this.dimensionId = ConfigAtum.dimensionID;
	}
	
	@Override
	public String getSaveFolder()
	{
		return (dimensionId == 0 ? null : "DimensionalDoors/pocketDimID" + dimensionId);
	}

	public void saveAsSchematic(int id)
	{
		this.isSavingSchematic=true;
		this.dimensionId=id;

	}

	public Vec3 getSkyColor(Entity cameraEntity, float partialTicks)
	{
		setCloudRenderer( new CloudRenderBlank());
		return this.worldObj.getWorldVec3Pool().getVecFromPool((double)0, (double)0, (double)0);

	}



	@SideOnly(Side.CLIENT)
	@Override
	public Vec3 getFogColor(float par1, float par2)
	{
		return this.worldObj.getWorldVec3Pool().getVecFromPool((double)0, (double)0, (double)0);

	}

	@Override
	public double getHorizon()
	{
		return worldObj.getHeight();
	}

	@Override
	public IChunkProvider createChunkGenerator()
	{
		return new pocketGenerator(worldObj, dimensionId, false);
	}

	@Override
	public boolean canSnowAt(int x, int y, int z)
	{
		return false;
	}
	
	public boolean canBlockFreeze(int x, int y, int z, boolean byWater)
	{
		return false;
	}

	@Override
	public String getDimensionName() 
	{
		//TODO: This should be a proper name. We need to show people proper names for things whenever possible.
		//The question is whether this should be "Pocket Dimension" or "Pocket Dimension #" -- I'm not going to change
		//it out of concern that it could break something. ~SenseiKiwi
		return "PocketDim " + this.dimensionId;
	}

	public int getRespawnDimension(EntityPlayerMP player)
	{
		int respawnDim;

		if (properties.LimboEnabled)
		{
			respawnDim = properties.LimboDimensionID;
		}
		else
		{
			respawnDim = dimHelper.dimList.get(this.dimensionId).exitDimLink.destDimID;
		}

		if (dimHelper.getWorld(respawnDim) == null)
		{
			dimHelper.initDimension(respawnDim);
		}
		return respawnDim;
	}

	public boolean canRespawnHere()
	{
		return false;
	}
}
