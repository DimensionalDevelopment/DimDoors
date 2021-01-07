package org.dimdev.dimdoors.rift.targets;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.dimdev.dimdoors.util.EntityUtils;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.TranslatableText;


public class PocketEntranceMarker extends VirtualTarget implements EntityTarget {

    protected float weight = 1;
    protected VirtualTarget ifDestination;
    protected VirtualTarget otherwiseDestination;

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
    public boolean receiveEntity(Entity entity, float yawOffset) {
        EntityUtils.chat(entity, new TranslatableText("The entrance of this dungeon has not been converted. If this is a normally generated pocket, please report this bug."));
        return false;
    }

    public float getWeight() {
        return this.weight;
    }

    public VirtualTarget getIfDestination() {
        return this.ifDestination;
    }

    public VirtualTarget getOtherwiseDestination() {
        return this.otherwiseDestination;
    }

    public String toString() {
        return "PocketEntranceMarker(weight=" + this.getWeight() + ", ifDestination=" + this.getIfDestination() + ", otherwiseDestination=" + this.getOtherwiseDestination() + ")";
    }

    public PocketEntranceMarkerBuilder toBuilder() {
        return new PocketEntranceMarkerBuilder().weight(this.weight).ifDestination(this.ifDestination).otherwiseDestination(this.otherwiseDestination);
    }


    @Override
    public VirtualTargetType<? extends VirtualTarget> getType() {
        return VirtualTargetType.POCKET_ENTRANCE;
    }

    public static CompoundTag toTag(PocketEntranceMarker target) {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("weight", target.weight);
        tag.put("ifDestination", VirtualTarget.toTag(target.ifDestination));
        tag.put("otherwiseDestination", VirtualTarget.toTag(target.otherwiseDestination));
        return tag;
    }

    public static PocketEntranceMarker fromTag(CompoundTag tag) {
        PocketEntranceMarker target = new PocketEntranceMarker();
        target.weight = tag.getFloat("weight");
        target.ifDestination = VirtualTarget.fromTag(tag.getCompound("ifDestination"));
        target.otherwiseDestination = VirtualTarget.fromTag(tag.getCompound("otherwiseDestination"));
        return target;
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
            return new PocketEntranceMarker(this.weight, this.ifDestination, this.otherwiseDestination);
        }

        public String toString() {
            return "PocketEntranceMarker.PocketEntranceMarkerBuilder(weight=" + this.weight + ", ifDestination=" + this.ifDestination + ", otherwiseDestination=" + this.otherwiseDestination + ")";
        }
    }
}
