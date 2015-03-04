package StevenDimDoors.experimental;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.world.World;
import StevenDimDoors.mod_pocketDim.DDLoot;
import StevenDimDoors.mod_pocketDim.Point3D;
import StevenDimDoors.mod_pocketDim.config.DDProperties;
import StevenDimDoors.mod_pocketDim.schematic.WorldOperation;

/**
 * Provides an operation for damaging structures based on a spherical area. The chance of damage decreases
 * with the square of the distance from the center of the sphere.
 * @author SenseiKiwi
 *
 */
public class SphereDecayOperation extends WorldOperation
{
	private Random random;
	private double scaling;
	private double centerX;
	private double centerY;
	private double centerZ;
	private Block primaryBlock;
	private int primaryMetadata;
	private Block secondaryBlock;
	private int secondaryMetadata;
	
	public SphereDecayOperation(Random random, Block primaryBlock, int primaryMetadata, Block secondaryBlock, int secondaryMetadata)
	{
		super("SphereDecayOperation");
		this.random = random;
		this.primaryBlock = primaryBlock;
		this.primaryMetadata = primaryMetadata;
		this.secondaryBlock = secondaryBlock;
		this.secondaryMetadata = secondaryMetadata;
	}
	
	@Override
	protected boolean initialize(World world, int x, int y, int z, int width, int height, int length)
	{
		// Calculate a scaling factor so that the probability of decay
		// at the edge of the largest dimension of our bounds is 20%.
		scaling = Math.max(width - 1, Math.max(height - 1, length - 1)) / 2.0;
		scaling *= scaling * 0.20;
		
		centerX = x + width / 2.0;
		centerY = y + height / 2.0;
		centerZ = z + length / 2.0;
		return true;
	}

	@Override
	protected boolean applyToBlock(World world, int x, int y, int z)
	{
		// Don't raise any notifications. This operation is only designed to run
		// when a dimension is being generated, which means there are no players around.
		if (!world.isAirBlock(x, y, z))
		{
			double dx = (centerX - x - 0.5);
			double dy = (centerY - y - 0.5); 
			double dz = (centerZ - z - 0.5);
			double squareDistance = dx * dx + dy * dy + dz * dz;
			
			if (squareDistance < 0.5 || random.nextDouble() < scaling / squareDistance)
			{
				world.setBlock(x, y, z, primaryBlock, primaryMetadata, 1);
			}
			else if (random.nextDouble() < scaling / squareDistance)
			{
				world.setBlock(x, y, z, secondaryBlock, secondaryMetadata, 1);
			}
		}
		return true;
	}
}
