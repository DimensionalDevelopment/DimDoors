package com.zixiken.dimdoors.blocks;

import java.util.List;
import java.util.Random;

import com.zixiken.dimdoors.DimDoors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import com.zixiken.dimdoors.client.PrivatePocketRender;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockDimWall extends Block {
    public static final String ID = "blockDimWall";
    public static final PropertyInteger TYPE = PropertyInteger.create("type", 0, 2);

	private static final float SUPER_HIGH_HARDNESS = 10000000000000F;
	private static final float SUPER_EXPLOSION_RESISTANCE = 18000000F;
	
	public BlockDimWall() {
		super(Material.iron);
		this.setCreativeTab(DimDoors.dimDoorsCreativeTab);
		setLightLevel(1.0F);
        setHardness(0.1F);
        setUnlocalizedName(ID);
        setDefaultState(blockState.getBaseState().withProperty(TYPE, 0));
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
        if(meta >= 0 && meta <= 2) return getDefaultState().withProperty(TYPE, meta);
        else return getDefaultState();
    }

	@Override
	public int getMetaFromState(IBlockState state) {return state.getValue(TYPE);}

	@Override
	protected BlockState createBlockState() {return new BlockState(this, TYPE);}

	@Override
	public float getBlockHardness(World world, int x, int y, int z) {
		if (world.getBlockMetadata(x, y, z) != 1) return this.blockHardness;
		else return SUPER_HIGH_HARDNESS;
	}
	
	@Override
    public float getExplosionResistance(Entity entity, World world, int x, int y, int z, double explosionX, double explosionY, double explosionZ) {
		if (world.getBlockMetadata(x, y, z) != 1)
			return super.getExplosionResistance(entity, world, x, y, z, explosionX, explosionY, explosionZ);
		else return SUPER_EXPLOSION_RESISTANCE;
    }
	
	public int getRenderType()
    {
        return PrivatePocketRender.renderID;
    }
	
	@Override
	public int damageDropped(IBlockState state) {
        int metadata = state.getValue(TYPE);
		//Return 0 to avoid dropping Ancient Fabric even if the player somehow manages to break it
		return metadata == 1 ? 0 : metadata;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
		for (int ix = 0; ix < 3; ix++)
			subItems.add(new ItemStack(itemIn, 1, ix));
	}
    
    @Override
	public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player)
    {
        return true;
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos pos, EntityPlayer player) {
        return new ItemStack(Item.getItemFromBlock(this), 1, getMetaFromState(world.getBlockState(pos)));
    }

    @Override
	public int quantityDropped(Random par1Random)
    {
        return 0;
    }
   
    /**
     * replaces the block clicked with the held block, instead of placing the block on top of it. Shift click to disable. 
     */
    @Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int par6, float par7, float par8, float par9) {
    	//Check if the metadata value is 0 -- we don't want the user to replace Ancient Fabric
        if (entityPlayer.getCurrentEquippedItem() != null && world.getBlockMetadata(x, y, z) != 1)
        {
        	Item playerEquip = entityPlayer.getCurrentEquippedItem().getItem();
        	
        	if (playerEquip instanceof ItemBlock)
        	{
        		// SenseiKiwi: Using getBlockID() rather than the raw itemID is critical.
        		// Some mods may override that function and use item IDs outside the range
        		// of the block list.

                ItemBlock playerEquipItemBlock = (ItemBlock)playerEquip;
        		Block block = playerEquipItemBlock.field_150939_a;
        		if (!block.isNormalCube(world, x, y, z) || block instanceof BlockContainer || block == this)
        		{
        			return false;
        		}
        		if (!world.isRemote)
        		{
            		if (!entityPlayer.capabilities.isCreativeMode)
            		{
            			entityPlayer.getCurrentEquippedItem().stackSize--;
            		}
            		world.setBlock(x, y, z, block, playerEquipItemBlock.getMetadata(entityPlayer.getCurrentEquippedItem().getItemDamage()), 0);
        		}
        		return true;
        	}
        }
        return false;
    }
}
