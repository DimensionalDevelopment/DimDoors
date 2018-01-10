package org.dimdev.dimdoors.shared.rifts;

import lombok.*;
import lombok.experimental.Wither;
import net.minecraft.nbt.NBTTagCompound;
import org.dimdev.annotatednbt.NBTSerializable;
import org.dimdev.annotatednbt.Saved;
import org.dimdev.ddutils.Location;
import org.dimdev.ddutils.nbt.INBTStorable;
import org.dimdev.ddutils.nbt.NBTUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@NBTSerializable @AllArgsConstructor @NoArgsConstructor @EqualsAndHashCode @Builder(toBuilder = true) @ToString
public class AvailableLink implements INBTStorable {
    @Wither public Location rift;

    @Saved @Builder.Default public UUID id = UUID.randomUUID();
    @Saved @Builder.Default public float floatingWeight = 1;
    @Saved @Builder.Default public float entranceWeight = 1;
    @Saved @Builder.Default public Set<Integer> groups = new HashSet<>();
    @Saved public UUID replaceDestination;
    @Saved @Builder.Default public int linksRemaining = 1;

    @Override public void readFromNBT(NBTTagCompound nbt) { NBTUtils.readFromNBT(this, nbt); }
    @Override public NBTTagCompound writeToNBT(NBTTagCompound nbt) { return NBTUtils.writeToNBT(this, nbt); }
}
