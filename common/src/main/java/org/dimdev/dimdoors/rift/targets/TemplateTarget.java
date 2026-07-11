package org.dimdev.dimdoors.rift.targets;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.ModRegistryKeys;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.pockets.PocketGenerator;
import org.dimdev.dimdoors.pockets.virtual.VirtualPocket;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;
import org.dimdev.dimdoors.world.pocket.VirtualLocation;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

public class TemplateTarget extends RestoringTarget<TemplateTarget> {
    public static final MapCodec<TemplateTarget> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            VirtualPocket.HOLDER_CODEC.fieldOf("template").forGetter(a -> a.template)).apply(instance, TemplateTarget::new));

    private final Holder<VirtualPocket> template;

    public TemplateTarget(Holder<VirtualPocket> template) {
        this.template = template;
    }

    @Override
    public Location makeLinkTarget() {
        VirtualLocation riftVirtualLocation = VirtualLocation.fromLocation(this.location);
        VirtualLocation newVirtualLocation;
        int depth = riftVirtualLocation.getDepth() + 1;
        newVirtualLocation = new VirtualLocation(riftVirtualLocation.getWorld(), riftVirtualLocation.getX(), riftVirtualLocation.getZ(), depth);
        Pocket<?, ?> pocket = PocketGenerator.generateFromVirtualPocket(DimensionalDoors.getWorld(ModDimensions.DUNGEON), template, newVirtualLocation, this.location.asTarget(), null);

        return DimensionalRegistry.getRiftRegistry().getPocketEntrance(pocket);
    }

    @Override
    public TemplateTarget copy() {
        return new TemplateTarget(template);
    }

    @Override
    public VirtualTargetType<TemplateTarget> getType() {
        return VirtualTargetType.TEMPLATE;
    }
}