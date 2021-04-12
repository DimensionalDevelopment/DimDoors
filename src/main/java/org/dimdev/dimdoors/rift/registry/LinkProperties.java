package org.dimdev.dimdoors.rift.registry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.nbt.NbtCompound;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class LinkProperties {
	public static final LinkProperties NONE = LinkProperties.builder().build();

	public float floatingWeight; // TODO: depend on rift properties (ex. size, stability, or maybe a getWeightFactor method) rather than rift type

	private final float entranceWeight;
	private final Set<Integer> groups;
	private final int linksRemaining;
	private final boolean oneWay;

	public LinkProperties(float floatingWeight, float entranceWeight, Set<Integer> groups, int linksRemaining, boolean oneWay) {
		this.floatingWeight = floatingWeight;
		this.entranceWeight = entranceWeight;
		this.groups = groups;
		this.linksRemaining = linksRemaining;
		this.oneWay = oneWay;
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
		if (!Objects.equals(this.groups, other.groups)) return false;
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

	public static NbtCompound toNbt(LinkProperties properties) {
		net.minecraft.nbt.NbtCompound nbt = new net.minecraft.nbt.NbtCompound();
		nbt.putFloat("floatingWeight", properties.floatingWeight);
		nbt.putFloat("entranceWeight", properties.entranceWeight);
		nbt.putIntArray("groups", new ArrayList<>(properties.groups));
		nbt.putInt("linksRemaining", properties.linksRemaining);
		nbt.putBoolean("oneWay", properties.oneWay);
		return nbt;
	}

	public static LinkProperties fromNbt(NbtCompound nbt) {
		return LinkProperties.builder()
				.floatingWeight(nbt.getFloat("floatingWeight"))
				.entranceWeight(nbt.getFloat("entranceWeight"))
				.groups(Arrays.stream(nbt.getIntArray("groups")).boxed().collect(Collectors.toSet()))
				.linksRemaining(nbt.getInt("linksRemaining"))
				.oneWay(nbt.getBoolean("oneWay"))
				.build();
	}

	public float getEntranceWeight() {
		return entranceWeight;
	}

	public Set<Integer> getGroups() {
		return groups;
	}

	public int getLinksRemaining() {
		return linksRemaining;
	}

	public LinkProperties withLinksRemaining(int linksRemaining) {
		return toBuilder().linksRemaining(linksRemaining).build();
	}

	public boolean isOneWay() {
		return oneWay;
	}

	public static class LinkPropertiesBuilder {
		private float floatingWeight;
		private float entranceWeight;
		private Set<Integer> groups = Collections.emptySet();
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

		@Override
		public String toString() {
			return new ToStringBuilder(this)
					.append("floatingWeight", floatingWeight)
					.append("entranceWeight", entranceWeight)
					.append("groups", groups)
					.append("linksRemaining", linksRemaining)
					.append("oneWay", oneWay)
					.toString();
		}
	}

//	public WWidget widget() {
//		WBox root = new WBox(Axis.VERTICAL);
//		root.add(new WLabel("Rift Data:"));
//
//		WBox tab = new WBox(Axis.HORIZONTAL);
//		tab.add(new WLabel("  "));
//
//		WBox main = new WBox(Axis.VERTICAL);
//
//		WBox box = new WBox(Axis.HORIZONTAL);
//		box.add(new WLabel("Floating Weight:"));
//
//		WTextField floatingWeightText = new WTextField().setChangedListener(a -> {
//			try {
//				this.floatingWeight = Float.parseFloat(a);
//			} catch (NumberFormatException ignored) {
//			}
//		});
//		floatingWeightText.setText(String.valueOf(this.floatingWeight));
//		box.add(floatingWeightText);
//
//		WToggleButton oneWayButton = new WToggleButton(Text.of("One Way:")).setOnToggle(oneWay -> this.oneWay = oneWay);
//		oneWayButton.setToggle(this.oneWay);
//
//		main.add(box);
//		main.add(oneWayButton);
//
//		tab.add(main);
//
//		root.add(tab);
//
//		return root;
//	}
}
