package org.dimdev.ddutils;

import lombok.*;
import net.minecraft.nbt.NBTTagCompound;
import org.dimdev.ddutils.nbt.INBTStorable;
import org.dimdev.ddutils.nbt.NBTUtils;
import org.dimdev.ddutils.nbt.SavedToNBT;

@ToString @AllArgsConstructor @NoArgsConstructor
@SavedToNBT public class RotatedLocation implements INBTStorable {
    @Getter @SavedToNBT /*private*/ Location location;
    @Getter @SavedToNBT /*private*/ float yaw;
    @Getter @SavedToNBT /*private*/ float pitch;

    @Override public NBTTagCompound writeToNBT(NBTTagCompound nbt) { return NBTUtils.writeToNBT(this, nbt); }
    @Override public void readFromNBT(NBTTagCompound nbt) { NBTUtils.readFromNBT(this, nbt); }
}
