package StevenDimDoors.mod_pocketDim.ticking;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class MobObelisk extends EntityFlying implements IMob
{

	public MobObelisk(World par1World) 
	{
		super(par1World);
		this.texture="/mods/DimensionalDoors/textures/mobs/Monolith.png";
		this.setSize(2.5F, 7.0F);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getMaxHealth() 
	{
		// TODO Auto-generated method stub
		return 20;
	}
	
	@Override
	public void onEntityUpdate()
	{
	//	if(rand.nextInt(10)==0)
		{
			EntityPlayer entityPlayer = this.worldObj.getClosestPlayerToEntity(this, 100);
			if(entityPlayer == null)
			{
				return;
			}
			this.faceEntity(entityPlayer, 100, 100);
		}
		super.onEntityUpdate();
		
		System.out.println(this.rotationYaw);
		System.out.println(this.rotationYawHead);

	}
	
	  public void faceEntity(Entity par1Entity, float par2, float par3)
	    {
	        double d0 = par1Entity.posX - this.posX;
	        double d1 = par1Entity.posZ - this.posZ;
	        double d2;

	        if (par1Entity instanceof EntityLiving)
	        {
	            EntityLiving entityliving = (EntityLiving)par1Entity;
	            d2 = entityliving.posY + (double)entityliving.getEyeHeight() - (this.posY + (double)this.getEyeHeight());
	        }
	        else
	        {
	            d2 = (par1Entity.boundingBox.minY + par1Entity.boundingBox.maxY)  - (this.posY + (double)this.getEyeHeight());
	        }

	        double d3 = (double)MathHelper.sqrt_double(d0 * d0 + d1 * d1);
	        float f2 = (float)(Math.atan2(d1, d0) * 180.0D / Math.PI) - 90.0F;
	        float f3 = (float)(-(Math.atan2(d2, d3) * 180.0D / Math.PI));
	        this.rotationYaw =  f2;
	        this.rotationYawHead=f2;
	    }
	  
	  private float updateRotation(float par1, float par2, float par3)
	    {
	        float f3 = MathHelper.wrapAngleTo180_float(par2 - par1);

	        if (f3 > par3)
	        {
	            f3 = par3;
	        }

	        if (f3 < -par3)
	        {
	            f3 = -par3;
	        }

	        return par1 + f3;
	    }
	
	
}