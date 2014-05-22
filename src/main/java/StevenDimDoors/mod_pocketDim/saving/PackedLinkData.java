package StevenDimDoors.mod_pocketDim.saving;

import java.util.List;

import StevenDimDoors.mod_pocketDim.Point3D;
import StevenDimDoors.mod_pocketDim.core.DDLock;
import StevenDimDoors.mod_pocketDim.util.Point4D;

public class PackedLinkData
{
	public final Point4D source;
	public final Point3D parent;
	public final PackedLinkTail tail;
	public final int orientation;
	public final List<Point3D> children;
	public final DDLock lock;
	
	public PackedLinkData(Point4D source, Point3D parent, PackedLinkTail tail, int orientation, List<Point3D> children, DDLock lock)
	{
		this.source=source;
		this.parent=parent;
		this.tail=tail;
		this.orientation=orientation;
		this.children=children;
		this.lock = lock;
	}
}
