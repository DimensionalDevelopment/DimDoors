package org.dimdev.dimdoors.pockets;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimcore.schematic.Schematic;
import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;
import org.dimdev.dimdoors.block.entity.ModBlockEntityTypes;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.rift.registry.LinkProperties;
import org.dimdev.dimdoors.rift.targets.VirtualTarget;
import org.dimdev.dimdoors.util.Location;
import org.dimdev.dimdoors.world.pocket.Pocket;
import org.dimdev.dimdoors.world.pocket.PocketRegistry;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

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
            return this.baseWeight;
        } else {
            return this.baseWeight; // TODO: make this actually dependend on the depth
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
                        rift.setProperties(DefaultDungeonDestinations.POCKET_LINK_PROPERTIES);
                        rift.setDestination(DefaultDungeonDestinations.DEEPER_DUNGEON_DESTINATION);
                        newNBT = rift.toTag(newNBT);
                        break;
                    case "less_deep_depth_door":
                        rift.setPos(new BlockPos(x, y, z));
                        rift.setProperties(DefaultDungeonDestinations.POCKET_LINK_PROPERTIES);
                        rift.setDestination(DefaultDungeonDestinations.SHALLOWER_DUNGEON_DESTINATION);
                        newNBT = rift.toTag(newNBT);
                        break;
                    case "overworld_door":
                        rift.setPos(new BlockPos(x, y, z));
                        rift.setProperties(DefaultDungeonDestinations.POCKET_LINK_PROPERTIES);
                        rift.setDestination(DefaultDungeonDestinations.OVERWORLD_DESTINATION);
                        newNBT = rift.toTag(newNBT);
                        break;
                    case "entrance_door":
                        rift.setPos(new BlockPos(x, y, z));
                        rift.setProperties(DefaultDungeonDestinations.POCKET_LINK_PROPERTIES);
                        rift.setDestination(DefaultDungeonDestinations.TWO_WAY_POCKET_ENTRANCE);
                        newNBT = rift.toTag(newNBT);
                        break;
                    case "gateway_portal":
                        rift.setPos(new BlockPos(x, y, z));
                        rift.setProperties(DefaultDungeonDestinations.OVERWORLD_LINK_PROPERTIES);
                        rift.setDestination(DefaultDungeonDestinations.GATEWAY_DESTINATION);
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
            TemplateUtils.setupEntityPlaceholders(entities, entitiesNBT);
        }
        schematic.entities = entities;
        isReplacingPlaceholders = false;
    }

    public void place(Pocket pocket, boolean setup) {
        pocket.setSize(this.size * 16, this.size * 16, this.size * 16);
        int gridSize = PocketRegistry.getInstance(pocket.world).getGridSize();
        ServerWorld world = DimensionalDoorsInitializer.getWorld(pocket.world);
        int xBase = pocket.box.minX;
        int yBase = pocket.box.minY;
        int zBase = pocket.box.minZ;

        //Converting the schematic from bytearray if needed
        if (this.schematic == null) {
            LOGGER.debug("Schematic is null, trying to reload from byteArray.");
            this.schematic = SchematicHandler.INSTANCE.loadSchematicFromByteArray(this.schematicBytecode);
            replacePlaceholders(this.schematic);
        }

        //Place the schematic
        LOGGER.info("Placing new pocket using schematic " + this.id + " at x = " + xBase + ", z = " + zBase);
        this.schematic.place(world, xBase, yBase, zBase);

        SchematicHandler.INSTANCE.incrementUsage(this);
        if (!setup && !SchematicHandler.INSTANCE.isUsedOftenEnough(this)) {
            //remove schematic from "cache"
            this.schematic = null;
        }
    }

    public void setup(Pocket pocket, VirtualTarget linkTo, LinkProperties linkProperties) {
        int gridSize = PocketRegistry.getInstance(pocket.world).getGridSize();
        ServerWorld world = DimensionalDoorsInitializer.getWorld(pocket.world);
        int xBase = pocket.box.minX;
        int yBase = pocket.box.minY;
        int zBase = pocket.box.minZ;

        // Fill chests and make rift list
        List<RiftBlockEntity> rifts = new ArrayList<>();
        for (CompoundTag tileEntityNBT : this.schematic.tileEntities) {
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
                        TemplateUtils.setupLootTable(world, tile, inventory, LOGGER);
                        LOGGER.debug("Inventory should be populated now. Chest is: " + (inventory.isEmpty() ? "empty." : "filled."));
                        if (inventory.isEmpty()) {
                            LOGGER.error(", however Inventory is: empty!");
                        }
                    }
                }
            }
        }
        // Link pocket exits back
        TemplateUtils.registerRifts(rifts, linkTo, linkProperties, pocket);

        if (!SchematicHandler.INSTANCE.isUsedOftenEnough(this)) {
            //remove schematic from "cache"
            this.schematic = null;
        }
    }

    public String getGroup() {
        return this.group;
    }

    public String getId() {
        return this.id;
    }

    public String getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

    public String getAuthor() {
        return this.author;
    }

    public Schematic getSchematic() {
        return this.schematic;
    }

    public int getSize() {
        return this.size;
    }

    public int getBaseWeight() {
        return this.baseWeight;
    }

    public void setSchematic(Schematic schematic) {
        this.schematic = schematic;
    }

    public void setSchematicBytecode(byte[] schematicBytecode) {
        this.schematicBytecode = schematicBytecode;
    }
}
