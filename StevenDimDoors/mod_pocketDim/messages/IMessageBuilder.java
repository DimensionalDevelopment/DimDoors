package StevenDimDoors.mod_pocketDim.messages;

import java.io.DataInputStream;
import java.io.IOException;

public interface IMessageBuilder<T>
{
	public IDataMessage createKey(T target);
	public IDataMessage createMessage(T target);
	public IDataMessage read(DataInputStream source) throws IOException;
}
