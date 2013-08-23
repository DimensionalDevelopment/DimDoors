package StevenDimDoors.mod_pocketDim;

import StevenDimDoors.mod_pocketDim.core.NewLinkData;
import StevenDimDoors.mod_pocketDim.helpers.dimHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class pocketTeleporter
{
	int x,y,z;
	
	NewLinkData sendingLink;
	

 public pocketTeleporter() 
	 
	 {
		
		 
		
	 }
	

	    /**
	     * Create a new portal near an entity.
	     */
	
	 public void placeInPortal(Entity par1Entity, WorldServer world, NewLinkData link)
	 {
		 
		
			this.x=link.destXCoord;
			this.y=link.destYCoord;
			this.z=link.destZCoord;
			
			this.sendingLink=link;
			
			int id;
	         
        	//TODO Temporary workaround for mismatched door/rift metadata cases. Gives priority to the door. 
           	id=dimHelper.instance.getDestOrientation(sendingLink);
           	int receivingDoorMeta=world.getBlockMetadata(link.destXCoord, link.destYCoord-1, link.destZCoord);
           	int recevingDoorID=world.getBlockId(link.destXCoord, link.destYCoord, link.destZCoord);
           	if(receivingDoorMeta!=id)
           	{
           		if(recevingDoorID==mod_pocketDim.dimDoor.blockID||recevingDoorID==mod_pocketDim.ExitDoor.blockID)
           		{
               		dimHelper.instance.getLinkDataFromCoords(link.destXCoord, link.destYCoord, link.destZCoord, world).linkOrientation=receivingDoorMeta;
               		id=receivingDoorMeta;

           		}
           	}
           
           	
       
		 if(par1Entity instanceof EntityPlayer)
		 {
		 	EntityPlayer player = (EntityPlayer) par1Entity;
	    
	    	
		 
           
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
			

			
			 par1Entity.rotationYaw=(id*90)+90;
   
			 if(id==2||id==6)
			 {
				
			
				 this.setEntityPosition(par1Entity, x+1.5, y, z+.5 );
				 par1Entity.motionX =.39;
				   par1Entity.worldObj.updateEntityWithOptionalForce(par1Entity, false);

			 }
			 else if(id==3||id==7)
			 {
     	
				
				 this.setEntityPosition(par1Entity, x+.5, y, z+1.5 );
				 par1Entity.motionZ =.39;
				   par1Entity.worldObj.updateEntityWithOptionalForce(par1Entity, false);


				 
			 }
			 else if(id==0||id==4)
			 {
     	
				
				 this.setEntityPosition(par1Entity,x-.5, y, z+.5);
				 par1Entity.motionX =-.39;
				   par1Entity.worldObj.updateEntityWithOptionalForce(par1Entity, false);


			 }
			 else if(id==1||id==5)
			 {
				
				 this.setEntityPosition(par1Entity,x+.5, y, z-.5);	
				 par1Entity.motionZ =-.39;
				   par1Entity.worldObj.updateEntityWithOptionalForce(par1Entity, false);


			 }
			 else
			 {
				 this.setEntityPosition(par1Entity,x, y, z);	

			 }
 		
		
     
     
		}
		 
		 
		 else if(par1Entity instanceof   Entity)
		 {
			
			 //System.out.println("Teleporting with link oreintation "+id);


			 par1Entity.rotationYaw=(id*90)+90;
   
	//		 EntityMinecart.class.cast(par1Entity).isinreverse=false;
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
		
	    }

	 public void setEntityPosition(Entity entity, double x, double y, double z)
	 {
		 entity.lastTickPosX = entity.prevPosX = entity.posX = x;
		 entity.lastTickPosY = entity.prevPosY = entity.posY = y + (double)entity.yOffset;
		 entity.lastTickPosZ = entity.prevPosZ = entity.posZ = z;
		 entity.setPosition(x, y, z);
	 }
		 
			

	   

}
