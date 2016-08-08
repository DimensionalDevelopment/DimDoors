package com.zixiken.dimdoors.watcher;

import java.io.*;

import com.zixiken.dimdoors.core.DimData;
import com.zixiken.dimdoors.core.DimensionType;
import io.netty.buffer.ByteBuf;

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
	
	public ClientDimData(DimData dimension)
	{
		ID = dimension.id();
		this.rootID = dimension.root().id();
		this.type = dimension.type();
	}
	
	public void write(ByteBuf output) throws IOException
	{
		output.writeInt(ID);
		output.writeInt(rootID);
		output.writeInt(type.index);
	}
	
	public static ClientDimData read(ByteBuf input) throws IOException
	{
		int id = input.readInt();
		int rootID = input.readInt();
		int index = input.readInt();
		return new ClientDimData(id, rootID, DimensionType.getTypeFromIndex(index));
	}
}
