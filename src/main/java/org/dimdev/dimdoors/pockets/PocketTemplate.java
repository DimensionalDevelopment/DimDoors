package org.dimdev.dimdoors.pockets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimcore.schematic.Schematic;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;
import org.dimdev.dimdoors.block.entity.ModBlockEntityTypes;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.entity.ModEntityTypes;
import org.dimdev.dimdoors.entity.MonolithEntity;
import org.dimdev.dimdoors.rift.registry.LinkProperties;
import org.dimdev.dimdoors.rift.registry.RiftRegistry;
import org.dimdev.dimdoors.rift.targets.PocketEntranceMarker;
import org.dimdev.dimdoors.rift.targets.PocketExitMarker;
import org.dimdev.dimdoors.rift.targets.VirtualTarget;
import org.dimdev.dimdoors.world.pocket.Pocket;
import org.dimdev.dimdoors.world.pocket.PocketRegistry;
import org.dimdev.util.Location;
import org.dimdev.util.math.MathUtil;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author Robijnvogel
 */
public class PocketTemplate {
    private static final Logger LOGGER = LogManager.getLogger();
    private final String group;
    private final String id;
    private final String type;
    private final String name;
    private final String author;
    private Schematic schematic;
    private byte[] schematicBytecode;
    private int size; // number of chunks (16 blocks) on each side - 1
    private int baseWeight;
    private static boolean isReplacingPlaceholders = false;

    public PocketTemplate(String group, String id, String type, String name, String author) {
        this.group = group;
        this.id = id;
        this.type = type;
        this.name = name;
        this.author = author;
    }

    public PocketTemplate(String group, String id, String type, String name, String author, Schematic schematic, byte[] schematicBytecode, int size, int baseWeight) {
        this.group = group;
        this.id = id;
        this.type = type;
        this.name = name;
        this.author = author;
        this.schematic = schematic;
        this.schematicBytecode = schematicBytecode;
        this.size = size;
        this.baseWeight = baseWeight;
    }

    public static boolean isReplacingPlaceholders() {
        return PocketTemplate.isReplacingPlaceholders;
    }

    public float getWeight(int depth) {
        if (depth == -1) {
            return baseWeight;
        } else {
            return baseWeight; // TODO: make this actually dependend on the depth
        }
    }

