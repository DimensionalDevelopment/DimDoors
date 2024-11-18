package org.dimdev.dimdoors.rift.targets;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.pockets.PocketGenerator;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;
import org.dimdev.dimdoors.world.pocket.VirtualLocation;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

public class TemplateTarget extends WrappedDestinationTarget {
    public static final MapCodec<TemplateTarget> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(VirtualTarget.CODEC.optionalFieldOf("wrappedDestination", null).forGetter(a -> a.wrappedDestination),
                ResourceLocation.CODEC.fieldOf("template").forGetter(a -> a.template)).apply(instance, TemplateTarget::new));

    private final ResourceLocation template;

    public TemplateTarget(VirtualTarget wrappedDestination, ResourceLocation template) {
        super(wrappedDestination);
        this.template = template;
    }

    public TemplateTarget(ResourceLocation template) {
        this(null, template);
    }

    @Override
    public Location makeLinkTarget() {
        VirtualLocation riftVirtualLocation = VirtualLocation.fromLocation(this.location);
        VirtualLocation newVirtualLocation;
        int depth = riftVirtualLocation.getDepth() + 1;
        newVirtualLocation = new VirtualLocation(riftVirtualLocation.getWorld(), riftVirtualLocation.getX(), riftVirtualLocation.getZ(), depth);
        Pocket pocket = PocketGenerator.generateFromVirtualPocket(DimensionalDoors.getWorld(ModDimensions.DUNGEON), template, newVirtualLocation, new GlobalReference(this.location), null);

        return DimensionalRegistry.getRiftRegistry().getPocketEntrance(pocket);
    }

    @Override
    public VirtualTarget copy() {
        return new TemplateTarget(wrappedDestination, template);
    }
    @Override
    public VirtualTargetType<TemplateTarget> getType() {
        return VirtualTargetType.TEMPLATE.get();
    }
}
