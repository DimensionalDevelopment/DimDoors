package com.zixiken.dimdoors.shared;

import com.zixiken.dimdoors.shared.util.Location;
import lombok.Builder;
import lombok.ToString;
import lombok.Value;
import net.minecraft.nbt.NBTTagCompound;

@Value @ToString @Builder(toBuilder = true)
public class VirtualLocation {
    int dimID;
    int x;
    int y;
    int z;
    int depth;

    public NBTTagCompound writeToNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("dim", dimID);
        nbt.setInteger("x", x);
        nbt.setInteger("y", y);
        nbt.setInteger("z", z);
        nbt.setInteger("depth", depth);
        return nbt;
    }

    public static VirtualLocation readFromNBT(NBTTagCompound nbt) {
        int dim = nbt.getInteger("dim");
        int x = nbt.getInteger("x");
        int y = nbt.getInteger("y");
        int z = nbt.getInteger("z");
        int depth = nbt.getInteger("depth");
        return new VirtualLocation(dim, x, y, z, depth);
    }

    public Location getOverworldLocation() {
        return new Location(dimID, x, y, z);
    }
}
