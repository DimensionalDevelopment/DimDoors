package org.dimdev.dimdoors.rift.registry;

import net.minecraft.nbt.CompoundTag;
import org.dimdev.annotatednbt.Saved;
import org.dimdev.annotatednbt.AnnotatedNbt;

public class PocketEntrancePointer extends RegistryVertex { // TODO: PocketRiftPointer superclass?
    @Saved public int pocketDim;
    @Saved public int pocketId;

    public PocketEntrancePointer(int pocketDim, int pocketId) {
        this.pocketDim = pocketDim;
        this.pocketId = pocketId;
    }

    public PocketEntrancePointer() {}

    @Override
    public void fromTag(CompoundTag nbt) {
        super.fromTag(nbt);
        AnnotatedNbt.fromTag(this, nbt);
    }

    @Override
    public CompoundTag toTag(CompoundTag nbt) {
        nbt = super.toTag(nbt);
        return AnnotatedNbt.toTag(this, nbt);
    }

    public String toString() {return "PocketEntrancePointer(pocketDim=" + this.pocketDim + ", pocketId=" + this.pocketId + ")";}
}
