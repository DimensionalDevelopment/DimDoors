package StevenDimDoors.mod_pocketDim.dungeon;

import net.minecraft.block.Block;
import StevenDimDoors.mod_pocketDim.schematic.SchematicFilter;

public class ModBlockFilter extends SchematicFilter {

	private short maxVanillaBlockID;
	private short[] exceptions;
	private short replacementBlockID;
	private byte replacementMetadata;
	
	public ModBlockFilter(short maxVanillaBlockID, short[] exceptions, short replacementBlockID, byte replacementMetadata)
	{
		super("ModBlockFilter");
		this.maxVanillaBlockID = maxVanillaBlockID;
		this.exceptions = exceptions;
		this.replacementBlockID = replacementBlockID;
		this.replacementMetadata = replacementMetadata;
	}
	
	@Override
	protected boolean applyToBlock(int index, short[] blocks, byte[] metadata)
	{
		int k;
		short currentID = blocks[index];
		if (currentID > maxVanillaBlockID || (currentID != 0 && Block.blocksList[currentID] == null))
		{
			//This might be a mod block. Check if an exception exists.
			for (k = 0; k < exceptions.length; k++)
			{
				if (currentID == exceptions[k])
				{
					//Exception found, not considered a mod block
					return false;
				}
			}
			//No matching exception found. Replace the block.
			blocks[index] = replacementBlockID;
			metadata[index] = replacementMetadata;
			return true;
		}
		return false;
	}
	
	@Override
	protected boolean terminates()
	{
		return false;
	}
}
