package StevenDimDoors.mod_pocketDim.saving;

import java.util.List;

import StevenDimDoors.mod_pocketDim.Point3D;

public class PackedDimData
{
	// These fields will be public since this is a simple data container
	public final static long SAVE_DATA_VERSION_ID = 982405775L;
	public final long SAVE_DATA_VERSION_ID_INSTANCE = SAVE_DATA_VERSION_ID;
	public final int ID;
	public final boolean IsDungeon;
	public final boolean IsFilled;
	public final int Depth;
	public final int PackDepth;
	public final int ParentID;
	public final int RootID;
	public final PackedDungeonData DungeonData;
	public final Point3D Origin;
	public final int Orientation;
	public final List<Integer> ChildIDs;
	public final List<PackedLinkData> Links;
	public final List<PackedLinkTail> Tails;
	
	// FIXME Missing dungeon data, not sure how to include it

	public PackedDimData(int id, int depth, int packDepth, int parentID, int rootID, int orientation,
		boolean isDungeon, boolean isFilled,PackedDungeonData dungeonData, Point3D origin, List<Integer> childIDs, List<PackedLinkData> links,
		List<PackedLinkTail> tails)
	{
		ID = id;
		Depth = depth;
		PackDepth = packDepth;
		ParentID = parentID;
		RootID = rootID;
		Orientation = orientation;
		IsDungeon = isDungeon;
		IsFilled = isFilled;
		DungeonData = dungeonData;
		Origin = origin;
		ChildIDs = childIDs;
		Links = links;
		Tails = tails;
	}
	
	@Override
	public String toString()
	{
		return "ID= "+this.ID;
	}
	
	
}
