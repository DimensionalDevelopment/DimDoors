package StevenDimDoors.mod_pocketDim.watcher;

import java.io.*;

import StevenDimDoors.mod_pocketDim.core.DDLock;
import StevenDimDoors.mod_pocketDim.core.DimLink;
import StevenDimDoors.mod_pocketDim.core.LinkType;
import StevenDimDoors.mod_pocketDim.util.Point4D;
import net.minecraft.nbt.NBTTagCompound;

public class ClientLinkData
{
	public final Point4D point;
	public final DDLock lock;
	public final LinkType type;
	
	public ClientLinkData(DimLink link)
	{
		this.point = link.source();
		this.type = link.linkType();
		if (link.hasLock())
		{
			lock = link.getLock();
		}
		else
		{
			lock = null;
		}
	}

	public ClientLinkData(Point4D point, LinkType type, DDLock lock)
	{
		this.point = point;
		this.lock = lock;
		this.type = type;

	}

	public void write(DataOutput output) throws IOException
	{
		Point4D.write(point, output);
		output.writeInt(this.type.index);
		boolean hasLock = this.lock != null;
		output.writeBoolean(hasLock);

		if (hasLock)
		{
			output.writeBoolean(lock.getLockState());
			output.writeInt(lock.getLockKey());
		}
	}

    public void writeToNBT(NBTTagCompound tag) {
        tag.setInteger("Type", this.type.index);

        if (this.lock != null) {
            NBTTagCompound lock = new NBTTagCompound();
            lock.setBoolean("State", this.lock.getLockState());
            lock.setInteger("Key", this.lock.getLockKey());
            tag.setTag("Lock", lock);
        }

        if (this.point != null) {
            NBTTagCompound point = new NBTTagCompound();
            Point4D.writeToNBT(this.point, point);
            tag.setTag("Point", point);
        }
    }

	public static ClientLinkData read(DataInput input) throws IOException
	{
		Point4D point = Point4D.read(input);
		LinkType type = LinkType.getLinkTypeFromIndex(input.readInt());
		DDLock lock = null;
		if (input.readBoolean())
		{
			lock = new DDLock(input.readBoolean(), input.readInt());
		}
		return new ClientLinkData(point, type, lock);
	}

    public static ClientLinkData readFromNBT(NBTTagCompound tag) {
        LinkType type = LinkType.getLinkTypeFromIndex(tag.getInteger("Type"));
        Point4D point = null;
        if (tag.hasKey("Point"))
            point = Point4D.readFromNBT(tag.getCompoundTag("Point"));
        DDLock lock = null;
        if (tag.hasKey("Lock")) {
            NBTTagCompound lockTag = tag.getCompoundTag("Lock");
            lock = new DDLock(lockTag.getBoolean("State"),lockTag.getInteger("Key"));
        }
        return new ClientLinkData(point, type, lock);
    }
}
