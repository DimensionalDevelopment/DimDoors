package StevenDimDoors.mod_pocketDim.schematic;


public class ReplacementFilter extends SchematicFilter {

	private short targetBlock;
	private byte targetMetadata;
	private boolean matchMetadata;
	private short replacementBlock;
	private byte replacementMetadata;
	private boolean changeMetadata;
	
	public ReplacementFilter(short targetBlock, byte targetMetadata, short replacementBlock, byte replacementMetadata)
	{
		super("ReplacementFilter");
		this.targetBlock = targetBlock;
		this.targetMetadata = targetMetadata;
		this.matchMetadata = true;
		this.replacementBlock = replacementBlock;
		this.replacementMetadata = replacementMetadata;
		this.changeMetadata = true;
	}
	
	public ReplacementFilter(short targetBlock, short replacementBlock, byte replacementMetadata)
	{
		super("ReplacementFilter");
		this.targetBlock = targetBlock;
		this.matchMetadata = false;
		this.replacementBlock = replacementBlock;
		this.replacementMetadata = replacementMetadata;
		this.changeMetadata = true;
	}
	
	public ReplacementFilter(short targetBlock, byte targetMetadata, short replacementBlock)
	{
		super("ReplacementFilter");
		this.targetBlock = targetBlock;
		this.targetMetadata = targetMetadata;
		this.matchMetadata = true;
		this.replacementBlock = replacementBlock;
		this.changeMetadata = false;
	}
	
	public ReplacementFilter(short targetBlock, short replacementBlock)
	{
		super("ReplacementFilter");
		this.targetBlock = targetBlock;
		this.matchMetadata = false;
		this.replacementBlock = replacementBlock;
		this.changeMetadata = false;
	}

	@Override
	protected boolean applyToBlock(int index, short[] blocks, byte[] metadata)
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
