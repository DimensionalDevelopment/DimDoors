package StevenDimDoors.experimental;

import java.util.ArrayList;

public class MazeDesign
{
	private PartitionNode root;
	private DirectedGraph<PartitionNode, DoorwayData> rooms;
	private ArrayList<IGraphNode<PartitionNode, DoorwayData>> cores;
	private ArrayList<BoundingBox> protectedAreas;
	
	public MazeDesign(PartitionNode root, DirectedGraph<PartitionNode, DoorwayData> rooms,
			ArrayList<IGraphNode<PartitionNode, DoorwayData>> cores)
	{
		this.root = root;
		this.rooms = rooms;
		this.cores = cores;
	}

	public PartitionNode getRootPartition()
	{
		return root;
	}

	public DirectedGraph<PartitionNode, DoorwayData> getRoomGraph()
	{
		return rooms;
	}

	public ArrayList<IGraphNode<PartitionNode, DoorwayData>> getCoreNodes()
	{
		return cores;
	}
	
	public ArrayList<BoundingBox> getProtectedAreas()
	{
		return protectedAreas;
	}
	
	public int width()
	{
		return root.width();
	}
	
	public int height()
	{
		return root.height();
	}
	
	public int length()
	{
		return root.length();
	}
}
