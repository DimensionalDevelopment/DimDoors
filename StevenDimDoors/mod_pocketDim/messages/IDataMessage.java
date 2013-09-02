package StevenDimDoors.mod_pocketDim.messages;

import java.io.DataOutputStream;
import java.io.IOException;

public interface IDataMessage
{ 
	public void writeToStream(DataOutputStream stream) throws IOException;
}
