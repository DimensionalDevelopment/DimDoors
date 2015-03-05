package StevenDimDoors.mod_pocketDim.schematic;


import net.minecraft.block.Block;

public class ReplacementFilter extends SchematicFilter {

	private Block targetBlock;
	private byte targetMetadata;
	private boolean matchMetadata;
	private Block replacementBlock;
	private byte replacementMetadata;
	private boolean changeMetadata;
	
	public ReplacementFilter(Block targetBlock, byte targetMetadata, Block replacementBlock, byte replacementMetadata)
	{
		super("ReplacementFilter");
		this.targetBlock = targetBlock;
		this.targetMetadata = targetMetadata;
		this.matchMetadata = true;
		this.replacementBlock = replacementBlock;
		this.replacementMetadata = replacementMetadata;
		this.changeMetadata = true;
	}
	
	public ReplacementFilter(Block targetBlock, Block replacementBlock, byte replacementMetadata)
	{
		super("ReplacementFilter");
		this.targetBlock = targetBlock;
		this.matchMetadata = false;
		this.replacementBlock = replacementBlock;
		this.replacementMetadata = replacementMetadata;
		this.changeMetadata = true;
	}
	
	public ReplacementFilter(Block targetBlock, byte targetMetadata, Block replacementBlock)
	{
		super("ReplacementFilter");
		this.targetBlock = targetBlock;
		this.targetMetadata = targetMetadata;
		this.matchMetadata = true;
		this.replacementBlock = replacementBlock;
		this.changeMetadata = false;
	}
	
	public ReplacementFilter(Block targetBlock, Block replacementBlock)
	{
		super("ReplacementFilter");
		this.targetBlock = targetBlock;
		this.matchMetadata = false;
		this.replacementBlock = replacementBlock;
		this.changeMetadata = false;
	}

	@Override
	protected boolean applyToBlock(int index, Block[] blocks, byte[] metadata)
	{
		if (blocks[index] == targetBlock)
		{
			if ((matchMetadata && metadata[index] == targetMetadata) || !matchMetadata)
			{
				blocks[index] = replacementBlock;
				if (changeMetadata)
				{
					metadata[index] = replacementMetadata;
				}
				return true;
			}
		}
		return false;
	}
	
	@Override
	protected boolean terminates()
	{
		return false;
	}
}
