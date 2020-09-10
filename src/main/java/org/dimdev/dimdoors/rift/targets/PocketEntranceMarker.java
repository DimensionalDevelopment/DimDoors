package org.dimdev.dimdoors.rift.targets;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.dimdev.dimdoors.util.EntityUtils;

import net.minecraft.entity.Entity;
import net.minecraft.text.TranslatableText;

public class PocketEntranceMarker extends VirtualTarget implements EntityTarget {
    public static final Codec<PocketEntranceMarker> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
                Codec.FLOAT.fieldOf("weight").forGetter(target -> target.weight),
                VirtualTarget.CODEC.fieldOf("ifDestination").forGetter(target -> target.ifDestination),
                VirtualTarget.CODEC.fieldOf("otherwiseDestination").forGetter(target -> target.otherwiseDestination)
        ).apply(instance, PocketEntranceMarker::new);
    });

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

    @Override
    public VirtualTargetType<? extends VirtualTarget> getType() {
        return VirtualTargetType.POCKET_ENTRANCE;
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
