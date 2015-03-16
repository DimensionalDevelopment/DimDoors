package StevenDimDoors.mod_pocketDim.core;

import java.util.HashMap;

public enum LinkType
{
	// WARNING: Don't modify these values carelessly or you'll risk breaking links in existing worlds!
	NORMAL(0),
	POCKET(1),
	DUNGEON(2),
	RANDOM(3),
	DUNGEON_EXIT(4),
	SAFE_EXIT(5),
	UNSAFE_EXIT(6),
	REVERSE(7),
	PERSONAL(8),
    LIMBO(9),
	CLIENT(-1337);
	
	LinkType(int index)
	{
		this.index = index;
	}
	
	public final int index;
	
	/**
	 * Get the LinkType given an index. I feel like there should be a better way to do this. 
	 * @param index
	 * @return
	 */
	public static LinkType getLinkTypeFromIndex(int index)
	{
		for(LinkType type : LinkType.values())
		{
			if(type.index == index)
			{
				return type;
			}
		}
		return null;
	}
}
