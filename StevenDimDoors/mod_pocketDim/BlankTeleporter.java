package StevenDimDoors.mod_pocketDim;

import net.minecraft.entity.Entity;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public class BlankTeleporter extends Teleporter
{
	

	public BlankTeleporter(WorldServer par1WorldServer) 
 	{
	 	super(par1WorldServer);
	}	
	

	    /**
	     * Create a new portal near an entity.
	     */
	 @Override
	 public void placeInPortal(Entity par1Entity, double par2, double par4, double par6, float par8)
	 {
		        
	 }
		 
		 
	    

	 public void setEntityPosition(Entity entity, double x, double y, double z)
	 {
		 entity.lastTickPosX = entity.prevPosX = entity.posX = x;
		 entity.lastTickPosY = entity.prevPosY = entity.posY = y + (double)entity.yOffset;
		 entity.lastTickPosZ = entity.prevPosZ = entity.posZ = z;
		 entity.setPosition(x, y, z);
	 }
	  
	 @Override
	 public void removeStalePortalLocations(long par1)
	 {
	    
	 }
}
