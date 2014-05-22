package StevenDimDoors.mod_pocketDim.watcher;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import StevenDimDoors.mod_pocketDim.core.DDLock;
import StevenDimDoors.mod_pocketDim.core.DimLink;
import StevenDimDoors.mod_pocketDim.util.Point4D;

public class ClientLinkData
{
	public Point4D point;
	public DDLock lock;

	public ClientLinkData(DimLink link)
	{
		this.point = link.source();
		if (link.hasLock())
		{
			lock = link.getLock();
		}
	}

	public ClientLinkData(Point4D point, DDLock lock)
	{
		this.point = point;
		this.lock = lock;

	}

	public void write(DataOutputStream output) throws IOException
	{
		Point4D.write(point, output);

		boolean hasLock = this.lock != null;
		output.writeBoolean(hasLock);

		if (hasLock)
		{
			output.writeBoolean(lock.isLocked());
			output.writeInt(lock.getLockKey());
		}
	}

	public static ClientLinkData read(DataInputStream input) throws IOException
	{
		Point4D point = Point4D.read(input);
		DDLock lock = null;
		if (input.readBoolean())
		{
			lock = new DDLock(input.readBoolean(), input.readInt());
		}
		return new ClientLinkData(point, lock);
	}
}
