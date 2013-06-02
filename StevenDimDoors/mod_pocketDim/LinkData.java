package StevenDimDoors.mod_pocketDim;

import java.io.Serializable;

public class LinkData implements Serializable
{
	
	public int locXCoord;
	public int locYCoord;
	public int locZCoord;
	
	public int destXCoord;
	public int destYCoord;
	public int destZCoord;
	public int numberofChildren;
	public boolean isLocPocket;
	public int linkOrientation;
	

	
	public int destDimID;
	public int locDimID;
	
	public boolean exists=false;
	public boolean hasGennedDoor=false;
	
	static final long serialVersionUID = 45544342L;
	
	
	public LinkData()
	{
		this.exists=false;
	}
	
	 public LinkData(int exitLinkDimID,  int exitX, int exitY, int exitZ)
	 {
		 this.destDimID=exitLinkDimID;
		 this.destXCoord=exitX;
		 this.destYCoord=exitY;
		 this.destZCoord=exitZ;
	 }

	public LinkData(int locationDimID, int destinationDimID,   int locationXCoord, int locationYCoord, int locationZCoord, int destinationXCoord, int destinationYCoord, int destinationZCoord, boolean isPocket,int orientation)
	{
		
		this.exists=true;
		this.locXCoord=locationXCoord;
		this.locYCoord=locationYCoord;
		this.locZCoord=locationZCoord;
		
		this.destXCoord=destinationXCoord;
		this.destYCoord=destinationYCoord;
		this.destZCoord=destinationZCoord;
		
		this.destDimID=destinationDimID;
		this.locDimID=locationDimID;
		this.isLocPocket=isPocket;
		this.linkOrientation=orientation;
		
		

	}
	 
	public String printLinkData()
	{
		
		String linkInfo;
		linkInfo=String.valueOf(this.locDimID)+"locDimID "+String.valueOf(this.locXCoord)+":locXCoord "+String.valueOf(this.locYCoord)+":locYCoord "+String.valueOf(this.locZCoord)+":locZCoord ";
		linkInfo.concat("\n"+ String.valueOf(this.destDimID)+"DestDimID "+String.valueOf(this.destXCoord)+":destXCoord "+String.valueOf(this.destYCoord)+":destYCoord "+String.valueOf(this.destZCoord)+":destZCoord ");
		return linkInfo;

		

	}
}