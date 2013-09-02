package StevenDimDoors.mod_pocketDim.core;

import StevenDimDoors.mod_pocketDim.util.Point4D;

public interface IDimLink
{
	public final int TYPE_ENUM_MIN = 0;
	public final int TYPE_ENUM_MAX = 8;
	
	public final int TYPE_NORMAL = 0;
	public final int TYPE_LIMBO = 1;
	public final int TYPE_POCKET = 2;
	public final int TYPE_DUNGEON = 3;
	public final int TYPE_RANDOM = 4;
	public final int TYPE_DUNGEON_EXIT = 5;
	public final int TYPE_SAFE_EXIT = 6;
	public final int TYPE_UNSAFE_EXIT = 7;
	public final int TYPE_RANDOM_DUNGEON = 8;
	
	public Point4D source();
	public Point4D destination();
	public boolean hasDestination();
	public Iterable<IDimLink> children();
	public int childCount();
	public IDimLink parent();
	public int linkType();
}