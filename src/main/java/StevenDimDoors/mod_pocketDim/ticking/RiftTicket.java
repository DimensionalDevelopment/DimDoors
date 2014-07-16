package StevenDimDoors.mod_pocketDim.ticking;

import StevenDimDoors.mod_pocketDim.util.Point4D;

public class RiftTicket implements Comparable<RiftTicket> {

	private long timestamp;
	private Point4D location;
	
	public RiftTicket(Point4D location, long timestamp)
	{
		this.timestamp = timestamp;
		this.location = location;
	}
	
	@Override
	public int compareTo(RiftTicket other)
	{
		if (this.timestamp < other.timestamp)
		{
			return -1;
		}
		else if (this.timestamp > other.timestamp)
		{
			return 1;
		}
		return 0;
	}
	
	public long timestamp()
	{
		return timestamp;
	}
	
	public Point4D location()
	{
		return location;
	}
	
}
