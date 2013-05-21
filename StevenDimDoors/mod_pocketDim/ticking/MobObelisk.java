package StevenDimDoors.mod_pocketDim.ticking;

import net.minecraft.entity.monster.EntityMob;
import net.minecraft.world.World;

public class MobObelisk extends EntityMob
{

	public MobObelisk(World par1World) 
	{
		super(par1World);
		this.texture="/mods/DimensionalDoors/textures/mobs/Monolith.png";
		this.setSize(4.0F, 4.0F);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getMaxHealth() 
	{
		// TODO Auto-generated method stub
		return 20;
	}
	
}