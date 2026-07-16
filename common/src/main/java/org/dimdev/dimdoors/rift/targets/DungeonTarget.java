package org.dimdev.dimdoors.rift.targets;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceKey;
import org.dimdev.dimdoors.ModRegistryKeys;
import org.dimdev.dimdoors.pockets.PocketGenerator;
import org.dimdev.dimdoors.pockets.virtual.VirtualPocket;
import org.dimdev.dimdoors.rift.registry.LinkProperties;
import org.dimdev.dimdoors.world.pocket.VirtualLocation;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

import java.util.Set;

import static org.dimdev.limlib.api.util.Products.and;

public class DungeonTarget extends RandomTarget<DungeonTarget> {
    public static final MapCodec<DungeonTarget> CODEC = RecordCodecBuilder.mapCodec(instance -> and(common(instance), ResourceKey.codec(ModRegistryKeys.POCKET_GROUPS).fieldOf("dungeonGroup").forGetter(a -> a.dungeonGroup)).apply(instance, DungeonTarget::new));

    private final ResourceKey<VirtualPocket> dungeonGroup;

    public DungeonTarget(float newRiftWeight, double weightMaximum, double coordFactor, double positiveDepthFactor, double negativeDepthFactor, Set<Integer> acceptedGroups, boolean noLink, boolean noLinkBack, ResourceKey<VirtualPocket> dungeonGroup) {
        super(newRiftWeight, weightMaximum, coordFactor, positiveDepthFactor, negativeDepthFactor, acceptedGroups, noLink, noLinkBack);
        this.dungeonGroup = dungeonGroup;
    }

    @Override
    protected Pocket<?, ?> generatePocket(VirtualLocation location, VirtualTarget<?> linkTo, LinkProperties props) {
        return PocketGenerator.generateDungeonPocketV2(location, linkTo, props, this.dungeonGroup);
    }

    @Override
    public DungeonTarget copy() {
        return new DungeonTarget(getNewRiftWeight(), getWeightMaximum(), getCoordFactor(), getPositiveDepthFactor(), getNegativeDepthFactor(), getAcceptedGroups(), isNoLink(), isNoLinkBack(), dungeonGroup);
    }

    public static DungeonTargetBuilder builder() {
        return new DungeonTargetBuilder();
    }

    @Override
    public VirtualTargetType<DungeonTarget> getType() {
        return VirtualTargetType.DUNGEON;
    }

    public static class DungeonTargetBuilder extends RandomTargetBuilder<DungeonTarget, DungeonTargetBuilder> {
        private ResourceKey<VirtualPocket> dungeonGroup = PocketGenerator.ALL_DUNGEONS;

        DungeonTargetBuilder() {
        }

        public DungeonTargetBuilder dungeonGroup(ResourceKey<VirtualPocket> dungeonGroup) {
            this.dungeonGroup = dungeonGroup;
            return this;
        }

        @Override
        public DungeonTarget build() {
            return new DungeonTarget(this.newRiftWeight, this.weightMaximum, this.coordFactor, this.positiveDepthFactor, this.negativeDepthFactor, this.acceptedGroups, this.noLink, this.noLinkBack, this.dungeonGroup);
        }
    }
}
