package StevenDimDoors.mod_pocketDim.core;

import java.io.Serializable;

import StevenDimDoors.mod_pocketDim.util.Point4D;

public class NewLinkData implements Serializable
{
	private static final long serialVersionUID = 1462177151401498444L;
	
	private Point4D source;
	private Point4D destination;

	public NewLinkData(int srcX, int srcY, int srcZ, int srcDimension)
	{
		source = new Point4D(srcX, srcY, srcZ, srcDimension);
		destination = null;
	}

	public NewLinkData(int srcX, int srcY, int srcZ, int srcDimension, int dstX, int dstY, int dstZ, int dstDimension)
	{
		source = new Point4D(srcX, srcY, srcZ, srcDimension);
		destination = new Point4D(dstX, dstY, dstZ, dstDimension);
	}
	
	public NewLinkData(Point4D source, Point 4D destination)
	{
		
	}

	@Override
	public String toString()
	{
		return source + " -> " + destination;
	}
}