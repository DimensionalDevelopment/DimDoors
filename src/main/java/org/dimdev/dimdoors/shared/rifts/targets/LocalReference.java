package org.dimdev.dimdoors.shared.rifts.targets;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import org.dimdev.annotatednbt.NBTSerializable;
import org.dimdev.annotatednbt.Saved;
import org.dimdev.ddutils.Location;
import org.dimdev.ddutils.nbt.NBTUtils;

@Getter @AllArgsConstructor @Builder(toBuilder = true) @ToString
@NBTSerializable public class LocalReference extends RiftReference {
    @Saved protected BlockPos target;

    public LocalReference() {}

    @Override public void readFromNBT(NBTTagCompound nbt) { super.readFromNBT(nbt); NBTUtils.readFromNBT(this, nbt); }
    @Override public NBTTagCompound writeToNBT(NBTTagCompound nbt) { nbt = super.writeToNBT(nbt); return NBTUtils.writeToNBT(this, nbt); }

    @Override
    public Location getReferencedLocation() {
        return new Location(location.getDim(), target);
    }
}
