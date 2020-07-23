package org.dimdev.dimdoors.rift.targets;

import org.dimdev.annotatednbt.AnnotatedNbt;
import org.dimdev.annotatednbt.Saved;
import org.dimdev.util.EntityUtils;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.TranslatableText;


public class PocketEntranceMarker extends VirtualTarget implements EntityTarget {
    @Saved
    protected float weight = 1;
    /*@Saved*/ protected VirtualTarget ifDestination;
    /*@Saved*/ protected VirtualTarget otherwiseDestination;

    public PocketEntranceMarker() {
    }

    public PocketEntranceMarker(float weight, VirtualTarget ifDestination, VirtualTarget otherwiseDestination) {
        this.weight = weight;
        this.ifDestination = ifDestination;
        this.otherwiseDestination = otherwiseDestination;
    }

    public static PocketEntranceMarkerBuilder builder() {
        return new PocketEntranceMarkerBuilder();
    }

    @Override
    public void fromTag(CompoundTag nbt) {
        super.fromTag(nbt);
        ifDestination = nbt.contains("ifDestination") ? VirtualTarget.readVirtualTargetNBT(nbt.getCompound("ifDestination")) : null;
        otherwiseDestination = nbt.contains("otherwiseDestination") ? VirtualTarget.readVirtualTargetNBT(nbt.getCompound("otherwiseDestination")) : null;
        AnnotatedNbt.load(this, nbt);
    }

    @Override
    public CompoundTag toTag(CompoundTag nbt) {
        nbt = super.toTag(nbt);
        if (ifDestination != null) nbt.put("ifDestination", ifDestination.toTag(new CompoundTag()));
        if (otherwiseDestination != null)
            nbt.put("otherwiseDestination", otherwiseDestination.toTag(new CompoundTag()));
        AnnotatedNbt.save(this, nbt);
        return nbt;
    }

    @Override
    public boolean receiveEntity(Entity entity, float yawOffset) {
        EntityUtils.chat(entity, new TranslatableText("The entrance of this dungeon has not been converted. If this is a normally generated pocket, please report this bug."));
        return false;
    }

    public float getWeight() {
        return weight;
    }

    public VirtualTarget getIfDestination() {
        return ifDestination;
    }

    public VirtualTarget getOtherwiseDestination() {
        return otherwiseDestination;
    }

    public String toString() {
        return "PocketEntranceMarker(weight=" + getWeight() + ", ifDestination=" + getIfDestination() + ", otherwiseDestination=" + getOtherwiseDestination() + ")";
    }

    public PocketEntranceMarkerBuilder toBuilder() {
        return new PocketEntranceMarkerBuilder().weight(weight).ifDestination(ifDestination).otherwiseDestination(otherwiseDestination);
    }

    public static class PocketEntranceMarkerBuilder {
        private float weight;
        private VirtualTarget ifDestination;
        private VirtualTarget otherwiseDestination;

        PocketEntranceMarkerBuilder() {
        }

        public PocketEntranceMarker.PocketEntranceMarkerBuilder weight(float weight) {
            this.weight = weight;
            return this;
        }

        public PocketEntranceMarker.PocketEntranceMarkerBuilder ifDestination(VirtualTarget ifDestination) {
            this.ifDestination = ifDestination;
            return this;
        }

        public PocketEntranceMarker.PocketEntranceMarkerBuilder otherwiseDestination(VirtualTarget otherwiseDestination) {
            this.otherwiseDestination = otherwiseDestination;
            return this;
        }

        public PocketEntranceMarker build() {
            return new PocketEntranceMarker(weight, ifDestination, otherwiseDestination);
        }

        public String toString() {
            return "PocketEntranceMarker.PocketEntranceMarkerBuilder(weight=" + weight + ", ifDestination=" + ifDestination + ", otherwiseDestination=" + otherwiseDestination + ")";
        }
    }
}
