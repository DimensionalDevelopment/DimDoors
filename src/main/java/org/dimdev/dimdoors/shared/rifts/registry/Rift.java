package org.dimdev.dimdoors.shared.rifts.registry;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.minecraft.nbt.NBTTagCompound;
import org.dimdev.annotatednbt.NBTSerializable;
import org.dimdev.annotatednbt.Saved;
import org.dimdev.ddutils.Location;
import org.dimdev.ddutils.nbt.NBTUtils;
import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.rifts.TileEntityRift;

@NoArgsConstructor @AllArgsConstructor @ToString
@NBTSerializable public class Rift extends RegistryVertex {
    public @Saved Location location;
    public @Saved boolean isFloating;
    public @Saved LinkProperties properties;
    // TODO: receiveDungeonLink

    public Rift(Location location) {
        this.location = location;
    }

    @Override
    public void sourceGone(RegistryVertex source) {
        super.sourceGone(source);
        TileEntityRift riftTileEntity = (TileEntityRift) location.getTileEntity();
        if (source instanceof Rift) {
            riftTileEntity.sourceGone(((Rift) source).location);
        }
    }

    @Override
    public void targetGone(RegistryVertex target) {
        super.targetGone(target);
        TileEntityRift riftTileEntity = (TileEntityRift) location.getTileEntity();
        if (target instanceof Rift) {
            riftTileEntity.targetGone(((Rift) target).location);
        }
        riftTileEntity.updateColor();
    }

    public void targetChanged(RegistryVertex target) {
        DimDoors.log.info("Rift " + this + " notified of target " + target + " having changed. Updating color.");
        ((TileEntityRift) location.getTileEntity()).updateColor();
    }

    public void markDirty() { // TODO: better name
        RiftRegistry.instance().markSubregistryDirty(dim);
        ((TileEntityRift) location.getTileEntity()).updateColor();
        for (Location location : RiftRegistry.instance().getSources(location)) {
            RiftRegistry.instance().getRift(location).targetChanged(this);
        }
    }

    @Override public void readFromNBT(NBTTagCompound nbt) { super.readFromNBT(nbt); NBTUtils.readFromNBT(this, nbt); }
    @Override public NBTTagCompound writeToNBT(NBTTagCompound nbt) { nbt = super.writeToNBT(nbt); return NBTUtils.writeToNBT(this, nbt); }
}
