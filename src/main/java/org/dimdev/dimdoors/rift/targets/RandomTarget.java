package org.dimdev.dimdoors.rift.targets;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import org.dimdev.annotatednbt.AnnotatedNbt;
import org.dimdev.annotatednbt.Saved;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.block.entity.DetachedRiftBlockEntity;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.pockets.PocketGenerator;
import org.dimdev.dimdoors.rift.registry.LinkProperties;
import org.dimdev.dimdoors.rift.registry.Rift;
import org.dimdev.dimdoors.rift.registry.RiftRegistry;
import org.dimdev.dimdoors.util.Location;
import org.dimdev.dimdoors.util.WorldUtil;
import org.dimdev.dimdoors.util.math.MathUtil;
import org.dimdev.dimdoors.world.pocket.Pocket;
import org.dimdev.dimdoors.world.pocket.VirtualLocation;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;

public class RandomTarget extends VirtualTarget { // TODO: Split into DungeonTarget subclass
    public static final Codec<RandomTarget> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
                Codec.FLOAT.fieldOf("newRiftWeight").forGetter(target -> target.newRiftWeight),
                Codec.DOUBLE.fieldOf("weightMaximum").forGetter(location -> location.weightMaximum),
                Codec.DOUBLE.fieldOf("coordFactor").forGetter(location -> location.coordFactor),
                Codec.DOUBLE.fieldOf("positiveDepthFactor").forGetter(location -> location.positiveDepthFactor),
                Codec.DOUBLE.fieldOf("negativeDepthFactor").forGetter(location -> location.negativeDepthFactor),
                Codec.INT_STREAM.<Set<Integer>>comapFlatMap(a -> DataResult.success(a.boxed().collect(Collectors.toSet())), a -> a.stream().mapToInt(Integer::intValue)).fieldOf("acceptedGroups").forGetter(target -> target.acceptedGroups),
                Codec.BOOL.fieldOf("noLink").forGetter(target -> target.noLink),
                Codec.BOOL.fieldOf("noLinkBack").forGetter(target -> target.noLinkBack)
        ).apply(instance, RandomTarget::new);
    });

    @Saved
    protected float newRiftWeight;
    @Saved
    protected double weightMaximum;
    @Saved
    protected double coordFactor;
    @Saved
    protected double positiveDepthFactor;
    @Saved
    protected double negativeDepthFactor;
    @Saved
    protected Set<Integer> acceptedGroups;
    @Saved
    protected boolean noLink;
    @Saved
    protected boolean noLinkBack;

    public RandomTarget(float newRiftWeight, double weightMaximum, double coordFactor, double positiveDepthFactor, double negativeDepthFactor, Set<Integer> acceptedGroups, boolean noLink, boolean noLinkBack) {
        this.newRiftWeight = newRiftWeight;
        this.weightMaximum = weightMaximum;
        this.coordFactor = coordFactor;
        this.positiveDepthFactor = positiveDepthFactor;
        this.negativeDepthFactor = negativeDepthFactor;
        this.acceptedGroups = acceptedGroups;
        this.noLink = noLink;
        this.noLinkBack = noLinkBack;
    }

    public static RandomTargetBuilder builder() {
        return new RandomTargetBuilder();
    }

    @Override
    public Target receiveOther() { // TODO: Wrap rather than replace
        VirtualLocation virtualLocationHere = VirtualLocation.fromLocation(location);

        Map<Location, Float> riftWeights = new HashMap<>();
        if (newRiftWeight > 0) riftWeights.put(null, newRiftWeight);

        for (Rift otherRift : RiftRegistry.instance().getRifts()) {
            VirtualLocation otherVirtualLocation = VirtualLocation.fromLocation(otherRift.location);
            if (otherRift.properties == null) continue;
            double otherWeight = otherRift.isDetached ? otherRift.properties.floatingWeight : otherRift.properties.entranceWeight;
            if (otherWeight == 0 || Sets.intersection(acceptedGroups, otherRift.properties.groups).isEmpty()) continue;

            // Calculate the distance as sqrt((coordFactor * coordDistance)^2 + (depthFactor * depthDifference)^2)
            if (otherVirtualLocation == null || otherRift.properties.linksRemaining == 0) continue;
            double depthDifference = otherVirtualLocation.depth - virtualLocationHere.depth;
            double coordDistance = Math.sqrt(sq(otherVirtualLocation.x - virtualLocationHere.x)
                    + sq(otherVirtualLocation.z - virtualLocationHere.z));
            double depthFactor = depthDifference > 0 ? positiveDepthFactor : negativeDepthFactor;
            double distance = Math.sqrt(sq(coordFactor * coordDistance) + sq(depthFactor * depthDifference));

            // Calculate the weight as 4m/pi w/(m^2/d + d)^2. This is similar to how gravitational/electromagnetic attraction
            // works in physics (G m1 m2/d^2 and k_e m1 m2/d^2). Even though we add a depth dimension to the world, we keep
            // the weight inversly proportionally to the area of a sphere (the square of the distance) rather than a
            // hypersphere (the cube of the area) because the y coordinate does not matter for now. We use m^2/d + d
            // rather than d such that the probability near 0 tends to 0 rather than infinity. f(m^2/d) is a special case
            // of f((m^(a+1)/a)/d^a). m is the location of f's maximum. The constant 4m/pi makes it such that a newRiftWeight
            // of 1 is equivalent to having a total link weight of 1 distributed equally across all layers.
            // TODO: We might want an a larger than 1 to make the function closer to 1/d^2
            double weight = 4 * weightMaximum / Math.PI * otherWeight / sq(sq(weightMaximum) / distance + distance);
            riftWeights.put(otherRift.location, (float) weight);
        }

        Location selectedLink;
        if (riftWeights.size() == 0) {
            if (newRiftWeight == -1) {
                selectedLink = null;
            } else {
                return null;
            }
        } else {
            selectedLink = MathUtil.weightedRandom(riftWeights);
        }

        // Check if we have to generate a new rift
        if (selectedLink == null) {
//             Randomly select a distance from the distribution f(x) = 1/(m^2/x + x)^2. We use the same distribution as the
//             weighting function. The idea is that there is some kind of "field" formed by the rift, with the field's
//             intensity decreasing proportionally to the area of the sphere. The product of the area of the sphere and
//             the intesity of the field at that radius is therefore constant, so the infinitsimal weight of any layer
//             will be constant and its weight will therefore simply be the original weight function at that value.
//
//             The inverse of the normalized distribution function can be generated by this Mathematica code:
//             distribution = 1/(m^2/x + x)^2
//             totalDistributionIntegral = Integrate[distribution, {x, 0, Infinity}, Assumptions -> m > 0];
//             normalizedDistribution = Simplify[distribution/totalDistributionIntegral]
//             cummulativeNormalizedDistribution = Simplify[Integrate[normalizedDistribution, x] - (Integrate[normalizedDistribution, x] /. x -> 0)]
//
//             m = 1; (*Doesn't matter which m you use, it's proportional*)
//             table = Table[{r, x /. FindRoot[cummulativeNormalizedDistribution - r, {x, m}]}, {r, 0.01, 0.99, 0.01}];
//             fit = NonlinearModelFit[table, m (2 Tan[Pi/2 x] + a Log[x] Sqrt[x]), {a}, x]
//             Show[ListPlot[table], Plot[fit[x], {x, 0, 1}]]
//             Clear[m];
//             inverseCummulativeNormalizedDistribution = Normal[fit]
            double r = Math.random();
            double distance = weightMaximum * (2 * Math.tan(Math.PI / 2 * r) - 0.5578284481138029 * Math.sqrt(r) * Math.log(r));

            // Randomly split the vector into depth, x, and z components
            // TODO: Two random angles isn't a uniformly random direction! Use random vector, normalize, add depth offset, scale xz, scale depth.
            double theta = Math.random() * Math.PI; // Angle between vector and xz plane
            double phi = Math.random() * Math.PI;  // Angle of the vector on the xz plane relative to the x axis
            double depth = distance * Math.sin(theta);
            depth /= depth > 0 ? positiveDepthFactor : negativeDepthFactor;
            double x = Math.cos(theta) * Math.cos(phi) * distance / coordFactor;
            double z = Math.cos(theta) * Math.sin(phi) * distance / coordFactor;
            VirtualLocation virtualLocation = new VirtualLocation(virtualLocationHere.world,
                    virtualLocationHere.x + (int) Math.round(x),
                    virtualLocationHere.z + (int) Math.round(z),
                    virtualLocationHere.depth + (int) Math.round(depth));

            if (virtualLocation.depth <= 0) {
                // This will lead to the overworld
                World world = WorldUtil.getWorld(virtualLocation.world);
                BlockPos pos = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, new BlockPos(virtualLocation.x, 0, virtualLocation.z));
                if (pos.getY() == -1) {
                    // No blocks at that XZ (hole in bedrock)
                    pos = new BlockPos(virtualLocation.x, 0, virtualLocation.z);
                }
                world.setBlockState(pos, ModBlocks.DETACHED_RIFT.getDefaultState());

                RiftBlockEntity thisRift = (RiftBlockEntity) location.getBlockEntity();
                DetachedRiftBlockEntity riftEntity = (DetachedRiftBlockEntity) world.getBlockEntity(pos);
                // TODO: Should the rift not be configured like the other link
                riftEntity.setProperties(thisRift.getProperties().toBuilder().linksRemaining(1).build());

                if (!noLinkBack && !riftEntity.getProperties().oneWay)
                    linkRifts(new Location((ServerWorld) world, pos), location);
                if (!noLink) linkRifts(location, new Location((ServerWorld) world, pos));
                return riftEntity.as(Targets.ENTITY);
            } else {
                // Make a new dungeon pocket
                RiftBlockEntity thisRift = (RiftBlockEntity) location.getBlockEntity();
                LinkProperties newLink = thisRift.getProperties() != null ? thisRift.getProperties().toBuilder().linksRemaining(0).build() : null;
                Pocket pocket = PocketGenerator.generateDungeonPocket(virtualLocation, new GlobalReference(!noLinkBack ? location : null), newLink); // TODO make the generated dungeon of the same type, but in the overworld

                // Link the rift if necessary and teleport the entity
                if (!noLink) linkRifts(location, RiftRegistry.instance().getPocketEntrance(pocket));
                return (Target) RiftRegistry.instance().getPocketEntrance(pocket).getBlockEntity();
            }
        } else {
            // An existing rift was selected
            RiftBlockEntity riftEntity = (RiftBlockEntity) selectedLink.getBlockEntity();

            // Link the rifts if necessary and teleport the entity
            if (!noLink) linkRifts(location, selectedLink);
            if (!noLinkBack && !riftEntity.getProperties().oneWay) linkRifts(selectedLink, location);
            return riftEntity;
        }
    }

    private static void linkRifts(Location from, Location to) {
        RiftBlockEntity BlockEntityFrom = (RiftBlockEntity) from.getBlockEntity();
        RiftBlockEntity BlockEntityTo = (RiftBlockEntity) to.getBlockEntity();
        BlockEntityFrom.setDestination(RiftReference.tryMakeLocal(from, to));
        BlockEntityFrom.markDirty();
        if (BlockEntityTo.getProperties() != null) {
            BlockEntityTo.getProperties().linksRemaining--;
            BlockEntityTo.updateProperties();
            BlockEntityTo.markDirty();
        }
    }

    private double sq(double a) {
        return a * a;
    }

    public float getNewRiftWeight() {
        return this.newRiftWeight;
    }

    public double getWeightMaximum() {
        return this.weightMaximum;
    }

    public double getCoordFactor() {
        return this.coordFactor;
    }

    public double getPositiveDepthFactor() {
        return this.positiveDepthFactor;
    }

    public double getNegativeDepthFactor() {
        return this.negativeDepthFactor;
    }

    public Set<Integer> getAcceptedGroups() {
        return this.acceptedGroups;
    }

    public boolean isNoLink() {
        return this.noLink;
    }

    public boolean isNoLinkBack() {
        return this.noLinkBack;
    }

    @Override
    public VirtualTargetType<? extends VirtualTarget> getType() {
        return VirtualTargetType.AVAILABLE_LINK;
    }

    public static class RandomTargetBuilder {
        private float newRiftWeight;
        private double weightMaximum;
        private double coordFactor;
        private double positiveDepthFactor;
        private double negativeDepthFactor;
        private Set<Integer> acceptedGroups;
        private boolean noLink;
        private boolean noLinkBack;

        RandomTargetBuilder() {
        }

        public RandomTarget.RandomTargetBuilder newRiftWeight(float newRiftWeight) {
            this.newRiftWeight = newRiftWeight;
            return this;
        }

        public RandomTarget.RandomTargetBuilder weightMaximum(double weightMaximum) {
            this.weightMaximum = weightMaximum;
            return this;
        }

        public RandomTarget.RandomTargetBuilder coordFactor(double coordFactor) {
            this.coordFactor = coordFactor;
            return this;
        }

        public RandomTarget.RandomTargetBuilder positiveDepthFactor(double positiveDepthFactor) {
            this.positiveDepthFactor = positiveDepthFactor;
            return this;
        }

        public RandomTarget.RandomTargetBuilder negativeDepthFactor(double negativeDepthFactor) {
            this.negativeDepthFactor = negativeDepthFactor;
            return this;
        }

        public RandomTarget.RandomTargetBuilder acceptedGroups(Set<Integer> acceptedGroups) {
            this.acceptedGroups = acceptedGroups;
            return this;
        }

        public RandomTarget.RandomTargetBuilder noLink(boolean noLink) {
            this.noLink = noLink;
            return this;
        }

        public RandomTarget.RandomTargetBuilder noLinkBack(boolean noLinkBack) {
            this.noLinkBack = noLinkBack;
            return this;
        }

        public RandomTarget build() {
            return new RandomTarget(newRiftWeight, weightMaximum, coordFactor, positiveDepthFactor, negativeDepthFactor, acceptedGroups, noLink, noLinkBack);
        }
    }
}
