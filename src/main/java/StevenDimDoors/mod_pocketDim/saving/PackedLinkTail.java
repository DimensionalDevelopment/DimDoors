package StevenDimDoors.mod_pocketDim.saving;

import StevenDimDoors.mod_pocketDim.core.LinkType;
import StevenDimDoors.mod_pocketDim.util.Point4D;

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
