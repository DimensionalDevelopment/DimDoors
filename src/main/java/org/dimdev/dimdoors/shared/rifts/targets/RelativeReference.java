package org.dimdev.dimdoors.shared.rifts.targets;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3i;
import org.dimdev.annotatednbt.NBTSerializable;
import org.dimdev.annotatednbt.Saved;
import org.dimdev.ddutils.Location;
import org.dimdev.ddutils.nbt.NBTUtils;

@Getter @AllArgsConstructor @Builder(toBuilder = true) @ToString
@NBTSerializable public class RelativeReference extends RiftReference {
    @Saved protected Vec3i offset;

    public RelativeReference() {}

    @Override public void readFromNBT(NBTTagCompound nbt) { super.readFromNBT(nbt); NBTUtils.readFromNBT(this, nbt); }
    @Override public NBTTagCompound writeToNBT(NBTTagCompound nbt) { nbt = super.writeToNBT(nbt); return NBTUtils.writeToNBT(this, nbt); }

    @Override
    public Location getReferencedLocation() {
        return new Location(location.dim, location.pos.add(offset));
    }
}