    public static void replacePlaceholders(Schematic schematic) { // TODO: rift inheritance rather than placeholders
        // Replace placeholders (some schematics will contain them)
        isReplacingPlaceholders = true;
        List<CompoundTag> tileEntities = new ArrayList<>();
        for (CompoundTag tileEntityNBT : schematic.tileEntities) {
            if (tileEntityNBT.contains("placeholder")) {
                int x = tileEntityNBT.getInt("x");
                int y = tileEntityNBT.getInt("y");
                int z = tileEntityNBT.getInt("z");

                BlockState state = schematic.palette.get(schematic.blockData[x][y][z]);

                CompoundTag newNBT = new CompoundTag();
                EntranceRiftBlockEntity rift = ModBlockEntityTypes.ENTRANCE_RIFT.instantiate();
                switch (tileEntityNBT.getString("placeholder")) {
                    case "deeper_depth_door":
                        rift.setPos(new BlockPos(x, y, z));
                        rift.setProperties(DefaultDungeonDestinations.pocketLinkProperties);
                        rift.setDestination(DefaultDungeonDestinations.deeperDungeonDestination);
                        newNBT = rift.toTag(newNBT);
                        break;
                    case "less_deep_depth_door":
                        rift.setPos(new BlockPos(x, y, z));
                        rift.setProperties(DefaultDungeonDestinations.pocketLinkProperties);
                        rift.setDestination(DefaultDungeonDestinations.shallowerDungeonDestination);
                        newNBT = rift.toTag(newNBT);
                        break;
                    case "overworld_door":
                        rift.setPos(new BlockPos(x, y, z));
                        rift.setProperties(DefaultDungeonDestinations.pocketLinkProperties);
                        rift.setDestination(DefaultDungeonDestinations.overworldDestination);
                        newNBT = rift.toTag(newNBT);
                        break;
                    case "entrance_door":
                        rift.setPos(new BlockPos(x, y, z));
                        rift.setProperties(DefaultDungeonDestinations.pocketLinkProperties);
                        rift.setDestination(DefaultDungeonDestinations.twoWayPocketEntrance);
                        newNBT = rift.toTag(newNBT);
                        break;
                    case "gateway_portal":
                        rift.setPos(new BlockPos(x, y, z));
                        rift.setProperties(DefaultDungeonDestinations.overworldLinkProperties);
                        rift.setDestination(DefaultDungeonDestinations.gatewayDestination);
                        newNBT = rift.toTag(newNBT);
                        break;
                    default:
                        throw new RuntimeException("Unknown tile entity placeholder: " + tileEntityNBT.getString("placeholder"));
                }
                // TODO: allow overriding some placeholder properties by copying other properties (not placeholder and x/y/z) to the new nbt
                tileEntities.add(newNBT);
            } else {
                tileEntities.add(tileEntityNBT);
            }
        }
        schematic.tileEntities = tileEntities;


        List<CompoundTag> entities = new ArrayList<>();
        for (CompoundTag entitiesNBT : schematic.entities) {
            if (entitiesNBT.contains("placeholder")) {
                double x = entitiesNBT.getDouble("x");
                double y = entitiesNBT.getDouble("y");
                double z = entitiesNBT.getDouble("z");
                float yaw = entitiesNBT.getFloat("yaw");
                float pitch = entitiesNBT.getFloat("pitch");

                CompoundTag newNBT;
                if ("monolith".equals(entitiesNBT.getString("placeholder"))) {
                    MonolithEntity monolith = ModEntityTypes.MONOLITH.create(null);
                    monolith.setPos(x, y, z);
                    monolith.yaw = yaw;
                    monolith.pitch = pitch;
                    newNBT = monolith.toTag(new CompoundTag());
                } else {
                    throw new RuntimeException("Unknown entity placeholder: " + entitiesNBT.getString("placeholder"));
                }
                // TODO: allow overriding some placeholder properties by copying other properties (not placeholder and x/y/z) to the new nbt
                entities.add(newNBT);
            } else {
                entities.add(entitiesNBT);
            }
        }
        schematic.entities = entities;
        isReplacingPlaceholders = false;
    }

    public void place(Pocket pocket, boolean setup) {
        pocket.setSize(size * 16, size * 16, size * 16);
        int gridSize = PocketRegistry.instance(pocket.world).getGridSize();
        World world = pocket.world;
        int xBase = pocket.box.minX;
        int yBase = pocket.box.minY;
        int zBase = pocket.box.minZ;

        //Converting the schematic from bytearray if needed
        if (schematic == null) {
            LOGGER.debug("Schematic is null, trying to reload from byteArray.");
            schematic = SchematicHandler.INSTANCE.loadSchematicFromByteArray(schematicBytecode);
            replacePlaceholders(schematic);
        }

        //Place the schematic
        LOGGER.info("Placing new pocket using schematic " + id + " at x = " + xBase + ", z = " + zBase);
        schematic.place(world, xBase, yBase, zBase);

        SchematicHandler.INSTANCE.incrementUsage(this);
        if (!setup && !SchematicHandler.INSTANCE.isUsedOftenEnough(this)) {
            //remove schematic from "cache"
            schematic = null;
        }
    }

