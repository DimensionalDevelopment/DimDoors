package org.dimdev.dimdoors.pockets;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.block.state.BlockState;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tileentity.CoordinateArgument;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.util.Location;
import org.dimdev.util.math.MathUtil;
import org.dimdev.util.schem.Schematic;
import org.dimdev.dimdoors.entity.MonolithEntity;
import org.dimdev.dimdoors.rift.targets.VirtualTarget;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.rift.targets.PocketEntranceMarker;
import org.dimdev.dimdoors.rift.targets.PocketExitMarker;
import org.dimdev.dimdoors.rift.registry.LinkProperties;
import org.dimdev.dimdoors.rift.registry.RiftRegistry;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;
import org.dimdev.pocketlib.Pocket;
import org.dimdev.pocketlib.PocketRegistry;

import java.util.*;

/**
 * @author Robijnvogel
 */
@AllArgsConstructor @RequiredArgsConstructor
public class PocketTemplate {
    private static final Logger LOGGER = LogManager.getLogger();
    @Getter private final String group;
    @Getter private final String id;
    @Getter private final String type;
    @Getter private final String name;
    @Getter private final String author;
    @Getter @Setter private Schematic schematic;
    @Setter private byte[] schematicBytecode;
    @Getter private final int size; // number of chunks (16 blocks) on each side - 1
    @Getter private final int baseWeight;
    @Getter private static boolean isReplacingPlaceholders = false;

    public float getWeight(int depth) {
        //noinspection IfStatementWithIdenticalBranches
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
            if (tileEntityNBT.hasKey("placeholder")) {
                int x = tileEntityNBT.getInt("x");
                int y = tileEntityNBT.getInt("y");
                int z = tileEntityNBT.getInt("z");

                BlockState state = schematic.palette.get(schematic.blockData[x][y][z]);

                CompoundTag newNBT;
                switch (tileEntityNBT.getString("placeholder")) {
                    case "deeper_depth_door":
                        EntranceRiftBlockEntity rift = (EntranceRiftBlockEntity) state.getBlock().createTileEntity(null, state);
                        rift.setPos(new BlockPos(x, y, z));
                        rift.setProperties(DefaultDungeonDestinations.pocketLinkProperties);
                        rift.setDestination(DefaultDungeonDestinations.deeperDungeonDestination);
                        newNBT = rift.serializeNBT();
                        break;
                    case "less_deep_depth_door":
                        /*TileEntityEntranceRift*/ rift = (EntranceRiftBlockEntity) state.getBlock().createTileEntity(null, state);
                        rift.setPos(new BlockPos(x, y, z));
                        rift.setProperties(DefaultDungeonDestinations.pocketLinkProperties);
                        rift.setDestination(DefaultDungeonDestinations.shallowerDungeonDestination);
                        newNBT = rift.serializeNBT();
                        break;
                    case "overworld_door":
                        /*TileEntityEntranceRift*/ rift = (EntranceRiftBlockEntity) state.getBlock().createTileEntity(null, state);
                        rift.setPos(new BlockPos(x, y, z));
                        rift.setProperties(DefaultDungeonDestinations.pocketLinkProperties);
                        rift.setDestination(DefaultDungeonDestinations.overworldDestination);
                        rift.setLeaveRiftOnBreak(true);
                        newNBT = rift.serializeNBT();
                        break;
                    case "entrance_door":
                        /*TileEntityEntranceRift*/ rift = (EntranceRiftBlockEntity) state.getBlock().createTileEntity(null, state);
                        rift.setPos(new BlockPos(x, y, z));
                        rift.setProperties(DefaultDungeonDestinations.pocketLinkProperties);
                        rift.setDestination(DefaultDungeonDestinations.twoWayPocketEntrance);
                        newNBT = rift.serializeNBT();
                        break;
                    case "gateway_portal":
                        /*TileEntityEntranceRift*/ rift = (EntranceRiftBlockEntity) state.getBlock().createTileEntity(null, state);
                        rift.setPos(new BlockPos(x, y, z));
                        rift.setProperties(DefaultDungeonDestinations.overworldLinkProperties);
                        rift.setDestination(DefaultDungeonDestinations.gatewayDestination);
                        newNBT = rift.serializeNBT();
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
            if (entitiesNBT.hasKey("placeholder")) {
                double x = entitiesNBT.getDouble("x");
                double y = entitiesNBT.getDouble("y");
                double z = entitiesNBT.getDouble("z");
                float yaw = entitiesNBT.getFloat("yaw");
                float pitch = entitiesNBT.getFloat("pitch");

                CompoundTag newNBT;
                switch (entitiesNBT.getString("placeholder")) {
                    case "monolith":
                        MonolithEntity monolith = new MonolithEntity(null);
                        monolith.setLocationAndAngles(x, y, z, yaw, pitch);
                        newNBT = monolith.serializeNBT();
                        break;
                    default:
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
        pocket.setSize(size);
        int gridSize = PocketRegistry.instance(pocket.world).getGridSize();
        World world = pocket.world;
        int xBase = pocket.getX() * gridSize * 16;
        int yBase = 0;
        int zBase = pocket.getZ() * gridSize * 16;
        
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
        World world = pocket.world;
        int xBase = pocket.getX() * gridSize * 16;
        int yBase = 0;
        int zBase = pocket.getZ() * gridSize * 16;

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
                rift.getDestination().setLocation(new Location(rift.getWorld(), rift.getPos()));
                rifts.add(rift);
            } else if (tile instanceof Inventory) {
                Inventory inventory = (Inventory) tile;
                if (inventory.isInvEmpty()) {
                    if (tile instanceof ChestBlockEntity || tile instanceof DispenserBlockEntity) {
                        LootTable table;
                        if (tile instanceof ChestBlockEntity) {
                            LOGGER.debug("Now populating chest.");
                            table = world.getLootTableManager().getLootTableFromLocation(new Identifier("dimdoors:dungeon_chest"));
                        } else { //(tile instanceof TileEntityDispenser)
                            LOGGER.debug("Now populating dispenser.");
                            table = world.getLootTableManager().getLootTableFromLocation(new Identifier("dimdoors:dispenser_projectiles"));
                        }
                        LootContext ctx = new LootContext.Builder(world).build();
                        table.fillInventory(inventory, world.rand, ctx);
                        LOGGER.debug("Inventory should be populated now. Chest is: " + (inventory.isEmpty() ? "emtpy." : "filled."));
                        if (inventory.isEmpty()) {
                            LOGGER.error(", however Inventory is: emtpy!");
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
                    RiftRegistry.instance().addPocketEntrance(pocket, new Location(rift.getWorld(), rift.getPos()));
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
                if (rift instanceof EntranceRiftBlockEntity && !rift.isAlwaysDelete()) {
                    ((EntranceRiftBlockEntity) rift).setLeaveRiftOnBreak(true); // We modified the door's state
                }
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
}
