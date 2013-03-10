package StevenDimDoors.mod_pocketDim;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class pocketTeleporter extends Teleporter
{
	int x,y,z;
	World world;
	LinkData sendingLink;
	

 public pocketTeleporter(WorldServer par1WorldServer, LinkData link) 
	 
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
		 if(par1Entity instanceof EntityPlayer)
		 {
		 	EntityPlayer player = (EntityPlayer) par1Entity;
	    
	    	
		 	int id;
         
            	
           	id=dimHelper.instance.getDestOrientation(sendingLink);
            		//System.out.println("Teleporting with link oreintation "+id);

       
           	player.rotationYaw=(id*90)+90;
            if(id==2||id==6)
            {
            	player.setPositionAndUpdate( x+1.5, y-1, z+.5 );
                	

                }
                else if(id==3||id==7)
                {
                	
                	player.setPositionAndUpdate( x+.5, y-1, z+1.5 );
                

                }
                else if(id==0||id==4)
                {
                	
                	player.setPositionAndUpdate(x-.5, y-1, z+.5);
                
                }
                else if(id==1||id==5)
                {
                	player.setPositionAndUpdate(x+.5, y-1, z-.5);	
                	

                }
                else
                {
                	player.setPositionAndUpdate(x, y-1, z);	

                }
                
                
                
		 }
		 else if(par1Entity instanceof   EntityMinecart)
		 {
			 par1Entity.motionX=0;
			 par1Entity.motionZ=0;
			 par1Entity.motionY=0;
			 int id;
         
			 id=dimHelper.instance.getDestOrientation(sendingLink);


			
   
			 if(id==2||id==6)
			 {
				
				 par1Entity.motionX =2;
				 this.setEntityPosition(par1Entity, x+1.5, y, z+.5 );
			 }
			 else if(id==3||id==7)
			 {
     	
				 par1Entity.motionZ =2;
				 this.setEntityPosition(par1Entity, x+.5, y, z+1.5 );

				 
			 }
			 else if(id==0||id==4)
			 {
     	
				 par1Entity.motionX =-2;
				 this.setEntityPosition(par1Entity,x-.5, y, z+.5);

			 }
			 else if(id==1||id==5)
			 {
				 par1Entity.motionZ =-2;
				 this.setEntityPosition(par1Entity,x+.5, y, z-.5);	

			 }
			 else
			 {
				 this.setEntityPosition(par1Entity,x, y, z);	

			 }
 		
			 par1Entity.rotationYaw=(id*90)+90;
     
     
		}
		 
		 
		 else if(par1Entity instanceof   Entity)
		 {
			 int id;
         
     	
			 id=dimHelper.instance.getDestOrientation(sendingLink);
			 //System.out.println("Teleporting with link oreintation "+id);


			 par1Entity.rotationYaw=(id*90)+90;
   
			 if(id==2||id==6)
			 {
				 this.setEntityPosition(par1Entity, x+1.5, y, z+.5 );
     	

			 }
			 else if(id==3||id==7)
			 {
     	
				 this.setEntityPosition(par1Entity, x+.5, y, z+1.5 );
     

			 }
			 else if(id==0||id==4)
			 {
     	
				 this.setEntityPosition(par1Entity,x-.5, y, z+.5);
     
			 }
			 else if(id==1||id==5)
			 {
				 this.setEntityPosition(par1Entity,x+.5, y, z-.5);	
     	

			 }
			 else
			 {
				 this.setEntityPosition(par1Entity,x, y, z);	

			 }
 		
     
     
     
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
	    public boolean func_85188_a(Entity par1Entity)
	    {
	    	return true;
	    }
	    @Override
	    public void func_85189_a(long par1)
	    {
	    
	    }
}
