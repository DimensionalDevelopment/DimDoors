package StevenDimDoors.mod_pocketDim.schematic;

public class SchematicFilter {

	private String name;
	
	protected SchematicFilter(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}
	
	public boolean apply(Schematic schematic, short[] blocks, byte[] metadata)
	{
		if (!initialize(schematic, blocks, metadata))
			return false;
		
		for (int index = 0; index < blocks.length; index++)
		{
			if (applyToBlock(index, blocks, metadata) && terminates())
				return false;
		}
		
		return finish();
	}
	
	protected boolean initialize(Schematic schematic, short[] blocks, byte[] metadata)
	{
		return true;
	}
	
	protected boolean applyToBlock(int index, short[] blocks, byte[] metadata)
	{
		return true;
	}
	
	protected boolean finish()
	{
		return true;
	}
	
	protected boolean terminates()
	{
		return true;
	}
	
	public String toString()
	{
		return name;
	}
}
