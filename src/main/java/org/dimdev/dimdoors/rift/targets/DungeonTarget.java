package org.dimdev.dimdoors.rift.targets;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.dimdev.dimdoors.api.rift.target.Target;
import org.dimdev.dimdoors.pockets.PocketGenerator;
import org.dimdev.dimdoors.rift.registry.LinkProperties;
import org.dimdev.dimdoors.world.pocket.VirtualLocation;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public class DungeonTarget extends RandomTarget {
	private final Identifier dungeonGroup;

	public DungeonTarget(float newRiftWeight, double weightMaximum, double coordFactor, double positiveDepthFactor, double negativeDepthFactor, Set<Integer> acceptedGroups, boolean noLink, boolean noLinkBack, Identifier dungeonGroup) {
		super(newRiftWeight, weightMaximum, coordFactor, positiveDepthFactor, negativeDepthFactor, acceptedGroups, noLink, noLinkBack);
		this.dungeonGroup = dungeonGroup;
	}

	@Override
	protected Pocket generatePocket(VirtualLocation location, GlobalReference linkTo, LinkProperties props) {
		return PocketGenerator.generateDungeonPocketV2(location, linkTo, props, this.dungeonGroup);
	}

	public static NbtCompound toNbt(DungeonTarget target) {
		NbtCompound nbt = RandomTarget.toNbt(target);

		nbt.putString("dungeonGroup", target.dungeonGroup.toString());

		return nbt;
	}

	public static DungeonTargetBuilder builder() {
		return new DungeonTargetBuilder();
	}

	public static DungeonTarget fromNbt(NbtCompound nbt) {
		return new DungeonTarget(
				nbt.getFloat("newRiftWeight"),
				nbt.getDouble("weightMaximum"),
				nbt.getDouble("coordFactor"),
				nbt.getDouble("positiveDepthFactor"),
				nbt.getDouble("negativeDepthFactor"),
				Arrays.stream(nbt.getIntArray("acceptedGroups")).boxed().collect(Collectors.toSet()),
				nbt.getBoolean("noLink"),
				nbt.getBoolean("noLinkBack"),
				new Identifier(nbt.getString("dungeonGroup"))
		);
	}

	@Override
	public VirtualTargetType<? extends VirtualTarget> getType() {
		return VirtualTargetType.DUNGEON;
	}

	@Override
	public Target receiveOther() {
		return super.receiveOther();
	}

	public static class DungeonTargetBuilder extends RandomTarget.RandomTargetBuilder {
		private Identifier dungeonGroup = PocketGenerator.ALL_DUNGEONS;

		DungeonTargetBuilder() {
		}

		public void dungeonGroup(Identifier dungeonGroup) {
			this.dungeonGroup = dungeonGroup;
		}

		@Override
		public DungeonTargetBuilder newRiftWeight(float newRiftWeight) {
			return (DungeonTargetBuilder) super.newRiftWeight(newRiftWeight);
		}

		@Override
		public DungeonTargetBuilder weightMaximum(double weightMaximum) {
			return (DungeonTargetBuilder) super.weightMaximum(weightMaximum);
		}

		@Override
		public DungeonTargetBuilder coordFactor(double coordFactor) {
			return (DungeonTargetBuilder) super.coordFactor(coordFactor);
		}

		@Override
		public DungeonTargetBuilder positiveDepthFactor(double positiveDepthFactor) {
			return (DungeonTargetBuilder) super.positiveDepthFactor(positiveDepthFactor);
		}

		@Override
		public DungeonTargetBuilder negativeDepthFactor(double negativeDepthFactor) {
			return (DungeonTargetBuilder) super.negativeDepthFactor(negativeDepthFactor);
		}

		@Override
		public DungeonTargetBuilder acceptedGroups(Set<Integer> acceptedGroups) {
			return (DungeonTargetBuilder) super.acceptedGroups(acceptedGroups);
		}

		@Override
		public DungeonTargetBuilder noLink(boolean noLink) {
			return (DungeonTargetBuilder) super.noLink(noLink);
		}

		@Override
		public DungeonTargetBuilder noLinkBack(boolean noLinkBack) {
			return (DungeonTargetBuilder) super.noLinkBack(noLinkBack);
		}

		@Override
		public DungeonTarget build() {
			return new DungeonTarget(this.newRiftWeight, this.weightMaximum, this.coordFactor, this.positiveDepthFactor, this.negativeDepthFactor, this.acceptedGroups, this.noLink, this.noLinkBack, this.dungeonGroup);
		}
	}
}
