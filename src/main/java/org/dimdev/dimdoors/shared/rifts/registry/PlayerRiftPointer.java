package org.dimdev.dimdoors.shared.rifts.registry;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.dimdev.annotatednbt.NBTSerializable;
import org.dimdev.annotatednbt.Saved;

import java.util.UUID;

@AllArgsConstructor @NoArgsConstructor
@NBTSerializable public class PlayerRiftPointer extends RegistryVertex {
    @Saved public UUID player;
}
