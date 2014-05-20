package StevenDimDoors.mod_pocketDim.saving;

import java.util.List;

import StevenDimDoors.mod_pocketDim.Point3D;
import StevenDimDoors.mod_pocketDim.util.Point4D;

public class PackedLinkData
{
	public final Point4D source;
	public final Point3D parent;
	public final PackedLinkTail tail;
	public final int orientation;
	public final List<Point3D> children;
	public final boolean locked;
	
	public PackedLinkData(Point4D source, Point3D parent, PackedLinkTail tail, int orientation, List<Point3D> children, boolean locked)
	{
		this.source=source;
		this.parent=parent;
		this.tail=tail;
		this.orientation=orientation;
		this.children=children;
		this.locked = locked;
	}
}
