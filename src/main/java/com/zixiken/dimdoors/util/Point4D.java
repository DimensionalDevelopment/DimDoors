package com.zixiken.dimdoors.util;

import java.io.*;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;


public final class Point4D implements Comparable<Point4D> {
	
	private final BlockPos pos;
	private final int dimension;
	
	/**
	 * 
	 * @param pos
	 * @param dimension
	 */
	public Point4D(BlockPos pos, int dimension)
	{
		this.pos = pos;
		this.dimension = dimension;
	}

	public int getX()
	{
		return pos.getX();
	}

	public int getY()
	{
		return pos.getY();
	}

	public int getZ()
	{
		return pos.getZ();
	}

	public int getDimension()
	{
		return dimension;
	}
	
	@Override
	public int hashCode() {
		//Time for some witchcraft.
		//The code here is inspired by a discussion on Stack Overflow regarding hash codes for 3D.
		//Source: http://stackoverflow.com/questions/9858376/hashcode-for-3d-integer-coordinates-with-high-spatial-coherence
		
		//I believe that most of the time, any points we might be hashing will be in close proximity to each other.
		//For instance, points that are within the same chunk or within a few neighboring chunks. Only the low-order
		//bits of each component would differ. I'll use 8 bits from Y and the 12 bits from X and Z. ~SenseiKiwi
		
		int bit;
		int hash;
		int index;
		
		hash = 0;
		index = 0;
		for (bit = 0; bit < 8; bit++) {
			hash |= ((pos.getY() >> bit) & 1) << index;
			index++;
			hash |= ((pos.getX() >> bit) & 1) << index;
			index++;
			hash |= ((pos.getZ() >> bit) & 1) << index;
			index++;
		}
		for (; bit < 12; bit++) {
			hash |= ((pos.getX() >> bit) & 1) << index;
			index++;
			hash |= ((pos.getZ() >> bit) & 1) << index;
			index++;
		}
		return hash;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		return equals((Point4D) obj);
	}
	
	public BlockPos toBlockPos() {
		return new BlockPos(this.pos);
	}
	
	public boolean equals(Point4D other) {
		if (this == other)
			return true;
		if (other == null)
			return false;
		return (pos.equals(other.pos) && dimension == other.dimension);
	}

	@Override
	public int compareTo(Point4D other) {
		BlockPos diff = pos.subtract(other.pos);
		if (diff.getX() != 0)
			return diff.getX();
		if (diff.getY() != 0)
			return diff.getZ();
		if (diff.getZ() != 0)
			return diff.getZ();
		return dimension - other.dimension;
	}

	@Override
	public String toString()
	{
		return "(" + pos + ", " + dimension + ")";
	}

    public static void writeToNBT(Point4D point, NBTTagCompound tag) {
        if (point != null) {
            tag.setInteger("X", point.getX());
            tag.setInteger("Y", point.getY());
            tag.setInteger("Z", point.getZ());
            tag.setInteger("Dimension", point.dimension);
        }
    }

	public static void write(Point4D point, ByteBuf stream) throws IOException {
		stream.writeBoolean(point != null);
		if (point != null) {
			stream.writeInt(point.getX());
			stream.writeInt(point.getY());
			stream.writeInt(point.getZ());
			stream.writeInt(point.dimension);
		}
	}
	
	public static Point4D read(ByteBuf stream) throws IOException {
		if (stream.readBoolean()) {
			return new Point4D(new BlockPos(stream.readInt(), stream.readInt(), stream.readInt()), stream.readInt() );
		} else {
			return null;
		}
	}

    public static Point4D readFromNBT(NBTTagCompound tag) {
        return new Point4D(new BlockPos(tag.getInteger("X"), tag.getInteger("Y"), tag.getInteger("Z")), tag.getInteger("Dimension"));
    }
}
