package StevenDimDoors.mod_pocketDim.watcher;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import StevenDimDoors.mod_pocketDim.core.NewDimData;

public class ClientDimData
{
	//We'll use public fields since this is just a data container and it's immutable
	public final int ID;
	public final int RootID;
	
	public ClientDimData(int id, int rootID)
	{
		ID = id;
		RootID = rootID;
	}
	
	public ClientDimData(NewDimData dimension)
	{
		ID = dimension.id();
		RootID = dimension.root().id();
	}
	
	public void write(DataOutputStream output) throws IOException
	{
		output.writeInt(ID);
		output.writeInt(RootID);
	}
	
	public static ClientDimData read(DataInputStream input) throws IOException
	{
		return new ClientDimData(input.readInt(), input.readInt());
	}
}
