package StevenDimDoors.mod_pocketDim;

import java.util.List;
import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;

public class Spells
{
	public EntityPlayer player;
	public int spellID;
	public int castingTime=0;
	
	public int fireCastTime=40;
	public int smokeCastTime=5;
	public int plagueCastTime=5;
	
	public Random rand= new Random();
	
	 public int fireSpell=1;
	    public int smokeVanish=2;
	    public int enderPush=3;
	    public int flood=4;
	    public int nightVision=5;
	    public int heal = 6;
	    public int plague = 7;
	    public int butcher = 8;
	    
	    
	public Spells(EntityPlayer player, int spellID)
	{
		this.spellID=spellID;
		this.player=player;
	}
	
	public boolean cast()
	{
		this.castingTime++;

		if(this.spellID==this.fireSpell)
		{
			return this.castFire();
			
		}
		
		if(this.spellID==this.smokeVanish)
		{
			return this.castSmoke();
		}
		
		
		
		
		return false;
	}
	
	public boolean castSmoke()
	{
		
		if(!this.player.isPotionActive(Potion.invisibility))
		{
			this.player.addPotionEffect(new PotionEffect(Potion.invisibility.id, 2 * 200, 200));
		}
		
		if(this.castingTime<this.smokeCastTime)
		{
			for(int i = 0; i<16;i++)
			{
				this.player.worldObj.spawnParticle("largesmoke", this.player.posX+this.rand.nextDouble()-.5, this.player.posY+(this.rand.nextDouble()-.5)*2, this.player.posZ+this.rand.nextDouble()-.5, 
						this.rand.nextGaussian()/50,this.rand.nextGaussian()/50,this.rand.nextGaussian()/50);
		
			}
			return true;
		}
		else return false;
		
	}
	
	public boolean castFire()
	{
		
	
		
		if(this.castingTime<this.fireCastTime)
		{
			for(int i = 0; i<50;i++)
			{
				this.player.worldObj.spawnParticle("flame", this.player.posX+this.rand.nextDouble()-.5, this.player.posY+this.rand.nextDouble()-.5, this.player.posZ+this.rand.nextDouble()-.5, 
					(this.rand.nextDouble()-.5)/3, (this.rand.nextDouble()-.5)/3, (this.rand.nextDouble()-.5/3));
				
				this.player.worldObj.spawnParticle("flame", this.player.posX+this.rand.nextDouble()-.5, this.player.posY+this.rand.nextDouble()-.5, this.player.posZ+this.rand.nextDouble()-.5, 
						(this.rand.nextDouble()-.5), (this.rand.nextDouble()-.45)/2, (this.rand.nextDouble()-.5));
				this.player.worldObj.spawnParticle("flame", this.player.posX+this.rand.nextDouble()-.5, this.player.posY+this.rand.nextDouble()-.5, this.player.posZ+this.rand.nextDouble()-.5, 
						(this.rand.nextDouble()-.5)/10, (this.rand.nextDouble()-.45)/20, (this.rand.nextDouble()-.5)/10);
				this.player.worldObj.spawnParticle("flame", this.player.posX+this.rand.nextDouble()-.5, this.player.posY+this.rand.nextDouble()-.5, this.player.posZ+this.rand.nextDouble()-.5, 
						(this.rand.nextDouble()-.5)*2, (this.rand.nextDouble()-.45), (this.rand.nextDouble()-.5)*2);
			}
		
		}
		
		
	  	List<Entity> list = player.worldObj.getEntitiesWithinAABBExcludingEntity(player,AxisAlignedBB.getBoundingBox( player.posX-7, player.posY-3, player.posZ-7, player.posX+7, player.posY+7, player.posZ+7));
	  	for(Entity entity : list)
	  	{
	  		entity.setFire(3);
	  		entity.attackEntityFrom(DamageSource.lava, 2);
	  	}
	  	
	  	return true;
	}
	/**
	public boolean castEnderPush()
	{
		
	}
	
	public boolean castFlood()
	{
		
	}
	
	public boolean castNightVision()
	{
		
	}
	
	public boolean castPlague()
	{
		
	}
	**/
}