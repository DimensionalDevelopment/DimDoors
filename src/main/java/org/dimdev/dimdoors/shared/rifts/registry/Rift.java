package org.dimdev.dimdoors.shared.rifts.registry;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.dimdev.annotatednbt.NBTSerializable;
import org.dimdev.annotatednbt.Saved;
import org.dimdev.ddutils.Location;
import org.dimdev.dimdoors.shared.rifts.TileEntityRift;

@NoArgsConstructor @AllArgsConstructor
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
        riftTileEntity.updateColor();
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

    @Override
    public void sourceAdded(RegistryVertex source) {
        super.sourceAdded(source);
        ((TileEntityRift) location.getTileEntity()).updateColor();
    }

    @Override
    public void targetAdded(RegistryVertex target) {
        super.targetAdded(target);
        ((TileEntityRift) location.getTileEntity()).updateColor();
    }

    public void markDirty() {
        RiftRegistry.instance().markSubregistryDirty(dim);
        ((TileEntityRift) location.getTileEntity()).updateColor();
    }
}
