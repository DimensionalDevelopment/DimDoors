package org.dimdev.dimdoors.pockets;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.util.BlockPlacementType;
import org.dimdev.dimdoors.util.schematic.Schematic;
import org.dimdev.dimdoors.util.schematic.SchematicPlacer;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

import java.util.List;

public record PocketTemplate(Schematic schematic, ResourceLocation id) {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final boolean replacingPlaceholders = false;

//    public void setup(Pocket pocket, VirtualTarget linkTo, LinkProperties linkProperties) {
//        ServerWorld world = DimensionalDoorsInitializer.getWorld(pocket.world);
//
//        List<RiftBlockEntity> rifts = new ArrayList<>();
//        BlockPos origin = pocket.getOrigin();
//        for (CompoundTag blockEntityTag : this.schematic.getBlockEntities()) {
//            int[] pos = blockEntityTag.getIntArray("Pos");
//
//            BlockPos actualBlock = origin.offset(pos[0], pos[1], pos[2]);
//
//            BlockEntity tile = world.getBlockEntity(actualBlock);
//
//            if (tile instanceof RiftBlockEntity) {
//                LOGGER.debug("Rift found in schematic at " + actualBlock);
//                RiftBlockEntity rift = (RiftBlockEntity) tile;
//                rift.getDestination().setLocation(new Location((ServerWorld) Objects.requireNonNull(rift.getWorld()), rift.getPos()));
//                rifts.add(rift);
//            } else if (tile instanceof Container) {
//                Inventory inventory = (Inventory) tile;
//                if (inventory.isEmpty()) {
//                    if (tile instanceof ChestBlockEntity || tile instanceof DispenserBlockEntity) {
//                        TemplateUtils.setupLootTable(world, tile, inventory, LOGGER);
//                        if (inventory.isEmpty()) {
//                            LOGGER.error(", however Inventory is: empty!");
//                        }
//                    }
//                }
//            }
//        }
//
//        TemplateUtils.registerRifts(rifts, linkTo, linkProperties, pocket);
//    }

    public List<BlockEntity> place(Pocket pocket, BlockPlacementType placementType) {
        pocket.setSize(schematic.getWidth(), schematic.getHeight(), schematic.getLength());
        ServerLevel world = DimensionalDoors.getWorld(pocket.getWorld());
        BlockPos origin = pocket.getOrigin();
        return SchematicPlacer.place(this.schematic, world, origin, placementType);
    }

//	public Map<BlockPos, RiftBlockEntity> getAbsoluteRifts(Pocket pocket) {
//		pocket.setSize(schematic.getWidth(), schematic.getHeight(), schematic.getLength());
//		Map<BlockPos, RiftBlockEntity> absoluteRifts = SchematicPlacer.getAbsoluteRifts(this.schematic, pocket.getOrigin(), DimensionalDoors.getWorld(pocket.getWorld()).registryAccess());
//		ServerLevel world = DimensionalDoors.getWorld(pocket.getWorld());
//		absoluteRifts.values().forEach(rift -> rift.setWorld(world));
//		return absoluteRifts;
//	}

    public static boolean isReplacingPlaceholders() {
        return replacingPlaceholders;
    }


}