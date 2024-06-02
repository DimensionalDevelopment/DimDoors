package org.dimdev.dimdoors.pockets;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.api.util.math.MathUtil;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.entity.ModEntityTypes;
import org.dimdev.dimdoors.entity.MonolithEntity;
import org.dimdev.dimdoors.rift.registry.LinkProperties;
import org.dimdev.dimdoors.rift.targets.PocketEntranceMarker;
import org.dimdev.dimdoors.rift.targets.PocketExitMarker;
import org.dimdev.dimdoors.rift.targets.VirtualTarget;
import org.dimdev.dimdoors.util.schematic.Schematic;
import org.dimdev.dimdoors.forge.world.level.registry.DimensionalRegistry;
import org.dimdev.dimdoors.forge.world.pocket.type.Pocket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class TemplateUtils {
    static void setupEntityPlaceholders(List<CompoundTag> entities, CompoundTag entityTag) {
        if (entityTag.contains("placeholder")) {
            double x = entityTag.getDouble("x");
            double y = entityTag.getDouble("y");
            double z = entityTag.getDouble("z");
            float yaw = entityTag.getFloat("yaw");
            float pitch = entityTag.getFloat("pitch");

            CompoundTag newTag;
            if ("monolith".equals(entityTag.getString("placeholder"))) {
                MonolithEntity monolith = Objects.requireNonNull(ModEntityTypes.MONOLITH.get().create(null));
                monolith.setPos(x, y, z);
                monolith.setYRot(yaw);
                monolith.setPitch(pitch);
                newTag = monolith.saveWithoutId(new CompoundTag());
            } else {
                throw new RuntimeException("Unknown entity placeholder: " + entityTag.getString("placeholder"));
            }
            entities.add(newTag);
        } else {
            entities.add(entityTag);
        }
    }

    public static void setupLootTable(ServerLevel world, BlockEntity tile, Container inventory, Logger logger) {
        LootTable table;
        if (tile instanceof ChestBlockEntity) {
            logger.debug("Now populating chest.");
            table = world.getServer().getLootTables().get(DimensionalDoors.id("dungeon_chest"));
        } else {
            logger.debug("Now populating dispenser.");
            table = world.getServer().getLootTables().get(DimensionalDoors.id("dispenser_projectiles"));
        }
        LootContext ctx = new LootContext.Builder(world).withParameter(LootContextParams.ORIGIN, Vec3.atLowerCornerOf(tile.getBlockPos())).create(LootContextParamSets.CHEST);
        table.fill(inventory, ctx);
		if (inventory.isEmpty()) {
			logger.error(", however Inventory is: empty!");
		}
    }

    static public void registerRifts(List<? extends RiftBlockEntity> rifts, VirtualTarget linkTo, LinkProperties linkProperties, Pocket pocket) {
        ServerLevel world = DimensionalDoors.getWorld(pocket.getWorld());
        HashMap<RiftBlockEntity, Float> entranceWeights = new HashMap<>();

        for (RiftBlockEntity rift : rifts) { // Find an entrance
            if (rift.getDestination() instanceof PocketEntranceMarker) {
                entranceWeights.put(rift, ((PocketEntranceMarker) rift.getDestination()).getWeight());
            }
        }

        if (entranceWeights.size() == 0) {
            return;
        }

        RiftBlockEntity selectedEntrance = MathUtil.weightedRandom(entranceWeights);

        // Replace entrances with appropriate destinations
        for (RiftBlockEntity rift : rifts) {
            VirtualTarget dest = rift.getDestination();
            if (dest instanceof PocketEntranceMarker) {
                if (rift == selectedEntrance) {
                    rift.setDestination(((PocketEntranceMarker) dest).getIfDestination());
                    rift.register();
                    DimensionalRegistry.getRiftRegistry().addPocketEntrance(pocket, new Location((ServerLevel) rift.getLevel(), rift.getBlockPos()));
                } else {
                    rift.setDestination(((PocketEntranceMarker) dest).getOtherwiseDestination());
                }
            }
        }

        for (RiftBlockEntity rift : rifts) {
            VirtualTarget dest = rift.getDestination();
            if (dest instanceof PocketExitMarker) {
                if (linkProperties != null) rift.setProperties(linkProperties);
                rift.setDestination(rift.getProperties() == null || !rift.getProperties().isOneWay() ? linkTo : null);
            }
        }

        for (RiftBlockEntity rift : rifts) {
            rift.register();
            rift.setChanged();
        }
    }

    public static void replacePlaceholders(Schematic schematic, WorldGenLevel world) {
        // Replace placeholders (some schematics will contain them)
        List<CompoundTag> blockEntities = new ArrayList<>();
        for (CompoundTag blockEntityTag : schematic.getBlockEntities()) {
            if (blockEntityTag.contains("placeholder")) {
                int x = blockEntityTag.getInt("x");
                int y = blockEntityTag.getInt("y");
                int z = blockEntityTag.getInt("z");
                BlockPos pos = new BlockPos(x, y, z);

                CompoundTag newTag = new CompoundTag();
                EntranceRiftBlockEntity rift = new EntranceRiftBlockEntity(pos, Schematic.getBlockSample(schematic).getBlockState(pos));
				switch (blockEntityTag.getString("placeholder")) {
					case "deeper_depth_door" -> {
						rift.setProperties(DefaultDungeonDestinations.POCKET_LINK_PROPERTIES);
						rift.setDestination(DefaultDungeonDestinations.getDeeperDungeonDestination());
						rift.saveAdditional(newTag);
					}
					case "less_deep_depth_door" -> {
						rift.setProperties(DefaultDungeonDestinations.POCKET_LINK_PROPERTIES);
						rift.setDestination(DefaultDungeonDestinations.getShallowerDungeonDestination());
						rift.saveAdditional(newTag);
					}
					case "overworld_door" -> {
						rift.setProperties(DefaultDungeonDestinations.POCKET_LINK_PROPERTIES);
						rift.setDestination(DefaultDungeonDestinations.getOverworldDestination());
						rift.saveAdditional(newTag);
					}
					case "entrance_door" -> {
						rift.setProperties(DefaultDungeonDestinations.POCKET_LINK_PROPERTIES);
						rift.setDestination(DefaultDungeonDestinations.getTwoWayPocketEntrance());
						rift.saveAdditional(newTag);
					}
					case "gateway_portal" -> {
						rift.setProperties(DefaultDungeonDestinations.OVERWORLD_LINK_PROPERTIES);
						rift.setDestination(DefaultDungeonDestinations.getGateway());
						rift.saveAdditional(newTag);
					}
					default -> throw new RuntimeException("Unknown block entity placeholder: " + blockEntityTag.getString("placeholder"));
				}
				rift.setWorld(world.getLevel());
                blockEntities.add(newTag);
            } else {
                blockEntities.add(blockEntityTag);
            }
        }
        schematic.setBlockEntities(blockEntities);

        List<CompoundTag> entities = new ArrayList<>();
        for (CompoundTag entityTag : schematic.getEntities()) {
            TemplateUtils.setupEntityPlaceholders(entities, entityTag);
        }
        schematic.setEntities(entities);
    }
}
