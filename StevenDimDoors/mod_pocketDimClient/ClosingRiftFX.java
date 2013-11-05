package StevenDimDoors.mod_pocketDimClient;

import StevenDimDoors.mod_pocketDim.core.PocketManager;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClosingRiftFX extends EntityFX
{
	private int field_92049_a = 160;
	private boolean field_92054_ax;
	private boolean field_92048_ay;
	private final EffectRenderer field_92047_az;
	private float field_92050_aA;
	private float field_92051_aB;
	private float field_92052_aC;
	private boolean field_92053_aD;

	public ClosingRiftFX(World par1World, double par2, double par4, double par6, double par8, double par10, double par12, EffectRenderer par14EffectRenderer)
	{

		super(par1World, par2, par4, par6);
		this.motionX = par8;
		this.motionY = par10;
		this.motionZ = par12;
		this.field_92047_az = par14EffectRenderer;
		this.particleScale *= .55F;
		this.particleMaxAge = 30 + this.rand.nextInt(16);
		this.noClip = true;
	}

	public void func_92045_e(boolean par1)
	{
		this.field_92054_ax = par1;
	}

	public void func_92043_f(boolean par1)
	{
		this.field_92048_ay = par1;
	}

	public void func_92044_a(int par1)
	{
		float var2 = (float)((par1 & 16711680) >> 16) / 255.0F;
		float var3 = (float)((par1 & 65280) >> 8) / 255.0F;
		float var4 = (float)((par1 & 255) >> 0) / 255.0F;
		float var5 = 1.0F;
		this.setRBGColorF(var2 * var5, var3 * var5, var4 * var5);
	}

	public void func_92046_g(int par1)
	{
		this.field_92050_aA = (float)((par1 & 16711680) >> 16) / 255.0F;
		this.field_92051_aB = (float)((par1 & 65280) >> 8) / 255.0F;
		this.field_92052_aC = (float)((par1 & 255) >> 0) / 255.0F;
		this.field_92053_aD = true;
	}

	/**
	 * returns the bounding box for this entity
	 */
	 public AxisAlignedBB getBoundingBox()
	{
		 return null;
	}

	/**
	 * Returns true if this entity should push and be pushed by other entities when colliding.
	 */
	 public boolean canBePushed()
	 {
		 return false;
	 }

	 public void renderParticle(Tessellator par1Tessellator, float par2, float par3, float par4, float par5, float par6, float par7)
	 {
		 if (!this.field_92048_ay || this.particleAge < this.particleMaxAge / 3 || (this.particleAge + this.particleMaxAge) / 3 % 2 == 0)
		 {
			 this.doRenderParticle(par1Tessellator, par2, par3, par4, par5, par6, par7);
		 }
	 }

	 public void doRenderParticle(Tessellator par1Tessellator, float par2, float par3, float par4, float par5, float par6, float par7)
	 {
		 float var8 = (float)(super.particleTextureIndexX % 16) / 16.0F;
		 float var9 = var8 + 0.0624375F;
		 float var10 = (float)(this.particleTextureIndexX / 16) / 16.0F;
		 float var11 = var10 + 0.0624375F;
		 float var12 = 0.1F * this.particleScale;
		 float var13 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)par2 - interpPosX);
		 float var14 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)par2 - interpPosY);
		 float var15 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)par2 - interpPosZ);
		 float var16 = 0.8F;

		 if (PocketManager.getDimensionData(worldObj).isPocketDimension())
		 {
			 var16 = 0.4F;
		 }
		 
		 par1Tessellator.setColorRGBA_F(this.particleRed * var16, this.particleGreen * var16, this.particleBlue * var16, (float) .7);

		 par1Tessellator.addVertexWithUV((double)(var13 - par3 * var12 - par6 * var12), (double)(var14 - par4 * var12), (double)(var15 - par5 * var12 - par7 * var12), (double)var9, (double)var11);
		 par1Tessellator.addVertexWithUV((double)(var13 - par3 * var12 + par6 * var12), (double)(var14 + par4 * var12), (double)(var15 - par5 * var12 + par7 * var12), (double)var9, (double)var10);
		 par1Tessellator.addVertexWithUV((double)(var13 + par3 * var12 + par6 * var12), (double)(var14 + par4 * var12), (double)(var15 + par5 * var12 + par7 * var12), (double)var8, (double)var10);
		 par1Tessellator.addVertexWithUV((double)(var13 + par3 * var12 - par6 * var12), (double)(var14 - par4 * var12), (double)(var15 + par5 * var12 - par7 * var12), (double)var8, (double)var11);
	 }

	 /**
	  * Called to update the entity's position/logic.
	  */
	  public void onUpdate()
	 {
		 this.prevPosX = this.posX;
		 this.prevPosY = this.posY;
		 this.prevPosZ = this.posZ;

		 if (this.particleAge++ >= this.particleMaxAge)
		 {
			 this.setDead();
		 }

		 if (this.particleAge > this.particleMaxAge / 2)
		 {
			 this.setAlphaF(1.0F - ((float)this.particleAge - (float)(this.particleMaxAge / 2)) / (float)this.particleMaxAge);

			 if (this.field_92053_aD)
			 {
				 this.particleRed += (this.field_92050_aA - this.particleRed) * 0.2F;
				 this.particleGreen += (this.field_92051_aB - this.particleGreen) * 0.2F;
				 this.particleBlue += (this.field_92052_aC - this.particleBlue) * 0.2F;
			 }
		 }

		 this.setParticleTextureIndex(this.field_92049_a + (7 - this.particleAge * 8 / this.particleMaxAge));
		 // this.motionY -= 0.004D;
		 this.moveEntity(this.motionX, this.motionY, this.motionZ);
		 this.motionX *= 0.9100000262260437D;
		 this.motionY *= 0.9100000262260437D;
		 this.motionZ *= 0.9100000262260437D;

		 if (this.onGround)
		 {
			 this.motionX *= 0.699999988079071D;
			 this.motionZ *= 0.699999988079071D;
		 }

		 if (this.field_92054_ax && this.particleAge < this.particleMaxAge / 2 && (this.particleAge + this.particleMaxAge) % 2 == 0)
		 {
			 ClosingRiftFX var1 = new ClosingRiftFX(this.worldObj, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D, this.field_92047_az);
			 var1.setRBGColorF(this.particleRed, this.particleGreen, this.particleBlue);
			 var1.particleAge = var1.particleMaxAge / 2;

			 if (this.field_92053_aD)
			 {
				 var1.field_92053_aD = true;
				 var1.field_92050_aA = this.field_92050_aA;
				 var1.field_92051_aB = this.field_92051_aB;
				 var1.field_92052_aC = this.field_92052_aC;
			 }

			 var1.field_92048_ay = this.field_92048_ay;
			 this.field_92047_az.addEffect(var1);
		 }
	 }

	  public int getBrightnessForRender(float par1)
	  {
		  return 15728880;
	  }

	  /**
	   * Gets how bright this entity is.
	   */
	  public float getBrightness(float par1)
	  {
		  return 1.0F;
	  }
}