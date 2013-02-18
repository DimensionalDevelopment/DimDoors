

package StevenDimDoors.mod_pocketDim;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import cpw.mods.fml.common.IPlayerTracker;


public class PlayerRespawnTracker implements IPlayerTracker
{
	

	@Override
	public void onPlayerLogin(EntityPlayer player) {
		// TODO Auto-generated method stub
	
	}

	@Override
	public void onPlayerLogout(EntityPlayer player) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPlayerChangedDimension(EntityPlayer player) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPlayerRespawn(EntityPlayer player) 
	{
		if(player.worldObj.provider.dimensionId==mod_pocketDim.limboDimID)
			{
			
			if(!player.worldObj.isRemote)
			{

				for(EntityItem drop :  mod_pocketDim.limboSpawnInventory)
				{
					
					player.inventory.addItemStackToInventory(drop.func_92014_d());
				
				
				}
			
				
				
			
			}
		
		}

		
		
	}
	
}