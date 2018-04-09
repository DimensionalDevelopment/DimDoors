package org.dimdev.dimdoors.shared.rifts.targets;

import org.dimdev.ddutils.Location;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import net.minecraft.nbt.NBTTagCompound;
import org.dimdev.ddutils.nbt.NBTUtils;
import org.dimdev.annotatednbt.Saved;
import org.dimdev.annotatednbt.NBTSerializable;

@Getter @AllArgsConstructor @Builder(toBuilder = true) @ToString
@NBTSerializable public class GlobalReference extends RiftReference {
    @Saved protected Location target;

    public GlobalReference() {}

    @Override public void readFromNBT(NBTTagCompound nbt) { super.readFromNBT(nbt); NBTUtils.readFromNBT(this, nbt); }
    @Override public NBTTagCompound writeToNBT(NBTTagCompound nbt) { nbt = super.writeToNBT(nbt); return NBTUtils.writeToNBT(this, nbt); }

    @Override
    public Location getReferencedLocation() {
        return target;
    }
}
