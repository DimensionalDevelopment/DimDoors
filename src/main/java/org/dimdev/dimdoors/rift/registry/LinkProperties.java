package org.dimdev.dimdoors.rift.registry;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.math.NumberUtils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.github.cottonmc.cotton.gui.widget.WBox;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WTextField;
import io.github.cottonmc.cotton.gui.widget.WToggleButton;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import io.github.cottonmc.cotton.gui.widget.data.Axis;
import org.dimdev.dimdoors.util.Codecs;

public class LinkProperties {
    public static Codec<LinkProperties> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
                Codec.FLOAT
                        .fieldOf("floatingWeight")
                        .forGetter(
                                properties ->
                                        properties.
                                                floatingWeight),
                Codec.FLOAT
                        .fieldOf("entranceWeight")
                        .forGetter(properties -> properties.entranceWeight),
                Codecs.INT_SET
                        .fieldOf("groups")
                        .forGetter(properties -> properties.groups),
                Codec.INT
                        .fieldOf("linksRemaining")
                        .forGetter(properties -> properties.linksRemaining),
                Codec.BOOL
                        .fieldOf("oneWay")
                        .forGetter(properties -> properties.oneWay)
        ).apply(instance, LinkProperties::new);
    });

    public static final LinkProperties NONE = new LinkProperties();

    public float floatingWeight; // TODO: depend on rift properties (ex. size, stability, or maybe a getWeightFactor method) rather than rift type

    public float entranceWeight;
    public Set<Integer> groups = new HashSet<>();
    public int linksRemaining;
    public boolean oneWay;

    public LinkProperties(float floatingWeight, float entranceWeight, Set<Integer> groups, int linksRemaining, boolean oneWay) {
        this.floatingWeight = floatingWeight;
        this.entranceWeight = entranceWeight;
        this.groups = groups;
        this.linksRemaining = linksRemaining;
        this.oneWay = oneWay;
    }

    private LinkProperties() {
    }

    public static LinkPropertiesBuilder builder() {
        return new LinkPropertiesBuilder();
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof LinkProperties)) return false;
        final LinkProperties other = (LinkProperties) o;
        if (!other.canEqual(this)) return false;
        if (Float.compare(this.floatingWeight, other.floatingWeight) != 0) return false;
        if (Float.compare(this.entranceWeight, other.entranceWeight) != 0) return false;
        final Object this$groups = this.groups;
        final Object other$groups = other.groups;
        if (!Objects.equals(this$groups, other$groups)) return false;
        if (this.linksRemaining != other.linksRemaining) return false;
        return this.oneWay == other.oneWay;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof LinkProperties;
    }

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

    public String toString() {
        return "LinkProperties(floatingWeight=" + this.floatingWeight + ", entranceWeight=" + this.entranceWeight + ", groups=" + this.groups + ", linksRemaining=" + this.linksRemaining + ", oneWay=" + this.oneWay + ")";
    }

    public LinkPropertiesBuilder toBuilder() {
        return new LinkPropertiesBuilder().floatingWeight(this.floatingWeight).entranceWeight(this.entranceWeight).groups(this.groups).linksRemaining(this.linksRemaining).oneWay(this.oneWay);
    }

    public static CompoundTag toTag(LinkProperties properties) {
        net.minecraft.nbt.CompoundTag tag = new net.minecraft.nbt.CompoundTag();
        tag.putFloat("floatingWeight", properties.floatingWeight);
        tag.putFloat("entranceWeight", properties.entranceWeight);
        tag.putIntArray("groups", new ArrayList<>(properties.groups));
        tag.putInt("linksRemaining", properties.linksRemaining);
        tag.putBoolean("oneWay", properties.oneWay);
        return tag;
    }

    public static LinkProperties fromTag(CompoundTag tag) {
        LinkProperties properties = new LinkProperties();
        properties.floatingWeight = tag.getFloat("floatingWeight");
        properties.entranceWeight = tag.getFloat("entranceWeight");
        properties.groups = Arrays.stream(tag.getIntArray("groups")).boxed().collect(Collectors.toSet());
        properties.linksRemaining = tag.getInt("linksRemaining");
        properties.oneWay = tag.getBoolean("oneWay");
        return properties;
    }

    public static class LinkPropertiesBuilder {
        private float floatingWeight;
        private float entranceWeight;
        private Set<Integer> groups;
        private int linksRemaining;
        private boolean oneWay;

        LinkPropertiesBuilder() {
        }

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
            return new LinkProperties(this.floatingWeight, this.entranceWeight, this.groups, this.linksRemaining, this.oneWay);
        }

        public String toString() {
            return "LinkProperties.LinkPropertiesBuilder(floatingWeight=" + this.floatingWeight + ", entranceWeight=" + this.entranceWeight + ", groups=" + this.groups + ", linksRemaining=" + this.linksRemaining + ", oneWay=" + this.oneWay + ")";
        }
    }

    public WWidget widget() {
        WBox root = new WBox(Axis.VERTICAL);
        root.add(new WLabel("Rift Data:"));

        WBox tab = new WBox(Axis.HORIZONTAL);
        tab.add(new WLabel("  "));

        WBox main = new WBox(Axis.VERTICAL);

        WBox box = new WBox(Axis.HORIZONTAL);
        box.add(new WLabel("Floating Weight:"));

        WTextField floatingWeightText = new WTextField().setChangedListener(a -> {
            if(NumberUtils.isParsable(a)) floatingWeight = NumberUtils.toFloat(a);
        });
        floatingWeightText.setText(String.valueOf(floatingWeight));
        box.add(floatingWeightText);

        WToggleButton oneWayButton = new WToggleButton(Text.of("One Way:")).setOnToggle(oneWay -> this.oneWay = oneWay);
        oneWayButton.setToggle(oneWay);

        main.add(box);
        main.add(oneWayButton);

        tab.add(main);

        root.add(tab);

        return root;
    }
}
