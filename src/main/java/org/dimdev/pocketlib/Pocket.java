package org.dimdev.pocketlib;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import org.dimdev.annotatednbt.NBTSerializable;
import org.dimdev.annotatednbt.Saved;
import org.dimdev.ddutils.nbt.INBTStorable;
import org.dimdev.ddutils.nbt.NBTUtils;
import org.dimdev.dimdoors.shared.pockets.PocketRules;

@NBTSerializable public class Pocket implements INBTStorable {

    @Saved @Getter protected int id;
    @Saved @Getter protected int x; // Grid x TODO: convert to non-grid dependant coordinates
    @Saved @Getter protected int z; // Grid y
    @Saved @Getter @Setter protected int size; // TODO: non chunk-based size, better bounds such as minX, minZ, maxX, maxZ, etc.
    @Saved @Getter @Setter protected VirtualLocation virtualLocation;
    @Getter @Setter protected PocketRules rules; // TODO: make pocket rules save
    // TODO: make method of changing rules of pockets via interface/ something similar

    @Getter int dim; // Not saved

    public Pocket() {}

    public Pocket(int id, int dim, int x, int z) {
        this.id = id;
        this.dim = dim;
        this.x = x;
        this.z = z;
    }

    @Override public void readFromNBT(NBTTagCompound nbt) { NBTUtils.readFromNBT(this, nbt); }
    @Override public NBTTagCompound writeToNBT(NBTTagCompound nbt) { return NBTUtils.writeToNBT(this, nbt); }

    boolean isInBounds(BlockPos pos) {
        // pockets bounds
        int gridSize = PocketRegistry.instance(dim).getGridSize();
        int minX = x * gridSize;
        int minZ = z * gridSize;
        int maxX = minX + (size + 1) * 16;
        int maxZ = minX + (size + 1) * 16;
        return minX <= pos.getX() && minZ <= pos.getZ() && pos.getX() < maxX && pos.getZ() < maxZ;
    }

    public BlockPos getOrigin() {
        int gridSize = PocketRegistry.instance(dim).getGridSize();
        return new BlockPos(x * gridSize * 16, 0, z * gridSize * 16);
    }
}
