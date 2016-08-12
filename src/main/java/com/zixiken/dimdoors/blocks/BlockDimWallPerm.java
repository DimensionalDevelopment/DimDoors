package com.zixiken.dimdoors.blocks;

import java.util.Random;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.config.DDProperties;
import com.zixiken.dimdoors.core.DDTeleporter;
import com.zixiken.dimdoors.helpers.yCoordHelper;
import com.zixiken.dimdoors.util.Point4D;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public class BlockDimWallPerm extends Block {
    public static final String ID = "blockDimWallPerm";

	private static final Random random = new Random();
	private static DDProperties properties;
	
	public BlockDimWallPerm() {
		super(Material.ground);
        this.setCreativeTab(DimDoors.dimDoorsCreativeTab);
		setLightLevel(1.0F);
        setBlockUnbreakable();
        setResistance(6000000.0F);
        setUnlocalizedName(ID);
		properties = DDProperties.instance();
	}

	@Override
	public int quantityDropped(Random par1Random)
	{
		return 0;
	}

	/**
	 * Only matters if the player is in limbo, acts to teleport the player from limbo back to dim 0
	 */
	@Override
	public void onEntityCollidedWithBlock(World world, BlockPos pos, Entity entity) {
		if(!world.isRemote && world.provider.getDimensionId() == properties.LimboDimensionID
				&& DimDoors.worldProperties.LimboEscapeEnabled) {
			World overworld = DimensionManager.getWorld(0);
			if (overworld != null && entity instanceof EntityPlayerMP) {
				EntityPlayer player = (EntityPlayer) entity;
				player.fallDistance = 0;
				int rangeLimit = properties.LimboReturnRange / 2;
				int destinationX = pos.getX() + MathHelper.getRandomIntegerInRange(random, -rangeLimit, rangeLimit);
				int destinationZ = pos.getZ() + MathHelper.getRandomIntegerInRange(random, -rangeLimit, rangeLimit);

				//make sure I am in the middle of a chunk, and not on a boundary, so it doesn't load the chunk next to me
                destinationX += (destinationX >> 4);
                destinationZ += (destinationZ >> 4);

				int destinationY = yCoordHelper.getFirstUncovered(overworld, new BlockPos(destinationX, 63, destinationZ), true);

                BlockPos destPos = new BlockPos(destinationX, destinationY, destinationZ);
				
				//FIXME: Shouldn't we make the player's destination safe BEFORE teleporting him?!
				//player.setPositionAndUpdate( x, y, z );
				Point4D destination = new Point4D(destPos, 0);
				DDTeleporter.teleportEntity(player, destination, false);
				
				//player.setPositionAndUpdate( x, y, z );

				// Make absolutely sure the player doesn't spawn inside blocks, though to be honest this shouldn't ever have to be a problem...
				overworld.setBlockToAir(destPos);
				overworld.setBlockToAir(destPos.up());
				
				for (int xc = -3; xc < 4; xc++)
					for (int zc = -3; zc < 4; zc++)
						if (Math.abs(xc) + Math.abs(zc) < random.nextInt(3) + 2 ||
                                Math.abs(xc) + Math.abs(zc) < random.nextInt(3) + 3)
							overworld.setBlockState(destPos.add(xc, -1, zc), DimDoors.blockLimbo.getDefaultState());

				//FIXME: Why do we do this repeatedly? We also set the fall distance at the start...
				player.setPositionAndUpdate( destinationX, destinationY, destinationZ );
				player.fallDistance = 0;
			}
		}
	}
}
