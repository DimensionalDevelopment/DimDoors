package org.dimdev.dimdoors.pockets;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.storage.loot.LootTable;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.api.util.math.MathUtil;
import org.dimdev.dimdoors.block.entity.Rift;
import org.dimdev.dimdoors.entity.ModEntityTypes;
import org.dimdev.dimdoors.entity.MonolithEntity;
import org.dimdev.dimdoors.rift.registry.LinkProperties;
import org.dimdev.dimdoors.rift.registry.PocketRegistry;
import org.dimdev.dimdoors.rift.targets.PocketEntranceMarker;
import org.dimdev.dimdoors.rift.targets.PocketExitMarker;
import org.dimdev.dimdoors.rift.targets.VirtualTarget;
import org.dimdev.dimdoors.world.ModLootTables;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static org.dimdev.dimdoors.DimensionalDoors.LOGGER;

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

    public static void setupLootTable(ServerLevel world, RandomizableContainerBlockEntity randomizable, Logger logger) {
        ResourceKey<LootTable> table;
        if (randomizable instanceof DispenserBlockEntity) {
            logger.debug("Now populating dispenser.");
            table = ModLootTables.DISPENSER_PROJECTILES;
        } else {
            logger.debug("Now populating chest.");
            table = ModLootTables.DUNGEON_CHEST;
        }

        randomizable.setLootTable(table);
        randomizable.setLootTableSeed(world.getRandom().nextLong());

//        LootParams ctx = new LootParams.Builder(world).withParameter(LootContextParams.ORIGIN, Vec3.atLowerCornerOf(tile.getBlockPos())).create(LootContextParamSets.CHEST);
//        table.fill(inventory, ctx, world.getSeed());
//        if (inventory.isEmpty()) {
//            logger.error(", however Inventory is: empty!");
//        }
    }

    static public void registerRifts(List<? extends Rift> rifts, VirtualTarget linkTo, LinkProperties linkProperties, Pocket pocket) {
        ServerLevel world = DimensionalDoors.getWorld(pocket.getWorld());
        HashMap<Rift, Float> entranceWeights = new HashMap<>();

        // Add logging to debug
        LOGGER.info("Registering {} rifts for pocket {}", rifts.size(), pocket.getId());

        for (Rift rift : rifts) {
            if (rift.getData().getDestination() instanceof PocketEntranceMarker entranceMarker) {
                entranceWeights.put(rift, entranceMarker.getWeight());
            }
        }

        if (entranceWeights.isEmpty()) {
            LOGGER.warn("No entrance markers found in pocket {}", pocket.getId());
            return;
        }

        Rift selectedEntrance = MathUtil.weightedRandom(entranceWeights);
        LOGGER.info("Selected entrance at {} for pocket {}", selectedEntrance.getBlockPos(), pocket.getId());

        // Replace entrances with appropriate destinations
        for (Rift rift : rifts) {
            if (rift.getData().getDestination() instanceof PocketEntranceMarker entranceMarker) {
                if (rift == selectedEntrance) {
                    rift.setDestination(entranceMarker.getIfDestination());
                    rift.register();

                    Location entranceLocation = Location.ofWorld(world, rift.getBlockPos());
                    PocketRegistry.getInstance().addPocketEntrance(pocket, entranceLocation);
                    LOGGER.info("Registered pocket entrance at {} {}", entranceLocation.getWorldId().location(), entranceLocation.getBlockPos());
                } else {
                    rift.setDestination(entranceMarker.getOtherwiseDestination());
                }
            }
        }

        for (Rift rift : rifts) {
            if (rift.getData().getDestination() instanceof PocketExitMarker) {
                if (linkProperties != null) rift.setProperties(linkProperties);
                VirtualTarget<?> exitDestination = rift.getProperties() == null || !rift.getProperties().isOneWay() ? linkTo : VirtualTarget.NoneTarget.INSTANCE;
                if (exitDestination == null) {
                    LOGGER.warn("No exit link target supplied for rift at {} in pocket {}", rift.getBlockPos(), pocket.getId());
                    exitDestination = VirtualTarget.NoneTarget.INSTANCE;
                }
                rift.setDestination(exitDestination);
                if (exitDestination != VirtualTarget.NoneTarget.INSTANCE) {
                    exitDestination.setLocation(Location.ofWorld(world, rift.getBlockPos()));
                }
            }
        }

        for (Rift rift : rifts) {
            rift.register();
            rift.markStateChanged();
        }
    }

    public static void linkRifts(Location from, Location to) {
        if (from == null || to == null) return;
        if (!(from.getBlockEntity() instanceof Rift fromBe)) return;
        //This is the freaking potato texture from tf2. Bad things happen if this invocation is removed
//    to.getWorld(); //TODO: Figure out how ensure world is loaded before .getBlockEntity is called so that this janky line isn't needed.
        Rift toBe = to.getBlockEntity() instanceof Rift rift ? rift : null;
        fromBe.setDestination(to.asTarget());
        fromBe.markStateChanged();
        if (toBe != null && toBe.getProperties() != null) {
            toBe.setProperties(toBe.getProperties().withLinksRemaining(toBe.getProperties().getLinksRemaining() - 1));
            toBe.updateProperties();
            toBe.markStateChanged();
        }
    }

