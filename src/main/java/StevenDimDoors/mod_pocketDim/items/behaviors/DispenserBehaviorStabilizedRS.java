package StevenDimDoors.mod_pocketDim.items.behaviors;

import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import StevenDimDoors.mod_pocketDim.items.ItemStabilizedRiftSignature;

public class DispenserBehaviorStabilizedRS extends BehaviorDefaultDispenseItem
{
    @Override
	public ItemStack dispenseStack(IBlockSource dispenser, ItemStack stack)
    {
    	// Search for a non-air block up to 3 blocks in front of a dispenser.
    	// If it's found, call ItemStabilizedRiftSignature.useFromDispenser().
        int x = dispenser.getXInt();
        int y = dispenser.getYInt();
        int z = dispenser.getZInt();
        EnumFacing facing = BlockDispenser.func_149937_b(dispenser.getBlockMetadata());
        int dx = facing.getFrontOffsetX();
        int dy = facing.getFrontOffsetY();
        int dz = facing.getFrontOffsetZ();
        World world = dispenser.getWorld();
        
        for (int k = 1; k <= 3; k++)
        {
        	x += dx;
        	y += dy;
        	z += dz;
        	if (!world.isAirBlock(x, y, z))
        	{
        		// Found a block. Activate the item.
        		ItemStabilizedRiftSignature.useFromDispenser(stack, world, x, y, z);
        		break;
        	}
        }
        // The item stack isn't modified
        return stack;
    }
}
