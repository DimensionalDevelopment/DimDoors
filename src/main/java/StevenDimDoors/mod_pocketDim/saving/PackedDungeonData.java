package StevenDimDoors.mod_pocketDim.saving;

public class PackedDungeonData 
{
	public final int Weight;
	public final boolean IsOpen;
	public final boolean IsInternal;
	public final String SchematicPath;
	public final String SchematicName;
	public final String DungeonTypeName;
	public final String DungeonPackName;
	
	public PackedDungeonData(int weight, boolean isOpen, boolean isInternal, String schematicPath, String schematicName, String dungeonTypeName, String dungeonPackName)
	{
		this.Weight= weight;
		this.IsOpen=isOpen;
		this.IsInternal=isInternal;
		this.SchematicName=schematicName;
		this.SchematicPath=schematicPath;
		this.DungeonTypeName=dungeonTypeName;
		this.DungeonPackName=dungeonPackName;
	}
}
