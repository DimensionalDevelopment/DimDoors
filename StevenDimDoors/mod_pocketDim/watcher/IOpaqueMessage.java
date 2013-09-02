package StevenDimDoors.mod_pocketDim.watcher;

import java.io.DataOutputStream;

public interface IOpaqueMessage
{
	void writeToStream(DataOutputStream stream);
}
