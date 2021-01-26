package org.dimdev.dimdoors.pockets;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.rift.registry.LinkProperties;
import org.dimdev.dimdoors.rift.targets.VirtualTarget;
import org.dimdev.dimdoors.util.Location;
import org.dimdev.dimdoors.util.schematic.v2.Schematic;
import org.dimdev.dimdoors.util.schematic.v2.SchematicPlacer;
import org.dimdev.dimdoors.world.pocket.Pocket;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class PocketTemplateV2 {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final boolean replacingPlaceholders = false;
    private final Schematic schematic;
    private final int size;
    private final String id;

    public PocketTemplateV2(Schematic schematic, int size, String id) {
        this.schematic = schematic;
        this.size = size;
        this.id = id;
    }

    public void setup(Pocket pocket, VirtualTarget linkTo, LinkProperties linkProperties) {
        ServerWorld world = DimensionalDoorsInitializer.getWorld(pocket.world);

        List<RiftBlockEntity> rifts = new ArrayList<>();
        BlockPos origin = pocket.getOrigin();
        for (CompoundTag blockEntityTag : this.schematic.getBlockEntities()) {
            int[] pos = blockEntityTag.getIntArray("Pos");

            BlockPos actualBlock = origin.add(pos[0], pos[1], pos[2]);

            BlockEntity tile = world.getBlockEntity(actualBlock);

            if (tile instanceof RiftBlockEntity) {
                LOGGER.debug("Rift found in schematic at " + actualBlock);
                RiftBlockEntity rift = (RiftBlockEntity) tile;
                rift.getDestination().setLocation(new Location((ServerWorld) Objects.requireNonNull(rift.getWorld()), rift.getPos()));
                rifts.add(rift);
            } else if (tile instanceof Inventory) {
                Inventory inventory = (Inventory) tile;
                if (inventory.isEmpty()) {
                    if (tile instanceof ChestBlockEntity || tile instanceof DispenserBlockEntity) {
                        TemplateUtils.setupLootTable(world, tile, inventory, LOGGER);
                        if (inventory.isEmpty()) {
                            LOGGER.error(", however Inventory is: empty!");
                        }
                    }
                }
            }
        }

        TemplateUtils.registerRifts(rifts, linkTo, linkProperties, pocket);
    }

    public void place(Pocket pocket) {
        pocket.setSize(this.size * 16, this.size * 16, this.size * 16);
        ServerWorld world = DimensionalDoorsInitializer.getWorld(pocket.world);
        BlockPos origin = new BlockPos(pocket.box.minX, pocket.box.minY, pocket.box.minZ);
        LOGGER.info("Placing new pocket using schematic " + this.id + " at x = " + origin.getX() + ", z = " + origin.getZ());
        SchematicPlacer.place(this.schematic, world, origin);
    }

    public static boolean isReplacingPlaceholders() {
        return replacingPlaceholders;
    }

    public Schematic getSchematic() {
        return this.schematic;
    }

    public String getId() {
        return this.id;
    }
}
