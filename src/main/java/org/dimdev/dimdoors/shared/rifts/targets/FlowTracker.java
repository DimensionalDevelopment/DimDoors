package org.dimdev.dimdoors.shared.rifts.targets;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import org.dimdev.annotatednbt.NBTSerializable;
import org.dimdev.annotatednbt.Saved;
import org.dimdev.ddutils.nbt.INBTStorable;
import org.dimdev.ddutils.nbt.NBTUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Helps flow (fluid, redstone, power) senders to keep track of flow received by the
 * target rift.
 */
@NBTSerializable
public class FlowTracker implements INBTStorable { // TODO
    //@Saved public Map<EnumFacing, Map<Fluid, Integer>> fluids = new HashMap<>();
    @Saved public Map<EnumFacing, Integer> redstone = new HashMap<>();
    @Saved public Map<EnumFacing, Integer> power = new HashMap<>();

    @Override public void readFromNBT(NBTTagCompound nbt) { NBTUtils.readFromNBT(this, nbt); }
    @Override public NBTTagCompound writeToNBT(NBTTagCompound nbt) { return NBTUtils.writeToNBT(this, nbt); }
}
