package org.dimdev.ddutils.nbt;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import org.dimdev.ddutils.Location;
import org.dimdev.dimdoors.shared.pockets.Pocket;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SavedToNBT
public final class NBTTest {
    @SavedToNBT public boolean a;
    @SavedToNBT public byte b;
    @SavedToNBT public short c;
    @SavedToNBT public int d;
    @SavedToNBT public long e;
    @SavedToNBT public char f;
    @SavedToNBT public float g;
    @SavedToNBT public double h;
    @SavedToNBT public byte[] i;
    @SavedToNBT public int[] j;
    @SavedToNBT public boolean[] k;
    @SavedToNBT public byte[] l;
    @SavedToNBT public short[] m;
    @SavedToNBT public int[] n;
    @SavedToNBT public long[] o;
    @SavedToNBT public float[] p;
    @SavedToNBT public double[] q;
    @SavedToNBT public byte[][] r;
    @SavedToNBT public int[][] s;
    @SavedToNBT public String str;
    @SavedToNBT public Vec3i vec3i;
    @SavedToNBT public BlockPos blockPos;
    @SavedToNBT public Location location;
    @SavedToNBT public String[] strArr;
    @SavedToNBT public String[][] strArrArr;
    //@SavedToNBT public String[][][][][][][][][][][][][][][] test;
    @SavedToNBT public Pocket nbtStorable;
    @SavedToNBT public List<Integer> list = new ArrayList<>();
    @SavedToNBT public Map<String, Integer> map;
    @SavedToNBT public Map<Map<List<String>, int[]>, Map<Pocket, List<Pocket>>> test2;
}
