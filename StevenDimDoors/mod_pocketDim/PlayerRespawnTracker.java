

package StevenDimDoors.mod_pocketDim;


import net.minecraft.entity.item.EntityItem;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import cpw.mods.fml.common.IPlayerTracker;


public class PlayerRespawnTracker implements IPlayerTracker
{
	private final DDProperties properties;
	
	public PlayerRespawnTracker(DDProperties properties)
	{
		this.properties = properties;
	}
	
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
		if(player.worldObj.provider.dimensionId==properties.LimboDimensionID)
			{
			
			if(!player.worldObj.isRemote && properties.LimboReturnsInventoryEnabled)
			{

				if(player.username!=null)
				{
					
					if(!mod_pocketDim.limboSpawnInventory.isEmpty()&&mod_pocketDim.limboSpawnInventory.containsKey(player.username))
					{
						for(EntityItem drop :  mod_pocketDim.limboSpawnInventory.get(player.username))
						{
							if(drop.getEntityItem().getItem() instanceof ItemArmor)
							{
								
							
							}
							player.inventory.addItemStackToInventory(drop.getEntityItem());
				
				
						}	
					}
				}
			
				
				
			
			}
		
		}

		
		
	}
	
}