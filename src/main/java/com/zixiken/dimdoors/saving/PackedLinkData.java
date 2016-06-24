package com.zixiken.dimdoors.saving;

import java.util.List;

import com.zixiken.dimdoors.Point3D;
import com.zixiken.dimdoors.core.DDLock;
import com.zixiken.dimdoors.util.Point4D;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class PackedLinkData
{
	public final Point4D source;
	public final BlockPos parent;
	public final PackedLinkTail tail;
	public final EnumFacing orientation;
	public final List<BlockPos> children;
	public final DDLock lock;
	
	public PackedLinkData(Point4D source, BlockPos parent, PackedLinkTail tail, EnumFacing orientation, List<BlockPos> children, DDLock lock) {
		this.source=source;
		this.parent=parent;
		this.tail=tail;
		this.orientation=orientation;
		this.children=children;
		this.lock = lock;
	}
}
