package StevenDimDoors.mod_pocketDim.watcher;

import java.io.*;

import StevenDimDoors.mod_pocketDim.core.DimensionType;
import StevenDimDoors.mod_pocketDim.core.NewDimData;

public class ClientDimData
{
	//We'll use public fields since this is just a data container and it's immutable
	public final int ID;
	public final int rootID;
	public final DimensionType type;
	
	public ClientDimData(int id, int rootID, DimensionType type)
	{
		ID = id;
		this.rootID = rootID;
		this.type = type;
	}
	
	public ClientDimData(NewDimData dimension)
	{
		ID = dimension.id();
		this.rootID = dimension.root().id();
		this.type = dimension.type();
	}
	
	public void write(DataOutput output) throws IOException
	{
		output.writeInt(ID);
		output.writeInt(rootID);
		output.writeInt(type.index);
	}
	
	public static ClientDimData read(DataInput input) throws IOException
	{
		int id = input.readInt();
		int rootID = input.readInt();
		int index = input.readInt();
		return new ClientDimData(id, rootID, DimensionType.getTypeFromIndex(index));
	}
}
