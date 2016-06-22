package com.zixiken.dimdoors.blocks;

import java.util.Random;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.core.DimLink;
import com.zixiken.dimdoors.core.DDTeleporter;
import com.zixiken.dimdoors.core.LinkType;
import com.zixiken.dimdoors.core.PocketManager;
import com.zixiken.dimdoors.items.ItemDDKey;
import com.zixiken.dimdoors.tileentities.TileEntityDimDoor;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class BaseDimDoor extends BlockDoor implements IDimDoor, ITileEntityProvider {
	
	public BaseDimDoor(Material material)
	{
		super(material);
	}
    
	@Override
	public void onEntityCollidedWithBlock(World world, BlockPos pos, Entity entity) {
		enterDimDoor(world, pos, entity);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
									EnumFacing side, float hitX, float hitY, float hitZ) {
		
		ItemStack stack = player.inventory.getCurrentItem();
		if (stack != null && stack.getItem() instanceof ItemDDKey) {return false;}

		if(!checkCanOpen(world, pos, player)) {return false;}

        if(state.getValue(BlockDoor.HALF) == EnumDoorHalf.UPPER) {
            pos = pos.down();
            state = world.getBlockState(pos);
        }

        if(state.getBlock() != this) return false;
        else {
            state = state.cycleProperty(BlockDoor.OPEN);
            world.setBlockState(pos, state, 2);
            world.markBlockRangeForRenderUpdate(pos, pos.up());
            world.playAuxSFXAtEntity(player, state.getValue(BlockDoor.OPEN) ? 1003 : 1006, pos, 0);
            return true;
        }
	}

	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
		placeLink(world, pos);
		world.setTileEntity(pos, createNewTileEntity(world, 0));
		updateAttachedTile(world, pos);
	}

	//Called to update the render information on the tile entity. Could probably implement a data watcher,
	//but this works fine and is more versatile I think. 
	public BaseDimDoor updateAttachedTile(World world, BlockPos pos) {
		DimDoors.proxy.updateDoorTE(this, world, pos);
		return this;
	}
	
	public boolean isDoorOnRift(World world, BlockPos pos)
	{
		return this.getLink(world, pos) != null;
	}
	
	public DimLink getLink(World world, BlockPos pos)
	{
		DimLink link= PocketManager.getLink(pos, world.provider.getDimensionId());
		if(link!=null)
		{
			return link;
		}
		
		if(isUpperDoorBlock(world.getBlockState(pos)))
		{
			link = PocketManager.getLink(pos.down(), world.provider.getDimensionId());
			if(link!=null)
			{
				return link;
			}
		}
		else
		{
			link = PocketManager.getLink(pos.up(), world.provider.getDimensionId());
			if(link != null)
			{
				return link;
			}
		}
		return null;
	}

	@Override
	public void updateTick(World par1World, BlockPos pos, IBlockState state, Random rand) {
		updateAttachedTile(par1World, pos);
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos)
	{
		this.setDoorRotation(worldIn.getBlockState(pos));
	}


	private void setDoorRotation(IBlockState state)
	{
		float var2 = 0.1875F;
		setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F);
		int var3 = state.getValue(BlockDoor.FACING).rotateY().getHorizontalIndex();
		boolean var4 = state.getValue(BlockDoor.OPEN);
		boolean var5 = state.getValue(BlockDoor.HINGE) == EnumHingePosition.LEFT;

		if (var3 == 0)
		{
			if (var4)
			{
				if (!var5)
				{
					setBlockBounds(0.001F, 0.0F, 0.0F, 1.0F, 1.0F, var2);
				}
				else
				{
					setBlockBounds(0.001F, 0.0F, 1.0F - var2, 1.0F, 1.0F, 1.0F);
				}
			}
			else
			{
				setBlockBounds(0.0F, 0.0F, 0.0F, var2, 1.0F, 1.0F);
			}
		}
		else if (var3 == 1)
		{
			if (var4)
			{
				if (!var5)
				{
					setBlockBounds(1.0F - var2, 0.0F, 0.001F, 1.0F, 1.0F, 1.0F);
				}
				else
				{
					setBlockBounds(0.0F, 0.0F, 0.001F, var2, 1.0F, 1.0F);
				}
			}
			else
			{
				setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, var2);
			}
		}
		else if (var3 == 2)
		{
			if (var4)
			{
				if (!var5)
				{
					setBlockBounds(0.0F, 0.0F, 1.0F - var2, .99F, 1.0F, 1.0F);
				}
				else
				{
					setBlockBounds(0.0F, 0.0F, 0.0F, .99F, 1.0F, var2);
				}
			}
			else
			{
				setBlockBounds(1.0F - var2, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
			}
		}
		else if (var3 == 3)
		{
			if (var4)
			{
				if (!var5)
				{
					setBlockBounds(0.0F, 0.0F, 0.0F, var2, 1.0F, 0.99F);
				}
				else
				{
					setBlockBounds(1.0F - var2, 0.0F, 0.0F, 1.0F, 1.0F, 0.99F);
				}
			}
			else
			{
				setBlockBounds(0.0F, 0.0F, 1.0F - var2, 1.0F, 1.0F, 1.0F);
			}
		}
	}


	/**
	 * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
	 * their own) Args: x, y, z, neighbor blockID
	 */
	@Override
	public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighbor) {

		if (isUpperDoorBlock(state)) {
			if (world.getBlockState(pos.down()) != this)
                world.setBlockToAir(pos);
			if (!neighbor.isAir(world, pos) && neighbor != this)
                onNeighborBlockChange(world, pos.down(), state, neighbor);
		} else {
			if (world.getBlockState(pos.up()) != this) {
				world.setBlockToAir(pos);
				if (!world.isRemote) dropBlockAsItem(world, pos, state, 0);
			}
			else if(this.getLockStatus(world, pos)<=1) {
				boolean powered = world.isBlockPowered(pos) || world.isBlockPowered(pos.up());
				if ((powered || !neighbor.isAir(world, pos) && neighbor.canProvidePower()) && neighbor != this)
					toggleDoor(world, pos, powered);
			}
		}
	}

	/**
	 * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos pos, EntityPlayer player) {
		return new ItemStack(this.getDoorItem(), 1, 0);
	}

    /**
     * Returns the ID of the items to drop on destruction.
     */
    @Override
	public Item getItemDropped(IBlockState state, Random random, int fortune) {
        return isUpperDoorBlock(state) ? null : this.getDoorItem();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Item getItem(World world, BlockPos pos) {return this.getDoorItem();}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {return new TileEntityDimDoor();}

	@Override
	public void enterDimDoor(World world, BlockPos pos, Entity entity) {
		// FX entities dont exist on the server
		if (world.isRemote) {return;}
		
		// Check that this is the top block of the door
		if (world.getBlockState(pos).getValue(BlockDoor.HALF) == EnumDoorHalf.UPPER) {
			IBlockState state = world.getBlockState(pos.down());
			boolean canUse = state.getValue(BlockDoor.OPEN);
			if (canUse && entity instanceof EntityPlayer)
				// Dont check for non-player entites
				canUse = isEntityFacingDoor(state, (EntityLivingBase) entity);
			if (canUse) {
				// Teleport the entity through the link, if it exists
				DimLink link = PocketManager.getLink(pos, world.provider.getDimensionId());
				if (link != null && (link.linkType() != LinkType.PERSONAL || entity instanceof EntityPlayer)) {
					try {
						DDTeleporter.traverseDimDoor(world, link, entity, this);
					} catch (IllegalArgumentException e) {
						System.err.println("Something went wrong teleporting to a dimension:");
						e.printStackTrace();
					}
				}
				
				// Close the door only after the entity goes through
				// so players don't have it slam in their faces.
				this.toggleDoor(world, pos, false);
			}
		} else {
            BlockPos up = pos.up();
            if (world.getBlockState(up).getBlock() == this) enterDimDoor(world, up, entity);
        }
	}
	
	public boolean isUpperDoorBlock(IBlockState state) {return state.getValue(BlockDoor.HALF) == EnumDoorHalf.UPPER;}
	
	/**
	 * 0 if link is no lock;
	 * 1 if there is a lock;
	 * 2 if the lock is locked.
	 * @param world
	 * @param pos
	 * @return
	 */
	public byte getLockStatus(World world, BlockPos pos) {
		byte status = 0;
		DimLink link = getLink(world, pos);
		if(link != null && link.hasLock()) {
			status++;
			if(link.getLockState()) status++;
		}
		return status;
	}
	
	public boolean checkCanOpen(World world, BlockPos pos, EntityPlayer player) {
		DimLink link = getLink(world, pos);
		if(link == null || player == null) return link == null;
		if(!link.getLockState()) return true;
		
		for(ItemStack item : player.inventory.mainInventory)
			if(item != null && item.getItem() instanceof ItemDDKey && link.tryToOpen(item)) return true;

		player.playSound(DimDoors.MODID + ":doorLocked",  1F, 1F);
		return false;
	}

	    
	protected static boolean isEntityFacingDoor(IBlockState state, EntityLivingBase entity) {
		// Although any entity has the proper fields for this check,
		// we should only apply it to living entities since things
		// like Minecarts might come in backwards.
		return (state.getValue(BlockDoor.FACING) == EnumFacing.fromAngle(entity.rotationYaw));
	}
	
	@Override
	public TileEntity initDoorTE(World world, BlockPos pos) {
		TileEntity te = createTileEntity(world, world.getBlockState(pos));
		world.setTileEntity(pos, te);
		return te;
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		// This function runs on the server side after a block is replaced
		// We MUST call super.breakBlock() since it involves removing tile entities
        super.breakBlock(world, pos, state);
        
        // Schedule rift regeneration for this block if it was replaced
        if (world.getBlockState(pos).getBlock() != state.getBlock())
            DimDoors.riftRegenerator.scheduleFastRegeneration(pos, world);
    }
}