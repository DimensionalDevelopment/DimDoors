package StevenDimDoors.mod_pocketDim.core;

public class LinkTypes
{
	private LinkTypes() { }
	
	public static final int ENUM_MIN = 0;
	public static final int ENUM_MAX = 7;
	
	public static final int CLIENT_SIDE = -1337;
	
	// WARNING: Don't modify these values carelessly or you'll risk breaking links in existing worlds!
	public static final int NORMAL = 0;
	public static final int POCKET = 1;
	public static final int DUNGEON = 2;
	public static final int RANDOM = 3;
	public static final int DUNGEON_EXIT = 4;
	public static final int SAFE_EXIT = 5;
	public static final int UNSAFE_EXIT = 6;
	public static final int REVERSE = 7;
}
