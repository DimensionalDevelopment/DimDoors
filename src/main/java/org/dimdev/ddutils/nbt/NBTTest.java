package org.dimdev.ddutils.nbt;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import org.dimdev.annotatednbt.NBTSerializable;
import org.dimdev.annotatednbt.Saved;
import org.dimdev.ddutils.Location;
import org.dimdev.pocketlib.Pocket;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@NBTSerializable public final class NBTTest {
    @Saved public boolean a;
    @Saved public byte b;
    @Saved public short c;
    @Saved public int d;
    @Saved public long e;
    @Saved public char f;
    @Saved public float g;
    @Saved public double h;
    @Saved public byte[] i;
    @Saved public int[] j;
    @Saved public boolean[] k;
    @Saved public byte[] l;
    @Saved public short[] m;
    @Saved public int[] n;
    @Saved public long[] o;
    @Saved public float[] p;
    @Saved public double[] q;
    @Saved public byte[][] r;
    @Saved public int[][] s;
    @Saved public String str;
    @Saved public Vec3i vec3i;
    @Saved public BlockPos blockPos;
    @Saved public Location location;
    @Saved public String[] strArr;
    @Saved public String[][] strArrArr;
    //@Saved public String[][][][][][][][][][][][][][][] test;
    @Saved public Pocket nbtStorable;
    @Saved public List<Integer> list = new ArrayList<>();
    @Saved public Map<String, Integer> map;
    @Saved public Map<Map<List<String>, int[]>, Map<Pocket, List<Pocket>>> test2;
}
