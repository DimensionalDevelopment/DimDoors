package org.dimdev.dimdoors.rift.targets;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Set;

public class AvailableLinkTarget extends RandomTarget<AvailableLinkTarget> {
    public AvailableLinkTarget(float newRiftWeight, double weightMaximum, double coordFactor, double positiveDepthFactor, double negativeDepthFactor, Set<Integer> acceptedGroups, boolean noLink, boolean noLinkBack) {
        super(newRiftWeight, weightMaximum, coordFactor, positiveDepthFactor, negativeDepthFactor, acceptedGroups, noLink, noLinkBack);
    }

    public static final MapCodec<AvailableLinkTarget> CODEC = RecordCodecBuilder.mapCodec(instance -> common(instance).apply(instance, AvailableLinkTarget::new));

    @Override
    public VirtualTargetType<AvailableLinkTarget> getType() {
        return VirtualTargetType.AVAILABLE_LINK;
    }

    @Override
    public AvailableLinkTarget copy() {
        return new AvailableLinkTarget(this.newRiftWeight, weightMaximum, coordFactor, positiveDepthFactor, negativeDepthFactor, acceptedGroups, noLink, noLinkBack);
    }

    public static AvailableLinkTargetBuilder builder() {
        return new AvailableLinkTargetBuilder();
    }

    public static class AvailableLinkTargetBuilder extends RandomTargetBuilder<AvailableLinkTarget, AvailableLinkTargetBuilder> {

        @Override
        public AvailableLinkTarget build() {
            return new AvailableLinkTarget(this.newRiftWeight, this.weightMaximum, this.coordFactor, this.positiveDepthFactor, this.negativeDepthFactor, this.acceptedGroups, this.noLink, this.noLinkBack);
        }
    }
}
