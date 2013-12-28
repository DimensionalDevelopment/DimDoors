package StevenDimDoors.experimental;

import java.util.ArrayList;

public class RoomNode
{
	private ArrayList<DoorwayData> outbound;
	private ArrayList<DoorwayData> inbound;
	private PartitionNode bounds;
	private int distance;
	private boolean visited;
	
	public RoomNode(PartitionNode bounds)
	{
		this.bounds = bounds;
		this.distance = 0;
		this.visited = false;
		this.outbound = new ArrayList<DoorwayData>();
		this.inbound = new ArrayList<DoorwayData>();
	}
	
	public int distance()
	{
		return distance;
	}
	
	public boolean isVisited()
	{
		return visited;
	}
	
	public void setDistance(int value)
	{
		distance = value;
	}
	
	public void setVisited(boolean value)
	{
		visited = value;
	}
	
	public PartitionNode bounds()
	{
		return bounds;
	}
	
	public void addInboundDoorway(DoorwayData data)
	{
		inbound.add(data);
	}
	
	public void addOutboundDoorway(DoorwayData data)
	{
		outbound.add(data);
	}
}
