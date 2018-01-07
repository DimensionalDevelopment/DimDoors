package org.dimdev.ddutils;

import lombok.*;
import net.minecraft.nbt.NBTTagCompound;
import org.dimdev.ddutils.nbt.INBTStorable;
import org.dimdev.ddutils.nbt.NBTUtils;
import org.dimdev.annotatednbt.Saved;
import org.dimdev.annotatednbt.NBTSerializable;

@ToString @AllArgsConstructor @NoArgsConstructor
@NBTSerializable public class RotatedLocation implements INBTStorable {
    @Getter @Saved /*private*/ Location location;
    @Getter @Saved /*private*/ float yaw;
    @Getter @Saved /*private*/ float pitch;

    @Override public NBTTagCompound writeToNBT(NBTTagCompound nbt) { return NBTUtils.writeToNBT(this, nbt); }
    @Override public void readFromNBT(NBTTagCompound nbt) { NBTUtils.readFromNBT(this, nbt); }
}
