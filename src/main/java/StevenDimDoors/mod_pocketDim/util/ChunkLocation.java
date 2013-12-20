package StevenDimDoors.mod_pocketDim.util;

public class ChunkLocation {
	
	public int ChunkX;
	public int ChunkZ;
	public int DimensionID;
	
	public ChunkLocation(int dimensionID, int chunkX, int chunkZ)
	{
		this.DimensionID = dimensionID;
		this.ChunkX = chunkX;
		this.ChunkZ = chunkZ;
	}
}
