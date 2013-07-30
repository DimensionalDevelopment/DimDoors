package StevenDimDoors.mod_pocketDim.schematic;

import java.util.ArrayList;

public class CompoundFilter extends SchematicFilter {

	private ArrayList<SchematicFilter> filters;
	
	public CompoundFilter()
	{
		super("CompoundFilter");
		filters = new ArrayList<SchematicFilter>();
	}
	
	public void addFilter(SchematicFilter filter)
	{
		filters.add(filter);
	}
	
	@Override
	protected boolean initialize(Schematic schematic, short[] blocks, byte[] metadata)
	{
		for (SchematicFilter filter : filters)
		{
			if (!filter.initialize(schematic, blocks, metadata))
			{
				return false;
			}
		}
		return !filters.isEmpty();
	}
	
	@Override
	protected boolean finish()
	{
		for (SchematicFilter filter : filters)
		{
			if (!filter.finish())
			{
				return false;
			}
		}
		return true;
	}
	
	@Override
	protected boolean applyToBlock(int index, short[] blocks, byte[] metadata)
	{
		for (SchematicFilter filter : filters)
		{
			if (filter.applyToBlock(index, blocks, metadata))
			{
				return filter.terminates();
			}
		}
		return false;
	}
}
