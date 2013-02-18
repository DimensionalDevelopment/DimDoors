package Steven.Common.mod_pocketDim;

import java.io.PrintStream;
import java.io.Serializable;

public class LinkData
  implements Serializable
{
  public int locXCoord;
  public int locYCoord;
  public int locZCoord;
  public int destXCoord;
  public int destYCoord;
  public int destZCoord;
  public int numberofChildren = 0;
  public boolean isLocPocket;
  public int linkOrientation;
  public int destDimID;
  public int locDimID;
  public boolean exists = false;

  public LinkData()
  {
    this.exists = false;
  }

  public LinkData(int exitLinkDimID, int exitX, int exitY, int exitZ)
  {
    this.destDimID = exitLinkDimID;
    this.destXCoord = exitX;
    this.destYCoord = exitY;
    this.destZCoord = exitZ;
  }

  public LinkData(int locationDimID, int destinationDimID, int locationXCoord, int locationYCoord, int locationZCoord, int destinationXCoord, int destinationYCoord, int destinationZCoord, boolean isPocket)
  {
    this.exists = true;
    this.locXCoord = locationXCoord;
    this.locYCoord = locationYCoord;
    this.locZCoord = locationZCoord;

    this.destXCoord = destinationXCoord;
    this.destYCoord = destinationYCoord;
    this.destZCoord = destinationZCoord;

    this.destDimID = destinationDimID;
    this.locDimID = locationDimID;
    this.isLocPocket = isPocket;
  }

  public void printLinkData()
  {
    System.out.println(String.valueOf(this.locDimID) + "locDimID " + String.valueOf(this.locXCoord) + "-locXCoord " + String.valueOf(this.locYCoord) + "-locYCoord " + String.valueOf(this.locZCoord) + "-locZCoord ");
    System.out.println(String.valueOf(this.destDimID) + "DestDimID " + String.valueOf(this.destXCoord) + "-destXCoord " + String.valueOf(this.destYCoord) + "-destYCoord " + String.valueOf(this.destZCoord) + "-destZCoord ");
  }
}