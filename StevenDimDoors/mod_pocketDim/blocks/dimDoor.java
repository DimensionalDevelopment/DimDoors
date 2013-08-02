package StevenDimDoors.mod_pocketDim.blocks;

import java.util.Random;

import StevenDimDoors.mod_pocketDim.DDProperties;
import StevenDimDoors.mod_pocketDim.LinkData;
import StevenDimDoors.mod_pocketDim.TileEntityDimDoor;
import StevenDimDoors.mod_pocketDim.mod_pocketDim;
import StevenDimDoors.mod_pocketDim.helpers.dimHelper;
import StevenDimDoors.mod_pocketDim.schematic.BlockRotator;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class dimDoor extends BlockContainer
{
	private static Icon blockIconBottom;
	public dimDoor(int par1, Material material) 
	{
		super(par1, Material.iron);
	//	this.blockIndexInTexture = 18;

		if (properties == null)
			properties = DDProperties.instance();
	}
	
	private static DDProperties properties = null;
	

	public void registerIcons(IconRegister par1IconRegister)
    {
        this.blockIcon = par1IconRegister.registerIcon(mod_pocketDim.modid + ":" + this.getUnlocalizedName2()+"_top");
        this.blockIconBottom = par1IconRegister.registerIcon(mod_pocketDim.modid + ":" + this.getUnlocalizedName2()+"_bottom");

    }
	
	 
	//spawns the rift attatched to the block. Doesnt work in creative mode for some reason
	 //TODO make work in creative
    public void onBlockDestroyedByPlayer(World par1World, int par2, int par3, int par4, int par5) 
    {
    	if(!par1World.isRemote)
    	{
    		if(dimHelper.instance.getLinkDataFromCoords(par2, par3, par4, par1World)!=null)
    		{
    			LinkData link= dimHelper.instance.getLinkDataFromCoords(par2, par3, par4, par1World);
    			par1World.setBlock(par2, par3, par4, properties.RiftBlockID);
    		
    		}
    		 if(dimHelper.instance.getLinkDataFromCoords(par2, par3-1, par4, par1World)!=null)
    		{
    			LinkData link= dimHelper.instance.getLinkDataFromCoords(par2, par3-1, par4, par1World);
    			par1World.setBlock(par2, par3-1, par4, properties.RiftBlockID);
    		
    		}
    		 if(dimHelper.instance.getLinkDataFromCoords(par2, par3+1, par4, par1World)!=null)
    		{
    			LinkData link= dimHelper.instance.getLinkDataFromCoords(par2, par3+1, par4, par1World);
    			par1World.setBlock(par2, par3+1, par4, properties.RiftBlockID);
    		
    		}
    					
    	}
    	
    }

    //finds the rift data and teleports the player to it. 
    public void onEntityCollidedWithBlock(World par1World, int par2, int par3, int par4, Entity par5Entity) 
    {
    
    	if(!par1World.isRemote)
    	{
    		int var12 = (int) (MathHelper.floor_double((double)((par5Entity.rotationYaw+90) * 4.0F / 360.0F) + 0.5D) & 3);
       
    		int num=0;
    		LinkData linkData=null;
    		
    		if(par1World.getBlockId(par2, par3-1, par4)==this.blockID)
    		{
    			num=par1World.getBlockMetadata(par2, par3-1, par4);
    			linkData= dimHelper.instance.getLinkDataFromCoords(par2, par3, par4, par1World);
    		}
    		
    		if(par1World.getBlockId(par2, par3+1, par4)==this.blockID)
    		{
    			num=par1World.getBlockMetadata(par2, par3, par4);
    			linkData= dimHelper.instance.getLinkDataFromCoords(par2, par3+1, par4, par1World);
    		}
    		
    		if(!(par5Entity instanceof EntityPlayer)&&num>3)
    		{
    			this.onPoweredBlockChange(par1World, par2, par3, par4, false);
				dimHelper.instance.traverseDimDoor(par1World, linkData, par5Entity);
    		}
    		
    		else if(!par1World.isRemote&&(num==5||num==4||num==6||num==7)&&(num-4)==var12)
    		{
    			
    				//int destinationID= dimHelper.instance.getDestIDFromCoords(par2, par3, par4, par1World);
    		 
    			
    			this.onPoweredBlockChange(par1World, par2, par3, par4, false);
				
				dimHelper.instance.traverseDimDoor(par1World, linkData, par5Entity);

    				

    				
    				

    			
    	
    			
    			
				
			
    		}
    	}
    }

	public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9)
    {
        
     boolean shouldOpen=true;
     
    		//System.out.println(String.valueOf(par1World.getBlockMetadata(par2, par3, par4)));
		if(par5EntityPlayer.inventory.getCurrentItem()!=null)
		{
			if(par5EntityPlayer.inventory.getCurrentItem().getItem() == mod_pocketDim.itemRiftBlade)
			{
				shouldOpen=false;
				if(!par1World.isRemote&&par1World.getBlockId(par2, par3-1, par4)==this.blockID)
				{
					int var12 = (int) (MathHelper.floor_double((double)((par5EntityPlayer.rotationYaw+90) * 4.0F / 360.0F) + 0.5D) & 3);
	    		
					if(par1World.getBlockMetadata(par2, par3-1, par4)==var12)
					{
						var12 = BlockRotator.transformMetadata(var12, 2, Block.doorWood.blockID);
					}
					par1World.setBlockMetadataWithNotify(par2, par3-1, par4, var12,2);
					
					if(	dimHelper.instance.getLinkDataFromCoords(par2, par3, par4, par1World)!=null)
					{
						dimHelper.instance.getLinkDataFromCoords(par2, par3, par4, par1World).linkOrientation= par1World.getBlockMetadata(par2, par3-1, par4);
	    			
					}
				}
				if(!par1World.isRemote&&par1World.getBlockId(par2, par3+1, par4)==this.blockID)
				{
					int var12 = (int) (MathHelper.floor_double((double)((par5EntityPlayer.rotationYaw+90) * 4.0F / 360.0F) + 0.5D) & 3);
					if(par1World.getBlockMetadata(par2, par3, par4)==var12)
					{ 
						var12 = BlockRotator.transformMetadata(var12, 2, Block.doorWood.blockID);
					}
					par1World.setBlockMetadataWithNotify(par2, par3, par4, var12,2);
					
					if(	dimHelper.instance.getLinkDataFromCoords(par2, par3+1, par4, par1World)!=null)
					{
						dimHelper.instance.getLinkDataFromCoords(par2, par3+1, par4, par1World).linkOrientation= par1World.getBlockMetadata(par2, par3, par4);
					}
					
				}
	            par1World.playAuxSFXAtEntity(par5EntityPlayer, 1001, par2, par3, par4, 0);

			if(!shouldOpen&&!par1World.isRemote)
			{

				par5EntityPlayer.inventory.getCurrentItem().damageItem(5, par5EntityPlayer);
			
			//	par5EntityPlayer.sendChatToPlayer("You wedge the stick into a cranny in the door attempt to rotate the it");
			//	par5EntityPlayer.sendChatToPlayer("The door rotates, but the stick breaks in half and is lost");
			}
			

			
		}
	}
		
     if(shouldOpen)
     {
        	
            int var10 = this.getFullMetadata(par1World, par2, par3, par4);
            int var11 = var10 & 7;
            var11 ^= 4;

            if ((var10 & 8) == 0)
            {
                par1World.setBlockMetadataWithNotify(par2, par3, par4, var11,2);
                par1World.markBlockRangeForRenderUpdate(par2, par3, par4, par2, par3, par4);
            }
            else
            {
                par1World.setBlockMetadataWithNotify(par2, par3 - 1, par4, var11,2);
                par1World.markBlockRangeForRenderUpdate(par2, par3 - 1, par4, par2, par3, par4);
            }

            par1World.playAuxSFXAtEntity(par5EntityPlayer, 1003, par2, par3, par4, 0);
            if(dimHelper.instance.getLinkDataFromCoords(par2, par3, par4, par1World)!=null)
            {
          //  	System.out.println("Link orient is- " +dimHelper.instance.getLinkDataFromCoords(par2, par3, par4, par1World).linkOrientation);
            }
            return true;
     }
     else 
     {
    	 return false;
     }
       
    }

    /**
     * A function to open a door.
     */
    public void onPoweredBlockChange(World par1World, int par2, int par3, int par4, boolean par5)
    {
        int var6 = this.getFullMetadata(par1World, par2, par3, par4);
        boolean var7 = (var6 & 4) != 0;

        if (var7 != par5)
        {
            int var8 = var6 & 7;
            var8 ^= 4;

            if ((var6 & 8) == 0)
            {
                par1World.setBlockMetadataWithNotify(par2, par3, par4, var8,2);
                par1World.markBlockRangeForRenderUpdate(par2, par3, par4, par2, par3, par4);
            }
            else
            {
                par1World.setBlockMetadataWithNotify(par2, par3 - 1, par4, var8,2);
                par1World.markBlockRangeForRenderUpdate(par2, par3 - 1, par4, par2, par3, par4);
            }

            par1World.playAuxSFXAtEntity((EntityPlayer)null, 1003, par2, par3, par4, 0);
        }
    }
