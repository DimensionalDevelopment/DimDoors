package org.dimdev.dimdoors.shared.rifts.registry;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.minecraft.nbt.NBTTagCompound;
import org.dimdev.annotatednbt.NBTSerializable;
import org.dimdev.annotatednbt.Saved;
import org.dimdev.ddutils.nbt.NBTUtils;

import java.util.UUID;

@AllArgsConstructor @NoArgsConstructor @ToString
@NBTSerializable public class PlayerRiftPointer extends RegistryVertex {
    @Saved public UUID player;

    @Override public void readFromNBT(NBTTagCompound nbt) { super.readFromNBT(nbt); NBTUtils.readFromNBT(this, nbt); }
    @Override public NBTTagCompound writeToNBT(NBTTagCompound nbt) { nbt = super.writeToNBT(nbt); return NBTUtils.writeToNBT(this, nbt); }
}
