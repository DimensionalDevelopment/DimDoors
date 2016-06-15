package com.zixiken.dimdoors.saving;

import java.util.List;

import com.zixiken.dimdoors.Point3D;
import com.zixiken.dimdoors.core.DDLock;
import com.zixiken.dimdoors.util.Point4D;

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
