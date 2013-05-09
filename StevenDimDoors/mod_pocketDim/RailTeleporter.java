package StevenDimDoors.mod_pocketDim;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class RailTeleporter extends Teleporter
{
	int x,y,z;
	World world;
	LinkData sendingLink;
	

 public RailTeleporter(WorldServer par1WorldServer, LinkData link) 
	 
	 {
		
		 
		super(par1WorldServer);
		this.x=link.destXCoord;
		this.y=link.destYCoord;
		this.z=link.destZCoord;
		this.sendingLink=link;
		 world = par1WorldServer;
	 }
	

	    /**
	     * Create a new portal near an entity.
	     */
	 @Override
	    public void placeInPortal(Entity par1Entity, double par2, double par4, double par6, float par8)
	    {
		
		    	
	            int id;
	         
	            	
	            		id=dimHelper.instance.getDestOrientation(sendingLink);
	            		//System.out.println("Teleporting with link oreintation "+id);

	       
	            		par1Entity.rotationYaw=(id*90)+90;
	                if(id==2||id==6)
	                {
	                	
	                
	                	this.setEntityPosition(par1Entity, x+2.5, y, z+.5 );
	                	

	                }
	                else if(id==3||id==7)
	                {
	                	
	                	this.setEntityPosition(par1Entity, x+.5, y, z+2.5 );
	                

	                }
	                else if(id==0||id==4)
	                {
	                	
	                	this.setEntityPosition(par1Entity,x-1.5, y, z+.5);
	                
	                }
	                else if(id==1||id==5)
	                {
	                
	                	this.setEntityPosition(par1Entity,x+.5, y, z-1.5);	
	                	

	                }
	                else
	                {
	                	
	                	this.setEntityPosition(par1Entity,x, y, z);	

	                }
	              
	                par1Entity.worldObj.updateEntityWithOptionalForce(par1Entity, false);
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
