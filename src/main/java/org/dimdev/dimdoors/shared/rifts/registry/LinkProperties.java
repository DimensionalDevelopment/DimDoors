package org.dimdev.dimdoors.shared.rifts.registry;

import lombok.*;
import net.minecraft.nbt.NBTTagCompound;
import org.dimdev.annotatednbt.NBTSerializable;
import org.dimdev.annotatednbt.Saved;
import org.dimdev.ddutils.nbt.INBTStorable;
import org.dimdev.ddutils.nbt.NBTUtils;

import java.util.HashSet;
import java.util.Set;

@NBTSerializable @AllArgsConstructor @NoArgsConstructor @EqualsAndHashCode @Builder(toBuilder = true) @ToString
public class LinkProperties implements INBTStorable {
    @Saved @Builder.Default public float floatingWeight = 1; // TODO: depend on rift properties (ex. size, stability, or maybe a getWeightFactor method) rather than rift type
    @Saved @Builder.Default public float entranceWeight = 1;
    @Saved @Builder.Default public Set<Integer> groups = new HashSet<>();
    @Saved @Builder.Default public int linksRemaining = 1;
    @Saved @Builder.Default public boolean oneWay = false;

    @Override public void readFromNBT(NBTTagCompound nbt) { NBTUtils.readFromNBT(this, nbt); }
    @Override public NBTTagCompound writeToNBT(NBTTagCompound nbt) { return NBTUtils.writeToNBT(this, nbt); }
}