//TODO simplify this
	@Override
	public void onBlockAdded(World par1World, int par2, int par3, int par4) 
	{
		if(!par1World.isRemote&&par1World.getBlockId(par2, par3-1, par4)==this.blockID)
		{		
			
			
			if(dimHelper.instance.getLinkDataFromCoords(par2, par3, par4, par1World)==null)
			{
			
		
				
					LinkData link = new LinkData(par1World.provider.dimensionId, 0, par2, par3, par4, par2, par3, par4, true,par1World.getBlockMetadata(par2, par3-1, par4));
					dimHelper.instance.createPocket(link,true, false);
					//	System.out.println(link.linkOrientation);

				
				
			}
			

			
			if(dimHelper.instance.getLinkDataFromCoords(par2, par3, par4, par1World)!=null)
			{
				dimHelper.instance.getLinkDataFromCoords(par2, par3, par4, par1World).linkOrientation=par1World.getBlockMetadata(par2, par3-1, par4);
				dimHelper.instance.getLinkDataFromCoords(par2, par3, par4, par1World).hasGennedDoor=false;

			}
		}
		
	}
	
	@SideOnly(Side.CLIENT)

    /**
     * Retrieves the block texture to use based on the display side. Args: iBlockAccess, x, y, z, side
     */
    public Icon getBlockTexture(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
       if(par1IBlockAccess.getBlockId(par2, par3-1, par4)==this.blockID)
       {
    	   return this.blockIcon;
       }
       else
       {
    	   return this.blockIconBottom;
       }
    }

	//Called to update the render information on the tile entity. Could probably implement a data watcher, but this works fine and is more versatile I think. 
	public dimDoor updateAttatchedTile(IBlockAccess par1World, int par2, int par3, int par4)
	{
		TileEntity tile = (TileEntity) par1World.getBlockTileEntity(par2, par3, par4);
		if(tile instanceof TileEntityDimDoor )
		{
			TileEntityDimDoor dimTile=(TileEntityDimDoor)tile;
			
			if(par1World.getBlockId( par2, par3+1, par4 )==par1World.getBlockId( par2, par3, par4 ))
			{
				//dimTile.openOrClosed=false;
			}
			if(dimHelper.instance.getLinkDataFromCoords(par2, par3, par4, (World)par1World)==null)
			{
				dimTile.openOrClosed=false;
			}
			else
			{
				dimTile.openOrClosed=true;
			}
			
			int metaData = this.getFullMetadata(par1World, par2, par3, par4)%8;
			dimTile.orientation=metaData;
		}
    	return this;
    	
	}
	
    /**
     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random) 
    {
    		
    	TileEntityDimDoor tile = (TileEntityDimDoor) par1World.getBlockTileEntity(par2, par3, par4);
    	tile.openOrClosed=this.isDoorOpen( par1World,  par2,  par3,  par4);  	
    	int metaData = this.getFullMetadata(par1World, par2, par3, par4);
    	tile.orientation=metaData%8 ;
    		
    		

    	
    	
    	
    }
    public boolean isOpaqueCube()
    {
        return false;
    }

    public boolean getBlocksMovement(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        int var5 = this.getFullMetadata(par1IBlockAccess, par2, par3, par4);
        return (var5 & 4) != 0;
    }

    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    /**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return 7;
    }

    @SideOnly(Side.CLIENT)

    /**
     * Returns the bounding box of the wired rectangular prism to render.
     */
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
    {
        this.setBlockBoundsBasedOnState(par1World, par2, par3, par4);
        return super.getSelectedBoundingBoxFromPool(par1World, par2, par3, par4);
    }

    /**
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
    {
        this.setBlockBoundsBasedOnState(par1World, par2, par3, par4);
        return super.getCollisionBoundingBoxFromPool(par1World, par2, par3, par4);
    }

    /**
     * Updates the blocks bounds based on its current state. Args: world, x, y, z
     */
    public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        this.setDoorRotation(this.getFullMetadata(par1IBlockAccess, par2, par3, par4));
    }

    /**
     * Returns 0, 1, 2 or 3 depending on where the hinge is.
     */
    public int getDoorOrientation(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        return this.getFullMetadata(par1IBlockAccess, par2, par3, par4) & 3;
    }

    public boolean isDoorOpen(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        return (this.getFullMetadata(par1IBlockAccess, par2, par3, par4) & 4) != 0;
    }

    private void setDoorRotation(int par1)
    {
        float var2 = 0.1875F;
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F);
        int var3 = par1 & 3;
        boolean var4 = (par1 & 4) != 0;
        boolean var5 = (par1 & 16) != 0;

        if (var3 == 0)
        {
            if (var4)
            {
                if (!var5)
                {
                    this.setBlockBounds(0.001F, 0.0F, 0.0F, 1.0F, 1.0F, var2);
                }
                else
                {
                    this.setBlockBounds(0.001F, 0.0F, 1.0F - var2, 1.0F, 1.0F, 1.0F);
                }
            }
            else
            {
                this.setBlockBounds(0.0F, 0.0F, 0.0F, var2, 1.0F, 1.0F);
            }
        }
        else if (var3 == 1)
        {
            if (var4)
            {
                if (!var5)
                {
                    this.setBlockBounds(1.0F - var2, 0.0F, 0.001F, 1.0F, 1.0F, 1.0F);
                }
                else
                {
                    this.setBlockBounds(0.0F, 0.0F, 0.001F, var2, 1.0F, 1.0F);
                }
            }
            else
            {
                this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, var2);
            }
        }
        else if (var3 == 2)
        {
            if (var4)
            {
                if (!var5)
                {
                    this.setBlockBounds(0.0F, 0.0F, 1.0F - var2, .99F, 1.0F, 1.0F);
                }
                else
                {
                    this.setBlockBounds(0.0F, 0.0F, 0.0F, .99F, 1.0F, var2);
                }
            }
            else
            {
                this.setBlockBounds(1.0F - var2, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
            }
        }
        else if (var3 == 3)
        {
            if (var4)
            {
                if (!var5)
                {
                    this.setBlockBounds(0.0F, 0.0F, 0.0F, var2, 1.0F, 0.99F);
                }
                else
                {
                    this.setBlockBounds(1.0F - var2, 0.0F, 0.0F, 1.0F, 1.0F, 0.99F);
                }
            }
            else
            {
                this.setBlockBounds(0.0F, 0.0F, 1.0F - var2, 1.0F, 1.0F, 1.0F);
            }
        }
    }

    /**
     * Called when the block is clicked by a player. Args: x, y, z, entityPlayer
     */
    public void onBlockClicked(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer)
    {
    //	System.out.println(this.getFullMetadata(par1World, par2, par3, par4)%4);
    }

    /**
     * Called upon block activation (right click on the block.)
     */
    

    /**
     * A function to open a door.
     */
    
    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor blockID
     */
    public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, int par5)
    {
        int var6 = par1World.getBlockMetadata(par2, par3, par4);

        if ((var6 & 8) == 0)
        {
            boolean var7 = false;

            if (par1World.getBlockId(par2, par3 + 1, par4) != this.blockID)
            {
                par1World.setBlock(par2, par3, par4, 0);
                var7 = true;
            }

            /**
            if (!par1World.doesBlockHaveSolidTopSurface(par2, par3 - 1, par4))
            {
                par1World.setBlockWithNotify(par2, par3, par4, 0);
                var7 = true;

                if (par1World.getBlockId(par2, par3 + 1, par4) == this.blockID)
                {
                    par1World.setBlockWithNotify(par2, par3 + 1, par4, 0);
                }
            }
            **/

            if (var7)
            {
                if (!par1World.isRemote)
                {
                    this.dropBlockAsItem(par1World, par2, par3, par4, properties.DimensionalDoorID, 0);
                }
            }
            else
            {
                boolean var8 = par1World.isBlockIndirectlyGettingPowered(par2, par3, par4) || par1World.isBlockIndirectlyGettingPowered(par2, par3 + 1, par4);

                if ((var8 || par5 > 0 && Block.blocksList[par5].canProvidePower()) && par5 != this.blockID)
                {
                    this.onPoweredBlockChange(par1World, par2, par3, par4, var8);
                }
            }
        }
        else
        {
            if (par1World.getBlockId(par2, par3 - 1, par4) != this.blockID)
            {
                par1World.setBlock(par2, par3, par4, 0);
            }

            if (par5 > 0 && par5 != this.blockID)
            {
                this.onNeighborBlockChange(par1World, par2, par3 - 1, par4, par5);
            }
        }
    }

    /**
     * Returns the ID of the items to drop on destruction.
     */
   

    /**
     * Ray traces through the blocks collision from start vector to end vector returning a ray trace hit. Args: world,
     * x, y, z, startVec, endVec
     */
    public MovingObjectPosition collisionRayTrace(World par1World, int par2, int par3, int par4, Vec3 par5Vec3, Vec3 par6Vec3)
    {
        this.setBlockBoundsBasedOnState(par1World, par2, par3, par4);
        return super.collisionRayTrace(par1World, par2, par3, par4, par5Vec3, par6Vec3);
    }

    /**
     * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
     */
    public boolean canPlaceBlockAt(World par1World, int par2, int par3, int par4)
    {
        return par3 >= 255 ? false : par1World.doesBlockHaveSolidTopSurface(par2, par3 - 1, par4) && super.canPlaceBlockAt(par1World, par2, par3, par4) && super.canPlaceBlockAt(par1World, par2, par3 + 1, par4);
    }

    /**
     * Returns the mobility information of the block, 0 = free, 1 = can't push but can move over, 2 = total immobility
     * and stop pistons
     */
    public int getMobilityFlag()
    {
        return 2;
    }

    /**
     * Returns the full metadata value created by combining the metadata of both blocks the door takes up.
     */
    public int getFullMetadata(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        int var5 = par1IBlockAccess.getBlockMetadata(par2, par3, par4);
        boolean var6 = (var5 & 8) != 0;
        int var7;
        int var8;

        if (var6)
        {
            var7 = par1IBlockAccess.getBlockMetadata(par2, par3 - 1, par4);
            var8 = var5;
        }
        else
        {
            var7 = var5;
            var8 = par1IBlockAccess.getBlockMetadata(par2, par3 + 1, par4);
        }

        boolean var9 = (var8 & 1) != 0;
        return var7 & 7 | (var6 ? 8 : 0) | (var9 ? 16 : 0);
    }

    @SideOnly(Side.CLIENT)

    /**
     * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
     */
    public int idPicked(World par1World, int par2, int par3, int par4)
    {
        return Item.doorIron.itemID;
    }
    
    public int idDropped(int par1, Random par2Random, int par3)
    {
        return (par1 & 8) != 0 ? 0 : (Item.doorIron.itemID);
    }

    /**
     * Called when the block is attempted to be harvested
     */
    public void onBlockHarvested(World par1World, int par2, int par3, int par4, int par5, EntityPlayer par6EntityPlayer)
    {
        if (par6EntityPlayer.capabilities.isCreativeMode && (par5 & 8) != 0 && par1World.getBlockId(par2, par3 - 1, par4) == this.blockID)
        {
            par1World.setBlock(par2, par3 - 1, par4, 0);
        }
    }

	
	
	 public TileEntity createNewTileEntity(World par1World)
	    {
		 
		 TileEntity tile= new TileEntityDimDoor();
		 
	     return tile;
	    }
}