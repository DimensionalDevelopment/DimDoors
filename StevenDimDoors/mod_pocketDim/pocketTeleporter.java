package StevenDimDoors.mod_pocketDim;

import net.minecraft.entity.Entity;
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
	    	EntityPlayer player = (EntityPlayer) par1Entity;
	    
	    	
            int id;
         
            	
            		id=dimHelper.instance.getDestOrientation(sendingLink);
            		//System.out.println("Teleporting with link oreintation "+id);

         //   System.out.println(id);
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
