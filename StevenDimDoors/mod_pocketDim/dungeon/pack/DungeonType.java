package StevenDimDoors.mod_pocketDim.dungeon.pack;

public class DungeonType implements Comparable<DungeonType>
{
	public final DungeonPack Owner;
	public final String Name;
	public final int ID;
	
	public DungeonType(DungeonPack owner, String name, int id)
	{
		Owner = owner;
		Name = name;
		this.ID = id;
	}

	@Override
	public int compareTo(DungeonType other)
	{
		return this.ID - other.ID;
	}
	
	@Override
	public boolean equals(Object other)
	{
		return equals((DungeonType) other);
	}
	
	public boolean equals(DungeonType other)
	{
		if (this == other)
			return true;
		
		if (this == null || other == null)
			return false;
		
		return (this.ID == other.ID);
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 2039;
		return prime * ID;
	}
}
