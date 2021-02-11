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
import org.dimdev.dimdoors.block.entity.ModBlockEntityTypes;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.entity.ModEntityTypes;
import org.dimdev.dimdoors.entity.MonolithEntity;
import org.dimdev.dimdoors.rift.registry.LinkProperties;
import org.dimdev.dimdoors.rift.targets.PocketEntranceMarker;
import org.dimdev.dimdoors.rift.targets.PocketExitMarker;
import org.dimdev.dimdoors.rift.targets.VirtualTarget;
import org.dimdev.dimdoors.world.level.DimensionalRegistry;
import org.dimdev.dimdoors.util.Location;
import org.dimdev.dimdoors.util.math.MathUtil;
import org.dimdev.dimdoors.util.schematic.v2.Schematic;
import org.dimdev.dimdoors.world.pocket.Pocket;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;

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
                MonolithEntity monolith = Objects.requireNonNull(ModEntityTypes.MONOLITH.create(null));
                monolith.setPos(x, y, z);
                monolith.yaw = yaw;
                monolith.pitch = pitch;
                newTag = monolith.toTag(new CompoundTag());
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
    }

    static public void registerRifts(List<? extends RiftBlockEntity> rifts, VirtualTarget linkTo, LinkProperties linkProperties, Pocket pocket) {
        ServerWorld world = DimensionalDoorsInitializer.getWorld(pocket.world);
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
                rift.setDestination(rift.getProperties() == null || !rift.getProperties().oneWay ? linkTo : null);
            }
        }

        for (RiftBlockEntity rift : rifts) {
            rift.register();
            rift.markDirty();
        }
    }

    public static void replacePlaceholders(Schematic schematic, StructureWorldAccess world) {
        // Replace placeholders (some schematics will contain them)
        List<CompoundTag> blockEntities = new ArrayList<>();
        for (CompoundTag blockEntityTag : schematic.getBlockEntities()) {
            if (blockEntityTag.contains("placeholder")) {
                int x = blockEntityTag.getInt("x");
                int y = blockEntityTag.getInt("y");
                int z = blockEntityTag.getInt("z");

                CompoundTag newTag = new CompoundTag();
                EntranceRiftBlockEntity rift = Objects.requireNonNull(ModBlockEntityTypes.ENTRANCE_RIFT.instantiate());
                rift.setWorld(world.toServerWorld());
                switch (blockEntityTag.getString("placeholder")) {
                    case "deeper_depth_door":
                        rift.setPos(new BlockPos(x, y, z));
                        rift.setProperties(DefaultDungeonDestinations.POCKET_LINK_PROPERTIES);
                        rift.setDestination(DefaultDungeonDestinations.DEEPER_DUNGEON_DESTINATION);
                        newTag = rift.toTag(newTag);
                        break;
                    case "less_deep_depth_door":
                        rift.setPos(new BlockPos(x, y, z));
                        rift.setProperties(DefaultDungeonDestinations.POCKET_LINK_PROPERTIES);
                        rift.setDestination(DefaultDungeonDestinations.SHALLOWER_DUNGEON_DESTINATION);
                        newTag = rift.toTag(newTag);
                        break;
                    case "overworld_door":
                        rift.setPos(new BlockPos(x, y, z));
                        rift.setProperties(DefaultDungeonDestinations.POCKET_LINK_PROPERTIES);
                        rift.setDestination(DefaultDungeonDestinations.OVERWORLD_DESTINATION);
                        newTag = rift.toTag(newTag);
                        break;
                    case "entrance_door":
                        rift.setPos(new BlockPos(x, y, z));
                        rift.setProperties(DefaultDungeonDestinations.POCKET_LINK_PROPERTIES);
                        rift.setDestination(DefaultDungeonDestinations.TWO_WAY_POCKET_ENTRANCE);
                        newTag = rift.toTag(newTag);
                        break;
                    case "gateway_portal":
                        rift.setPos(new BlockPos(x, y, z));
                        rift.setProperties(DefaultDungeonDestinations.OVERWORLD_LINK_PROPERTIES);
                        rift.setDestination(DefaultDungeonDestinations.GATEWAY_DESTINATION);
                        newTag = rift.toTag(newTag);
                        break;
                    default:
                        throw new RuntimeException("Unknown block entity placeholder: " + blockEntityTag.getString("placeholder"));
                }
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
