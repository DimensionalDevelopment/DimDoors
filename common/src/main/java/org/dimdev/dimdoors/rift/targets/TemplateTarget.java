package org.dimdev.dimdoors.rift.targets;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.pockets.PocketGenerator;
import org.dimdev.dimdoors.rift.registry.LinkProperties;
import org.dimdev.dimdoors.forge.world.ModDimensions;
import org.dimdev.dimdoors.forge.world.level.registry.DimensionalRegistry;
import org.dimdev.dimdoors.forge.world.pocket.VirtualLocation;
import org.dimdev.dimdoors.forge.world.pocket.type.Pocket;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class TemplateTarget extends WrappedDestinationTarget {
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

    public static CompoundTag toNbt(TemplateTarget target) {
        CompoundTag nbt = WrappedDestinationTarget.toNbt(target);

        nbt.putString("template", target.template.toString());

        return nbt;
    }

    @Override
    public VirtualTarget copy() {
        return new TemplateTarget(wrappedDestination, template);
    }
    public static TemplateTarget fromNbt(CompoundTag nbt) {
        var id = ResourceLocation.tryParse(nbt.getString("template"));
        return fromNbt(nbt, new TemplateTarget(id));
    }

    @Override
    public VirtualTargetType<? extends VirtualTarget> getType() {
        return VirtualTargetType.TEMPLATE.get();
    }
}