    public void setup(Pocket pocket, VirtualTarget linkTo, LinkProperties linkProperties) {
        int gridSize = PocketRegistry.instance(pocket.world).getGridSize();
        ServerWorld world = pocket.world;
        int xBase = pocket.box.minX;
        int yBase = pocket.box.minY;
        int zBase = pocket.box.minZ;

        // Fill chests and make rift list
        List<RiftBlockEntity> rifts = new ArrayList<>();
        for (CompoundTag tileEntityNBT : schematic.tileEntities) {
            BlockPos pos = new BlockPos(
                    xBase + tileEntityNBT.getInt("x"),
                    yBase + tileEntityNBT.getInt("y"),
                    zBase + tileEntityNBT.getInt("z"));
            BlockEntity tile = world.getBlockEntity(pos);

            if (tile instanceof RiftBlockEntity) {
                LOGGER.debug("Rift found in schematic at " + pos);
                RiftBlockEntity rift = (RiftBlockEntity) tile;
                rift
                        .getDestination()
                        .setLocation(
                                new Location(
                                        (ServerWorld) rift.getWorld(),
                                        rift.getPos()));
                rifts.add(rift);
            } else if (tile instanceof Inventory) {
                Inventory inventory = (Inventory) tile;
                if (inventory.isEmpty()) {
                    if (tile instanceof ChestBlockEntity || tile instanceof DispenserBlockEntity) {
                        LootTable table;
                        if (tile instanceof ChestBlockEntity) {
                            LOGGER.debug("Now populating chest.");
                            table = world.getServer().getLootManager().getTable(new Identifier("dimdoors:dungeon_chest"));
                        } else { //(tile instanceof TileEntityDispenser)
                            LOGGER.debug("Now populating dispenser.");
                            table = world.getServer().getLootManager().getTable(new Identifier("dimdoors:dispenser_projectiles"));
                        }
                        LootContext ctx = new LootContext.Builder(world).random(world.random).build(LootContextTypes.CHEST);
                        table.supplyInventory(inventory, ctx);
                        LOGGER.debug("Inventory should be populated now. Chest is: " + (inventory.isEmpty() ? "empty." : "filled."));
                        if (inventory.isEmpty()) {
                            LOGGER.error(", however Inventory is: empty!");
                        }
                    }
                }
            }
        }

        // Find an entrance

        HashMap<RiftBlockEntity, Float> entranceWeights = new HashMap<>();

        for (RiftBlockEntity rift : rifts) { // Find an entrance
            if (rift.getDestination() instanceof PocketEntranceMarker) {
                entranceWeights.put(rift, ((PocketEntranceMarker) rift.getDestination()).getWeight());
            }
        }

        if (entranceWeights.size() == 0) {
            LOGGER.warn("Pocket had no possible entrance in schematic!");
            return;
        }
        RiftBlockEntity selectedEntrance = MathUtil.weightedRandom(entranceWeights);

        // Replace entrances with appropriate destinations
        for (RiftBlockEntity rift : rifts) {
            VirtualTarget dest = rift.getDestination();
            if (dest instanceof PocketEntranceMarker) {
                if (rift == selectedEntrance) {
                    PocketRegistry.instance(world).markDirty();
                    rift.setDestination(((PocketEntranceMarker) dest).getIfDestination());
                    rift.register();
                    RiftRegistry.instance(world).addPocketEntrance(pocket, new Location((ServerWorld) rift.getWorld(), rift.getPos()));
                } else {
                    rift.setDestination(((PocketEntranceMarker) dest).getOtherwiseDestination());
                }
            }
        }

        // Link pocket exits back
        for (RiftBlockEntity rift : rifts) {
            VirtualTarget dest = rift.getDestination();
            if (dest instanceof PocketExitMarker) {
                if (linkProperties != null) rift.setProperties(linkProperties);
                rift.setDestination(rift.getProperties() == null || !rift.getProperties().oneWay ? linkTo : null);
            }
        }

        // register the rifts
        for (RiftBlockEntity rift : rifts) {
            rift.register();
            rift.markDirty();
        }

        if (!SchematicHandler.INSTANCE.isUsedOftenEnough(this)) {
            //remove schematic from "cache"
            schematic = null;
        }
    }

    public String getGroup() {
        return group;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public Schematic getSchematic() {
        return schematic;
    }

    public int getSize() {
        return size;
    }

    public int getBaseWeight() {
        return baseWeight;
    }

    public void setSchematic(Schematic schematic) {
        this.schematic = schematic;
    }

    public void setSchematicBytecode(byte[] schematicBytecode) {
        this.schematicBytecode = schematicBytecode;
    }
}
