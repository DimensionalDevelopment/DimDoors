package StevenDimDoors.mod_pocketDim.core;

import StevenDimDoors.mod_pocketDim.util.Point4D;

class LinkTail
{
	private Point4D destination;
	private int linkType;
		
	public LinkTail(int linkType, Point4D destination)
	{
		this.linkType = linkType;
		this.destination = destination;
	}

	public Point4D getDestination() {
		return destination;
	}

	public void setDestination(Point4D destination) {
		this.destination = destination;
	}

	public int getLinkType() {
		return linkType;
	}

	public void setLinkType(int linkType) {
		this.linkType = linkType;
	}
}
