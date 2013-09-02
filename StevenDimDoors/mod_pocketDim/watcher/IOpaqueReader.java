package StevenDimDoors.mod_pocketDim.watcher;

import com.google.common.io.ByteArrayDataInput;

public interface IOpaqueReader
{
	IOpaqueMessage read(ByteArrayDataInput source);
}
