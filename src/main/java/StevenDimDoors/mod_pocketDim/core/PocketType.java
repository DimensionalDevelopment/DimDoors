package StevenDimDoors.mod_pocketDim.core;

public enum PocketType
{
	// WARNING: Don't modify these values carelessly or you'll risk breaking existing worlds!
	NORMAL(0),
	DUNGEON(1),
	PERSONAL(2);
	
	PocketType(int index)
	{
		this.index = index;
	}
	
	public final int index;
}
