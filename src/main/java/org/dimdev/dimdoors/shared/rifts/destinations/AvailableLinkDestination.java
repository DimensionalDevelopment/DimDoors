package org.dimdev.dimdoors.shared.rifts.destinations;

import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.dimdev.annotatednbt.NBTSerializable;
import org.dimdev.annotatednbt.Saved;
import org.dimdev.ddutils.Location;
import org.dimdev.ddutils.RotatedLocation;
import org.dimdev.ddutils.WorldUtils;
import org.dimdev.ddutils.math.MathUtils;
import org.dimdev.ddutils.nbt.NBTUtils;
import org.dimdev.pocketlib.VirtualLocation;
import org.dimdev.dimdoors.shared.blocks.ModBlocks;
import org.dimdev.pocketlib.Pocket;
import org.dimdev.dimdoors.shared.pockets.PocketGenerator;
import org.dimdev.dimdoors.shared.rifts.*;
import org.dimdev.dimdoors.shared.rifts.registry.LinkProperties;
import org.dimdev.dimdoors.shared.rifts.registry.Rift;
import org.dimdev.dimdoors.shared.rifts.registry.RiftRegistry;
import org.dimdev.dimdoors.shared.tileentities.TileEntityFloatingRift;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Getter @AllArgsConstructor @Builder(toBuilder = true) @ToString
@NBTSerializable public class AvailableLinkDestination extends RiftDestination {
    @Saved protected float newRiftWeight;
    @Saved protected double weightMaximum;
    @Saved protected double coordFactor;
    @Saved protected double positiveDepthFactor;
    @Saved protected double negativeDepthFactor;
    @Saved protected Set<Integer> acceptedGroups;
    @Saved protected boolean noLink;
    @Saved protected boolean noLinkBack;
    // TODO: better depth calculation

    public AvailableLinkDestination() {}

    @Override public void readFromNBT(NBTTagCompound nbt) { super.readFromNBT(nbt); NBTUtils.readFromNBT(this, nbt); }
    @Override public NBTTagCompound writeToNBT(NBTTagCompound nbt) { nbt = super.writeToNBT(nbt); return NBTUtils.writeToNBT(this, nbt); }

    @Override
    public boolean teleport(RotatedLocation location, Entity entity) {
        VirtualLocation virtualLocationHere = VirtualLocation.fromLocation(location.getLocation());

        Map<Location, Float> riftWeights = new HashMap<>();
        if (newRiftWeight > 0) riftWeights.put(null, newRiftWeight);

        for (Rift otherRift : RiftRegistry.instance().getRifts()) {
            VirtualLocation otherVirtualLocation = VirtualLocation.fromLocation(otherRift.location);
            if (otherRift.properties == null) continue;
            double otherWeight = otherRift.isFloating ? otherRift.properties.floatingWeight : otherRift.properties.entranceWeight;
            if (otherWeight == 0 || Sets.intersection(acceptedGroups, otherRift.properties.groups).isEmpty()) continue;

            // Calculate the distance as sqrt((coordFactor * coordDistance)^2 + (depthFactor * depthDifference)^2)
            if (otherVirtualLocation == null || otherRift.properties.linksRemaining == 0) continue;
            double depthDifference = otherVirtualLocation.getDepth() - virtualLocationHere.getDepth();
            double coordDistance = Math.sqrt(sq(otherVirtualLocation.getX() - virtualLocationHere.getX())
                                             + sq(otherVirtualLocation.getZ() - virtualLocationHere.getZ()));
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
                return false;
            }
        } else {
            selectedLink = MathUtils.weightedRandom(riftWeights);
        }

