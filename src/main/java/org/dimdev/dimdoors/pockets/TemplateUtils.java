package org.dimdev.dimdoors.pockets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.entity.ModEntityTypes;
import org.dimdev.dimdoors.entity.MonolithEntity;
import org.dimdev.dimdoors.rift.registry.LinkProperties;
import org.dimdev.dimdoors.rift.targets.PocketEntranceMarker;
import org.dimdev.dimdoors.rift.targets.PocketExitMarker;
import org.dimdev.dimdoors.rift.targets.VirtualTarget;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.api.util.math.MathUtil;
import org.dimdev.dimdoors.util.schematic.Schematic;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;

public class TemplateUtils {
    static void setupEntityPlaceholders(List<NbtCompound> entities, NbtCompound entityTag) {
        if (entityTag.contains("placeholder")) {
            double x = entityTag.getDouble("x");
            double y = entityTag.getDouble("y");
            double z = entityTag.getDouble("z");
            float yaw = entityTag.getFloat("yaw");
            float pitch = entityTag.getFloat("pitch");

            NbtCompound newTag;
            if ("monolith".equals(entityTag.getString("placeholder"))) {
                MonolithEntity monolith = Objects.requireNonNull(ModEntityTypes.MONOLITH.create(null));
                monolith.setPos(x, y, z);
                monolith.setYaw(yaw);
                monolith.setPitch(pitch);
                newTag = monolith.writeNbt(new NbtCompound());
            } else {
                throw new RuntimeException("Unknown entity placeholder: " + entityTag.getString("placeholder"));
            }
            entities.add(newTag);
        } else {
            entities.add(entityTag);
        }
    }

    public static void setupLootTable(ServerWorld world, BlockEntity tile, Inventory inventory, Logger logger) {
        LootTable table;
        if (tile instanceof ChestBlockEntity) {
            logger.debug("Now populating chest.");
            table = world.getServer().getLootManager().getTable(new Identifier("dimdoors:dungeon_chest"));
        } else {
            logger.debug("Now populating dispenser.");
            table = world.getServer().getLootManager().getTable(new Identifier("dimdoors:dispenser_projectiles"));
        }
        LootContext ctx = new LootContext.Builder(world).random(world.random).parameter(LootContextParameters.ORIGIN, Vec3d.of(tile.getPos())).build(LootContextTypes.CHEST);
        table.supplyInventory(inventory, ctx);
		if (inventory.isEmpty()) {
			logger.error(", however Inventory is: empty!");
		}
    }

    static public void registerRifts(List<? extends RiftBlockEntity> rifts, VirtualTarget linkTo, LinkProperties linkProperties, Pocket pocket) {
        ServerWorld world = DimensionalDoorsInitializer.getWorld(pocket.getWorld());
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
                    DimensionalRegistry.getRiftRegistry().addPocketEntrance(pocket, new Location((ServerWorld) rift.getWorld(), rift.getPos()));
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
            rift.markDirty();
        }
    }

    public static void replacePlaceholders(Schematic schematic, StructureWorldAccess world) {
        // Replace placeholders (some schematics will contain them)
        List<NbtCompound> blockEntities = new ArrayList<>();
        for (NbtCompound blockEntityTag : schematic.getBlockEntities()) {
            if (blockEntityTag.contains("placeholder")) {
                int x = blockEntityTag.getInt("x");
                int y = blockEntityTag.getInt("y");
                int z = blockEntityTag.getInt("z");
                BlockPos pos = new BlockPos(x, y, z);

                NbtCompound newTag = new NbtCompound();
                EntranceRiftBlockEntity rift = new EntranceRiftBlockEntity(pos, Schematic.getBlockSample(schematic).getBlockState(pos));
				switch (blockEntityTag.getString("placeholder")) {
					case "deeper_depth_door" -> {
						rift.setProperties(DefaultDungeonDestinations.POCKET_LINK_PROPERTIES);
						rift.setDestination(DefaultDungeonDestinations.getDeeperDungeonDestination());
						rift.writeNbt(newTag);
					}
					case "less_deep_depth_door" -> {
						rift.setProperties(DefaultDungeonDestinations.POCKET_LINK_PROPERTIES);
						rift.setDestination(DefaultDungeonDestinations.getShallowerDungeonDestination());
						rift.writeNbt(newTag);
					}
					case "overworld_door" -> {
						rift.setProperties(DefaultDungeonDestinations.POCKET_LINK_PROPERTIES);
						rift.setDestination(DefaultDungeonDestinations.getOverworldDestination());
						rift.writeNbt(newTag);
					}
					case "entrance_door" -> {
						rift.setProperties(DefaultDungeonDestinations.POCKET_LINK_PROPERTIES);
						rift.setDestination(DefaultDungeonDestinations.getTwoWayPocketEntrance());
						rift.writeNbt(newTag);
					}
					case "gateway_portal" -> {
						rift.setProperties(DefaultDungeonDestinations.OVERWORLD_LINK_PROPERTIES);
						rift.setDestination(DefaultDungeonDestinations.getGateway());
						rift.writeNbt(newTag);
					}
					default -> throw new RuntimeException("Unknown block entity placeholder: " + blockEntityTag.getString("placeholder"));
				}
				rift.setWorld(world.toServerWorld());
                blockEntities.add(newTag);
            } else {
                blockEntities.add(blockEntityTag);
            }
        }
        schematic.setBlockEntities(blockEntities);

        List<NbtCompound> entities = new ArrayList<>();
        for (NbtCompound entityTag : schematic.getEntities()) {
            TemplateUtils.setupEntityPlaceholders(entities, entityTag);
        }
        schematic.setEntities(entities);
    }
}
