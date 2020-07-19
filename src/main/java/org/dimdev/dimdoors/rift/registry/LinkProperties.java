package org.dimdev.dimdoors.rift.registry;

import org.dimdev.annotatednbt.Saved;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


public class LinkProperties {
    @Saved public float floatingWeight = 1; // TODO: depend on rift properties (ex. size, stability, or maybe a getWeightFactor method) rather than rift type
    @Saved public float entranceWeight = 1;
    @Saved public Set<Integer> groups = new HashSet<>();
    @Saved public int linksRemaining = 1;
    @Saved public boolean oneWay = false;

    public LinkProperties(float floatingWeight, float entranceWeight, Set<Integer> groups, int linksRemaining, boolean oneWay) {
        this.floatingWeight = floatingWeight;
        this.entranceWeight = entranceWeight;
        this.groups = groups;
        this.linksRemaining = linksRemaining;
        this.oneWay = oneWay;
    }

    public LinkProperties() {}

    public static LinkPropertiesBuilder builder() {return new LinkPropertiesBuilder();}

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof LinkProperties)) return false;
        final LinkProperties other = (LinkProperties) o;
        if (!other.canEqual((Object) this)) return false;
        if (Float.compare(this.floatingWeight, other.floatingWeight) != 0) return false;
        if (Float.compare(this.entranceWeight, other.entranceWeight) != 0) return false;
        final Object this$groups = this.groups;
        final Object other$groups = other.groups;
        if (!Objects.equals(this$groups, other$groups)) return false;
        if (this.linksRemaining != other.linksRemaining) return false;
        if (this.oneWay != other.oneWay) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {return other instanceof LinkProperties;}

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + Float.floatToIntBits(this.floatingWeight);
        result = result * PRIME + Float.floatToIntBits(this.entranceWeight);
        final Object $groups = this.groups;
        result = result * PRIME + ($groups == null ? 43 : $groups.hashCode());
        result = result * PRIME + this.linksRemaining;
        result = result * PRIME + (this.oneWay ? 79 : 97);
        return result;
    }

    public String toString() {return "LinkProperties(floatingWeight=" + this.floatingWeight + ", entranceWeight=" + this.entranceWeight + ", groups=" + this.groups + ", linksRemaining=" + this.linksRemaining + ", oneWay=" + this.oneWay + ")";}

    public LinkPropertiesBuilder toBuilder() {return new LinkPropertiesBuilder().floatingWeight(this.floatingWeight).entranceWeight(this.entranceWeight).groups(this.groups).linksRemaining(this.linksRemaining).oneWay(this.oneWay);}

    public static class LinkPropertiesBuilder {
        private float floatingWeight;
        private float entranceWeight;
        private Set<Integer> groups;
        private int linksRemaining;
        private boolean oneWay;

        LinkPropertiesBuilder() {}

        public LinkProperties.LinkPropertiesBuilder floatingWeight(float floatingWeight) {
            this.floatingWeight = floatingWeight;
            return this;
        }

        public LinkProperties.LinkPropertiesBuilder entranceWeight(float entranceWeight) {
            this.entranceWeight = entranceWeight;
            return this;
        }

        public LinkProperties.LinkPropertiesBuilder groups(Set<Integer> groups) {
            this.groups = groups;
            return this;
        }

        public LinkProperties.LinkPropertiesBuilder linksRemaining(int linksRemaining) {
            this.linksRemaining = linksRemaining;
            return this;
        }

        public LinkProperties.LinkPropertiesBuilder oneWay(boolean oneWay) {
            this.oneWay = oneWay;
            return this;
        }

        public LinkProperties build() {
            return new LinkProperties(floatingWeight, entranceWeight, groups, linksRemaining, oneWay);
        }

        public String toString() {return "LinkProperties.LinkPropertiesBuilder(floatingWeight=" + this.floatingWeight + ", entranceWeight=" + this.entranceWeight + ", groups=" + this.groups + ", linksRemaining=" + this.linksRemaining + ", oneWay=" + this.oneWay + ")";}
    }
}