        // Check if we have to generate a new rift
        if (selectedLink == null) {
            // Randomly select a distance from the distribution f(x) = 1/(m^2/x + x)^2. We use the same distribution as the
            // weighting function. The idea is that there is some kind of "field" formed by the rift, with the field's
            // intensity decreasing proportionally to the area of the sphere. The product of the area of the sphere and
            // the intesity of the field at that radius is therefore constant, so the infinitsimal weight of any layer
            // will be constant and its weight will therefore simply be the original weight function at that value.
            //
            // The inverse of the normalized distribution function can be generated by this Mathematica code:
            // distribution = 1/(m^2/x + x)^2
            // totalDistributionIntegral = Integrate[distribution, {x, 0, Infinity}, Assumptions -> m > 0];
            // normalizedDistribution = Simplify[distribution/totalDistributionIntegral]
            // cummulativeNormalizedDistribution = Simplify[Integrate[normalizedDistribution, x] - (Integrate[normalizedDistribution, x] /. x -> 0)]
            //
            // m = 1; (*Doesn't matter which m you use, it's proportional*)
            // table = Table[{r, x /. FindRoot[cummulativeNormalizedDistribution - r, {x, m}]}, {r, 0.01, 0.99, 0.01}];
            // fit = NonlinearModelFit[table, m (2 Tan[Pi/2 x] + a Log[x] Sqrt[x]), {a}, x]
            // Show[ListPlot[table], Plot[fit[x], {x, 0, 1}]]
            // Clear[m];
            // inverseCummulativeNormalizedDistribution = Normal[fit]
            double r = Math.random();
            double distance = weightMaximum * (2 * Math.tan(Math.PI / 2 * r) - 0.5578284481138029 * Math.sqrt(r) * Math.log(r));

            // Randomly split the vector into depth, x, and z components
            double theta = Math.random() * Math.PI; // Angle between vector and xz plane
            double phi = Math.random() * Math.PI;  // Angle of the vector on the xz plane relative to the x axis
            double depth = distance * Math.sin(theta);
            depth /= depth > 0 ? positiveDepthFactor : negativeDepthFactor;
            double x = Math.cos(theta) * Math.cos(phi) * distance / coordFactor;
            double z = Math.cos(theta) * Math.sin(phi) * distance / coordFactor;
            VirtualLocation virtualLocation = new VirtualLocation(virtualLocationHere.getDim(),
                    virtualLocationHere.getX() + (int) Math.round(x),
                    virtualLocationHere.getZ() + (int) Math.round(z),
                    virtualLocationHere.getDepth() + (int) Math.round(depth));

            if (virtualLocation.getDepth() <= 0) {
                // This will lead to the overworld
                World world = WorldUtils.getWorld(virtualLocation.getDim());
                BlockPos pos = world.getTopSolidOrLiquidBlock(new BlockPos(virtualLocation.getX(), 0, virtualLocation.getZ()));
                if (pos.getY() == -1) {
                    // No blocks at that XZ (hole in bedrock)
                    pos = new BlockPos(virtualLocation.getX(), 0, virtualLocation.getZ());
                }
                world.setBlockState(pos, ModBlocks.RIFT.getDefaultState());

                TileEntityRift thisRift = (TileEntityRift) location.getLocation().getTileEntity();
                TileEntityFloatingRift riftEntity = (TileEntityFloatingRift) world.getTileEntity(pos);
                // TODO: Should the rift not be configured like the other link
                riftEntity.setProperties(thisRift.getProperties().toBuilder().linksRemaining(1).build());

                if (!noLinkBack && !riftEntity.getProperties().oneWay) linkRifts(new Location(world, pos), location.getLocation());
                if (!noLink) linkRifts(location.getLocation(), new Location(world, pos));
                riftEntity.teleportTo(entity, thisRift.getYaw(), thisRift.getPitch());
            } else {
                // Make a new dungeon pocket
                TileEntityRift thisRift = (TileEntityRift) location.getLocation().getTileEntity();
                LinkProperties newLink = thisRift.getProperties() != null ? thisRift.getProperties().toBuilder().linksRemaining(0).build() : null;
                Pocket pocket = PocketGenerator.generateDungeonPocket(virtualLocation, new GlobalDestination(!noLinkBack ? location.getLocation() : null), newLink); // TODO make the generated dungeon of the same type, but in the overworld

                // Link the rift if necessary and teleport the entity
                if (!noLink) linkRifts(location.getLocation(), RiftRegistry.instance().getPocketEntrance(pocket));
                ((TileEntityRift) RiftRegistry.instance().getPocketEntrance(pocket).getTileEntity()).teleportTo(entity, location.getYaw(), location.getPitch());
            }
        } else {
            // An existing rift was selected
            TileEntityRift riftEntity = (TileEntityRift) selectedLink.getTileEntity();

            // Link the rifts if necessary and teleport the entity
            if (!noLink) linkRifts(location.getLocation(), selectedLink);
            if (!noLinkBack && !riftEntity.getProperties().oneWay) linkRifts(selectedLink, location.getLocation());
            riftEntity.teleportTo(entity, location.getYaw(), location.getPitch());
        }
        return true;
    }

    private static void linkRifts(Location from, Location to) {
        TileEntityRift tileEntityFrom = (TileEntityRift) from.getTileEntity();
        TileEntityRift tileEntityTo = (TileEntityRift) to.getTileEntity();
        tileEntityFrom.setDestination(new GlobalDestination(to)); // TODO: local if possible
        tileEntityFrom.markDirty();
        if (tileEntityTo.getProperties() != null) {
            tileEntityTo.getProperties().linksRemaining--;
            tileEntityTo.updateProperties();
            tileEntityTo.markDirty();
        }
    }

    private double sq(double a) { return a * a; }
}
