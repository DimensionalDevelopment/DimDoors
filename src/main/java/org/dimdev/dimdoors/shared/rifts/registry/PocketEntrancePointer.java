package org.dimdev.dimdoors.shared.rifts.registry;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.minecraft.nbt.NBTTagCompound;
import org.dimdev.annotatednbt.NBTSerializable;
import org.dimdev.annotatednbt.Saved;
import org.dimdev.ddutils.nbt.NBTUtils;

@AllArgsConstructor @NoArgsConstructor @ToString
@NBTSerializable public class PocketEntrancePointer extends RegistryVertex { // TODO: PocketRiftPointer superclass?
    @Saved public int pocketDim;
    @Saved public int pocketId;

    @Override public void readFromNBT(NBTTagCompound nbt) { super.readFromNBT(nbt); NBTUtils.readFromNBT(this, nbt); }
    @Override public NBTTagCompound writeToNBT(NBTTagCompound nbt) { nbt = super.writeToNBT(nbt); return NBTUtils.writeToNBT(this, nbt); }
}
