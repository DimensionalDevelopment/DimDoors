package com.zixiken.dimdoors.blocks;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.core.DimLink;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import com.zixiken.dimdoors.core.DDTeleporter;
import com.zixiken.dimdoors.core.LinkType;
import com.zixiken.dimdoors.core.NewDimData;
import com.zixiken.dimdoors.core.PocketManager;

public class TransientDoor extends BaseDimDoor {
	public static final String ID = "transientDoor";

	public TransientDoor() {
		super(Material.iron);
		setHardness(1.0F);
		setUnlocalizedName(ID);
	}

	@Override
	public void enterDimDoor(World world, BlockPos pos, Entity entity) {
		// Check that this is the top block of the door
		IBlockState state = world.getBlockState(pos.down());
		if (!world.isRemote && state.getBlock() == this) {
			if (entity instanceof EntityPlayer && isEntityFacingDoor(state, (EntityLivingBase) entity)) {
				// Teleport the entity through the link, if it exists
				DimLink link = PocketManager.getLink(pos, world.provider.getDimensionId());
				if (link != null && link.linkType() != LinkType.PERSONAL) {
                    DDTeleporter.traverseDimDoor(world, link, entity, this);
                    // Turn the door into a rift AFTER teleporting the player.
                    // The door's orientation may be necessary for the teleport.
                    world.setBlockState(pos, DimDoors.blockRift.getDefaultState());
                    world.setBlockToAir(pos.down());
				}
			}
		}
		else {
            BlockPos up = pos.up();
            if (world.getBlockState(up).getBlock() == this) enterDimDoor(world, up, entity);
        }
	}	

	@Override
	public void placeLink(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos.down());
		if (!world.isRemote && state.getBlock() == this) {
			NewDimData dimension = PocketManager.createDimensionData(world);
			DimLink link = dimension.getLink(pos);
			if (link == null && dimension.isPocketDimension()) {
				dimension.createLink(pos, LinkType.SAFE_EXIT, state.getValue(BlockDoor.FACING));
			}
		}
	}
	
	@Override
	public Item getDoorItem() {return null;}

	@Override
	public boolean isCollidable() {return false;}

    //The old textures are transparent. We should get the same effect with no render,
    //though setting opaque to false might be necessary for ao/culling purposes
	@Override
	public int getRenderType() {return -1;}
	
}