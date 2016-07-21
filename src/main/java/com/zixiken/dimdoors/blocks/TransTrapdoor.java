package com.zixiken.dimdoors.blocks;

import java.util.Random;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.core.DDTeleporter;
import com.zixiken.dimdoors.core.DimLink;
import com.zixiken.dimdoors.core.LinkType;
import com.zixiken.dimdoors.core.PocketManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import com.zixiken.dimdoors.items.ItemDDKey;
import com.zixiken.dimdoors.tileentities.TileEntityTransTrapdoor;

public class TransTrapdoor extends BlockTrapDoor implements IDimDoor, ITileEntityProvider {
	public static final String ID = "dimHatch";

	public TransTrapdoor() {
		super(Material.wood);
		this.setCreativeTab(DimDoors.dimDoorsCreativeTab);
        setHardness(1.0F);
        setUnlocalizedName(ID);
	}

	//Teleports the player to the exit link of that dimension, assuming it is a pocket
	@Override
	public void onEntityCollidedWithBlock(World world, BlockPos pos, Entity entity) {enterDimDoor(world, pos, entity);}

	public boolean checkCanOpen(World world, BlockPos pos) {return this.checkCanOpen(world, pos, null);}
	
	public boolean checkCanOpen(World world, BlockPos pos, EntityPlayer player) {
		DimLink link = PocketManager.getLink(pos, world);
		if(link == null || player == null) return link == null;

		if(!link.getLockState()) return true;
		
		for(ItemStack item : player.inventory.mainInventory)
			if(item != null && item.getItem() instanceof ItemDDKey && link.tryToOpen(item)) return true;
		return false;
	}

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
        return checkCanOpen(worldIn, pos, playerIn) &&
                super.onBlockActivated(worldIn, pos, state, playerIn, side, hitX, hitY,  hitZ);
    }

    @Override
    public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
        if(checkCanOpen(worldIn, pos)) super.onNeighborBlockChange(worldIn, pos, state, neighborBlock);
    }

    @Override
	public void enterDimDoor(World world, BlockPos pos, Entity entity) {
        IBlockState state = world.getBlockState(pos);
		if (!world.isRemote && state.getValue(BlockTrapDoor.OPEN)) {
			DimLink link = PocketManager.getLink(pos, world);
			if (link != null && (link.linkType() != LinkType.PERSONAL || entity instanceof EntityPlayer)) {
                DDTeleporter.traverseDimDoor(world, link, entity, this);
                state.cycleProperty(BlockTrapDoor.OPEN);
                world.markBlockRangeForRenderUpdate(pos, pos);
                world.playAuxSFXAtEntity(null, 1006, pos, 0);
            }
		}
	}	

	@Override
	public void onBlockAdded(World world, int x, int y, int z) 
	{
		this.placeLink(world, x, y, z);
		world.setTileEntity(x, y, z, this.createNewTileEntity(world, world.getBlockMetadata(x, y, z)));
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int metadata)
	{
		return new TileEntityTransTrapdoor();
	}
	
	@Override
	public void placeLink(World world, int x, int y, int z) 
	{
		if (!world.isRemote)
		{
			NewDimData dimension = PocketManager.createDimensionData(world);
			DimLink link = dimension.getLink(x, y, z);
			if (link == null && dimension.isPocketDimension())
			{
				dimension.createLink(x, y, z, LinkType.UNSAFE_EXIT,0);
			}
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player)
	{
		return new ItemStack(this.getDoorItem(), 1, 0);
	}
	
	@Override
	public Item getItemDropped(int metadata, Random random, int fortuneLevel)
    {
        return Item.getItemFromBlock(Blocks.trapdoor);
    }
	
	@Override
	public Item getDoorItem()
	{
		return Item.getItemFromBlock(DimDoors.transTrapdoor);
	}
	
	public static boolean isTrapdoorSetLow(int metadata)
	{
		return (metadata & 8) == 0;
	}
	
	@Override
	public TileEntity initDoorTE(World world, int x, int y, int z)
	{
		TileEntity te = this.createNewTileEntity(world, world.getBlockMetadata(x, y, z));
		world.setTileEntity(x, y, z, te);
		return te;
	}

	@Override
	public boolean isDoorOnRift(World world, int x, int y, int z)
	{
		return PocketManager.getLink(x, y, z, world)!=null;
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, Block oldBlock, int oldMeta)
    {
		// This function runs on the server side after a block is replaced
		// We MUST call super.breakBlock() since it involves removing tile entities
        super.breakBlock(world, x, y, z, oldBlock, oldMeta);
        
        // Schedule rift regeneration for this block if it was replaced
        if (world.getBlock(x, y, z) != oldBlock)
        {
        	DimDoors.riftRegenerator.scheduleFastRegeneration(x, y, z, world);
        }
    }
}