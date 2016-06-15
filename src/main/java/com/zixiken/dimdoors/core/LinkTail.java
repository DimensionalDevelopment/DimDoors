package com.zixiken.dimdoors.core;

import com.zixiken.dimdoors.util.Point4D;

class LinkTail
{
	private Point4D destination;
	private LinkType linkType;
		
	public LinkTail(LinkType linkType, Point4D destination)
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

	public LinkType getLinkType() {
		return linkType;
	}

	public void setLinkType(LinkType linkType) {
		this.linkType = linkType;
	}
}