//    public static void replacePlaceholders(Schematic schematic, WorldGenLevel world) {
//        // Replace placeholders (some schematics will contain them)
//        List<CompoundTag> blockEntities = new ArrayList<>();
//        for (CompoundTag blockEntityTag : schematic.getBlockEntities()) {
//            if (blockEntityTag.contains("placeholder")) {
//                int x = blockEntityTag.getInt("x");
//                int y = blockEntityTag.getInt("y");
//                int z = blockEntityTag.getInt("z");
//                BlockPos pos = new BlockPos(x, y, z);
//
//                CompoundTag newTag = new CompoundTag();
//                EntranceRiftBlockEntity rift = new EntranceRiftBlockEntity(pos, Schematic.getBlockSample(schematic).getBlockState(pos));
//                switch (blockEntityTag.getString("placeholder")) {
//                    case "deeper_depth_door" -> {
//                        rift.setProperties(DefaultDungeonDestinations.POCKET_LINK_PROPERTIES);
//                        rift.setDestination(DefaultDungeonDestinations.getDeeperDungeonDestination());
//                        rift.saveAdditional(newTag, world.registryAccess());
//                    }
//                    case "less_deep_depth_door" -> {
//                        rift.setProperties(DefaultDungeonDestinations.POCKET_LINK_PROPERTIES);
//                        rift.setDestination(DefaultDungeonDestinations.getShallowerDungeonDestination());
//                        rift.saveAdditional(newTag, world.registryAccess());
//                    }
//                    case "overworld_door" -> {
//                        rift.setProperties(DefaultDungeonDestinations.POCKET_LINK_PROPERTIES);
//                        rift.setDestination(DefaultDungeonDestinations.getOverworldDestination());
//                        rift.saveAdditional(newTag, world.registryAccess());
//                    }
//                    case "entrance_door" -> {
//                        rift.setProperties(DefaultDungeonDestinations.POCKET_LINK_PROPERTIES);
//                        rift.setDestination(DefaultDungeonDestinations.getTwoWayPocketEntrance());
//                        rift.saveAdditional(newTag, world.registryAccess());
//                    }
//                    case "gateway_portal" -> {
//                        rift.setProperties(DefaultDungeonDestinations.OVERWORLD_LINK_PROPERTIES);
//                        rift.setDestination(DefaultDungeonDestinations.getGateway());
//                        rift.saveAdditional(newTag, world.registryAccess());
//                    }
//                    default -> throw new RuntimeException("Unknown block entity placeholder: " + blockEntityTag.getString("placeholder"));
//                }
//                rift.setWorld(world.getLevel());
//                blockEntities.add(newTag);
//            } else {
//                blockEntities.add(blockEntityTag);
//            }
//        }
//        schematic.setBlockEntities(blockEntities);
//
//        List<CompoundTag> entities = new ArrayList<>();
//        for (CompoundTag entityTag : schematic.getEntities()) {
//            TemplateUtils.setupEntityPlaceholders(entities, entityTag);
//        }
//        schematic.setEntities(entities);
//    }
}
