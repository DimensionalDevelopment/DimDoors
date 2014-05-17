package StevenDimDoors.mod_pocketDim.tileentities;
import java.util.Random;
import net.minecraft.tileentity.TileEntity;

public abstract class DDTileEntityBase extends TileEntity
{
	/**
	 * 
	 * @return an array of floats representing RGBA color where 1.0 = 255.
	 */
	public abstract float[] getRenderColor(Random rand);
	
}
