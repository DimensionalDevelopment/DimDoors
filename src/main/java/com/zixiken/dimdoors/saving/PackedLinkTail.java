package com.zixiken.dimdoors.saving;

import com.zixiken.dimdoors.core.LinkType;
import com.zixiken.dimdoors.util.Point4D;

public class PackedLinkTail
{
	public final Point4D destination;
	public final int linkType;
	
	public PackedLinkTail(Point4D destination, LinkType linkType)
	{
		this.destination=destination;
		this.linkType=linkType.index;
	}
	
}
