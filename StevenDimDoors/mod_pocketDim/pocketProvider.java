package StevenDimDoors.mod_pocketDim;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.IChunkProvider;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class pocketProvider extends WorldProvider

{
	public int exitXCoord;
	public int exitYCoord;
	public int exitZCoord;
	public int exitDimID;
	public boolean hasNoSky = true;
	public pocketProvider()
	{
		this.hasNoSky=true;
		
	}
  //  @SideOnly(Side.CLIENT)
	@Override
	public String getSaveFolder()
    {
        return (dimensionId == 0 ? null : "DimensionalDoors/pocketDimID" + dimensionId);
    }
	public Vec3 getSkyColor(Entity cameraEntity, float partialTicks)
	{
    	setCloudRenderer( new CloudRenderBlank());
        return this.worldObj.getWorldVec3Pool().getVecFromPool((double)0, (double)0, (double)0);

	}
	
	
	 public void setAllowedSpawnTypes(boolean allowHostile, boolean allowPeaceful)
	 {
	       super.setAllowedSpawnTypes(false, false);
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
		// TODO Auto-generated method stub
		return "PocketDim "+this.dimensionId;
	}
	
	
	public int getRespawnDimension(EntityPlayerMP player)
	{
		if(mod_pocketDim.isLimboActive)
		{
	       return mod_pocketDim.limboDimID;
		}
		else
		{
			return dimHelper.dimList.get(this.dimensionId).exitDimLink.destDimID;
		}
	}
	
	 public boolean canRespawnHere()
	    {
	        return false;
	    }

	

}
