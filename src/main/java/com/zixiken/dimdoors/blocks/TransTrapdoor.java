package com.zixiken.dimdoors.blocks;

import java.util.Random;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.core.*;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
	public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
		this.placeLink(world, pos);
		world.setTileEntity(pos, createNewTileEntity(world, getMetaFromState(state)));
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {return new TileEntityTransTrapdoor();}

	@Override
	public void placeLink(World world, BlockPos pos) {
		if (!world.isRemote) {
			NewDimData dimension = PocketManager.createDimensionData(world);
			DimLink link = dimension.getLink(pos);
			if (link == null && dimension.isPocketDimension())
                dimension.createLink(pos, LinkType.UNSAFE_EXIT, EnumFacing.EAST);
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos pos, EntityPlayer player) {
		return new ItemStack(this.getDoorItem(), 1, 0);
	}
	
	@Override
	public Item getItemDropped(IBlockState state, Random random, int fortuneLevel) {
        return Item.getItemFromBlock(Blocks.trapdoor);
    }
	
	@Override
	public Item getDoorItem() {return Item.getItemFromBlock(DimDoors.transTrapdoor);}
	
	public static boolean isTrapdoorSetLow(IBlockState state) {
        return state.getValue(BlockTrapDoor.HALF) == DoorHalf.BOTTOM;
    }
	
	@Override
	public TileEntity initDoorTE(World world, BlockPos pos) {
		TileEntity te = createNewTileEntity(world, getMetaFromState(world.getBlockState(pos)));
		world.setTileEntity(pos, te);
		return te;
	}

	@Override
	public boolean isDoorOnRift(World world, BlockPos pos) {return PocketManager.getLink(pos, world) != null;}
	
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