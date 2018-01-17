package org.dimdev.dimdoors.shared.rifts.registry;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.dimdev.annotatednbt.NBTSerializable;
import org.dimdev.annotatednbt.Saved;

@AllArgsConstructor @NoArgsConstructor @NBTSerializable
public class PocketEntrancePointer extends RegistryVertex { // TODO: PocketRiftPointer superclass?
    @Saved public int pocketDim;
    @Saved public int pocketId;
}
