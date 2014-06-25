package StevenDimDoors.mod_pocketDim.core;

public enum DimensionType
{
	// WARNING: Don't modify these values carelessly or you'll risk breaking existing worlds!
	ROOT(0,false),
	POCKET(1,true),
	DUNGEON(2,true),
	PERSONAL(3,true);
	
	DimensionType(int index, boolean isPocket)
	{
		this.index = index;
		this.isPocket = isPocket;
	}
	
	public final int index;
	public final boolean isPocket;
	
	/**
	 * Get the DimensionType given an index. I feel like there should be a better way to do this. 
	 * @param index
	 * @return
	 */
	public static DimensionType getTypeFromIndex(int index)
	{
		for(DimensionType type : DimensionType.values())
		{
			if(type.index == index)
			{
				return type;
			}
		}
		return null;
	}
	
	public boolean isPocketDimension()
	{
		return this.isPocket;
	}
}
