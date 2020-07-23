package org.dimdev.dimdoors.rift.registry;

import org.dimdev.annotatednbt.AnnotatedNbt;
import org.dimdev.annotatednbt.Saved;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.World;

public class PocketEntrancePointer extends RegistryVertex { // TODO: PocketRiftPointer superclass?
    @Saved
    public World pocketDim;
    @Saved
    public int pocketId;

    public PocketEntrancePointer(World pocketDim, int pocketId) {
        this.pocketDim = pocketDim;
        this.pocketId = pocketId;
    }

    public PocketEntrancePointer() {
    }

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

    public String toString() {
        return "PocketEntrancePointer(pocketDim=" + this.pocketDim + ", pocketId=" + this.pocketId + ")";
    }
}
