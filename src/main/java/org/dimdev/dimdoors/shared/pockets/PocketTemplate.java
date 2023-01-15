package org.dimdev.dimdoors.shared.pockets;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import org.dimdev.ddutils.Location;
import org.dimdev.ddutils.WorldUtils;
import org.dimdev.ddutils.math.MathUtils;
import org.dimdev.ddutils.schem.Schematic;
import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.rifts.targets.VirtualTarget;
import org.dimdev.dimdoors.shared.tileentities.TileEntityRift;
import org.dimdev.dimdoors.shared.rifts.targets.PocketEntranceMarker;
import org.dimdev.dimdoors.shared.rifts.targets.PocketExitMarker;
import org.dimdev.dimdoors.shared.rifts.registry.LinkProperties;
import org.dimdev.dimdoors.shared.rifts.registry.RiftRegistry;
import org.dimdev.dimdoors.shared.tileentities.TileEntityEntranceRift;
import org.dimdev.pocketlib.Pocket;
import org.dimdev.pocketlib.PocketRegistry;

import java.util.*;

/**
 * @author Robijnvogel
 */
@AllArgsConstructor @RequiredArgsConstructor
public class PocketTemplate {

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
        //noinspection ConditionalExpressionWithIdenticalBranches
        return depth==-1 ? baseWeight : baseWeight;// TODO: make this actually depended on the depth
    }

    public static void replacePlaceholders(Schematic schematic) { // TODO: rift inheritance rather than placeholders
        // Replace placeholders (some schematics will contain them)
        isReplacingPlaceholders = true;
        List<NBTTagCompound> tileEntities = new ArrayList<>();
        for (NBTTagCompound tileEntityNBT : schematic.tileEntities) {
            if (tileEntityNBT.hasKey("placeholder")) {
                int x = tileEntityNBT.getInteger("x");
                int y = tileEntityNBT.getInteger("y");
                int z = tileEntityNBT.getInteger("z");
                IBlockState state = schematic.palette.get(schematic.blockData[x][y][z]);
                NBTTagCompound newNBT;
                switch (tileEntityNBT.getString("placeholder")) {
                    case "deeper_depth_door":
                        TileEntityEntranceRift rift = (TileEntityEntranceRift) state.getBlock().createTileEntity(null, state);
                        rift.setPos(new BlockPos(x, y, z));
                        rift.setProperties(DefaultDungeonDestinations.pocketLinkProperties);
                        rift.setDestination(DefaultDungeonDestinations.deeperDungeonDestination);
                        rift.setLeaveRiftOnBreak(true);
                        newNBT = rift.serializeNBT();
                        break;
                    case "less_deep_depth_door":
                        /*TileEntityEntranceRift*/ rift = (TileEntityEntranceRift) state.getBlock().createTileEntity(null, state);
                        rift.setPos(new BlockPos(x, y, z));
                        rift.setProperties(DefaultDungeonDestinations.pocketLinkProperties);
                        rift.setDestination(DefaultDungeonDestinations.shallowerDungeonDestination);
                        rift.setLeaveRiftOnBreak(true);
                        newNBT = rift.serializeNBT();
                        break;
                    case "overworld_door":
                        /*TileEntityEntranceRift*/ rift = (TileEntityEntranceRift) state.getBlock().createTileEntity(null, state);
                        rift.setPos(new BlockPos(x, y, z));
                        rift.setProperties(DefaultDungeonDestinations.pocketLinkProperties);
                        rift.setDestination(DefaultDungeonDestinations.overworldDestination);
                        rift.setLeaveRiftOnBreak(true);
                        newNBT = rift.serializeNBT();
                        break;
                    case "entrance_door":
                        /*TileEntityEntranceRift*/ rift = (TileEntityEntranceRift) state.getBlock().createTileEntity(null, state);
                        rift.setPos(new BlockPos(x, y, z));
                        rift.setProperties(DefaultDungeonDestinations.pocketLinkProperties);
                        rift.setDestination(DefaultDungeonDestinations.twoWayPocketEntrance);
                        rift.setLeaveRiftOnBreak(true);
                        newNBT = rift.serializeNBT();
                        break;
                    case "gateway_portal":
                        /*TileEntityEntranceRift*/ rift = (TileEntityEntranceRift) state.getBlock().createTileEntity(null, state);
                        rift.setPos(new BlockPos(x, y, z));
                        rift.setProperties(DefaultDungeonDestinations.overworldLinkProperties);
                        rift.setDestination(DefaultDungeonDestinations.gatewayDestination);
                        rift.setCloseAfterPassThrough(true);
                        rift.setLeaveRiftOnBreak(true);
                        newNBT = rift.serializeNBT();
                        break;
                    default:
                        throw new RuntimeException("Unknown tile entity placeholder: " + tileEntityNBT.getString("placeholder"));
                }
                // TODO: allow overriding some placeholder properties by copying other properties (not placeholder and x/y/z) to the new nbt
                tileEntities.add(newNBT);
            } else tileEntities.add(tileEntityNBT);
        }
        schematic.tileEntities = tileEntities;
        List<NBTTagCompound> entities = new ArrayList<>();
        for (NBTTagCompound entitiesNBT : schematic.entities) {
            if (entitiesNBT.hasKey("placeholder")) {
                double x = entitiesNBT.getDouble("x");
                double y = entitiesNBT.getDouble("y");
                double z = entitiesNBT.getDouble("z");
                float yaw = entitiesNBT.getFloat("yaw");
                float pitch = entitiesNBT.getFloat("pitch");
                NBTTagCompound newNBT;
                if ("monolith".equals(entitiesNBT.getString("placeholder"))) newNBT = defaultMonolith(x, y, z, yaw, pitch);
                else throw new RuntimeException("Unknown entity placeholder: " + entitiesNBT.getString("placeholder"));
                // TODO: allow overriding some placeholder properties by copying other properties (not placeholder and x/y/z) to the new nbt
                entities.add(newNBT);
            } else entities.add(entitiesNBT);
        }
        schematic.entities = entities;
        isReplacingPlaceholders = false;
    }

    private static NBTTagCompound defaultMonolith(double x, double y, double z, float yaw, float pitch) {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setString("id", "dimdoors:mob_monolith");
        compound.setTag("Pos", newDoubleNBTList(x, y, z));
        compound.setTag("Motion", newDoubleNBTList(0, 0, 0));
        compound.setTag("Rotation", newFloatNBTList(yaw, pitch));
        compound.setFloat("FallDistance", 0);
        compound.setShort("Fire", (short) 0);
        compound.setShort("Air", (short) 300);
        compound.setBoolean("OnGround", false);
        compound.setInteger("Dimension", 0);
        compound.setBoolean("Invulnerable", false);
        compound.setInteger("PortalCooldown", 0);
        compound.setUniqueId("UUID", UUID.randomUUID());
        compound.setInteger("Aggro", 0);
        return compound;
    }

    private static NBTTagList newDoubleNBTList(double... numbers) {
        NBTTagList nbttaglist = new NBTTagList();
        for (double d0 : numbers) nbttaglist.appendTag(new NBTTagDouble(d0));
        return nbttaglist;
    }

    private static NBTTagList newFloatNBTList(float... numbers) {
        NBTTagList nbttaglist = new NBTTagList();
        for (float d0 : numbers) nbttaglist.appendTag(new NBTTagFloat(d0));
        return nbttaglist;
    }

    public void place(Pocket pocket, boolean setup) {
        pocket.setSize(size);
        int gridSize = PocketRegistry.instance(pocket.getDim()).getGridSize();
        int dim = pocket.getDim();
        WorldServer world = WorldUtils.getWorld(dim);
        int xBase = pocket.getX() * gridSize * 16;
        int yBase = 0;
        int zBase = pocket.getZ() * gridSize * 16;
        //Converting the schematic from bytearray if needed
        if (schematic == null) {
            DimDoors.log.debug("Schematic is null, trying to reload from byteArray.");
            schematic = SchematicHandler.INSTANCE.loadSchematicFromByteArray(schematicBytecode);
            replacePlaceholders(schematic);
        }
        //Place the schematic
        DimDoors.log.info("Placing new pocket using schematic " + id + " at x = " + xBase + ", z = " + zBase);
        schematic.place(world, xBase, yBase, zBase);
        SchematicHandler.INSTANCE.incrementUsage(this);
        if (!setup && SchematicHandler.INSTANCE.notUsedOftenEnough(this))
            //remove schematic from "cache"
            schematic = null;
    }

    public void setup(Pocket pocket, VirtualTarget linkTo, LinkProperties linkProperties) {
        int gridSize = PocketRegistry.instance(pocket.getDim()).getGridSize();
        int dim = pocket.getDim();
        WorldServer world = WorldUtils.getWorld(dim);
        int xBase = pocket.getX() * gridSize * 16;
        int yBase = 0;
        int zBase = pocket.getZ() * gridSize * 16;
        // Fill chests and make rift list
        List<TileEntityRift> rifts = new ArrayList<>();
        for (NBTTagCompound tileEntityNBT : schematic.tileEntities) {
            BlockPos pos = new BlockPos(
                    xBase + tileEntityNBT.getInteger("x"),
                    yBase + tileEntityNBT.getInteger("y"),
                    zBase + tileEntityNBT.getInteger("z"));
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof TileEntityRift) {
                DimDoors.log.debug("Rift found in schematic at " + pos);
                TileEntityRift rift = (TileEntityRift) tile;
                rift.getDestination().setLocation(new Location(rift.getWorld(), rift.getPos()));
                rifts.add(rift);
            } else if (tile instanceof IInventory) {
                IInventory inventory = (IInventory) tile;
                if (inventory.isEmpty()) {
                    if (tile instanceof TileEntityChest || tile instanceof TileEntityDispenser) {
                        LootTable table;
                        if (tile instanceof TileEntityChest) {
                            DimDoors.log.debug("Now populating chest.");
                            table = world.getLootTableManager().getLootTableFromLocation(new ResourceLocation(DimDoors.MODID + ":dungeon_chest"));
                        } else { //(tile instanceof TileEntityDispenser)
                            DimDoors.log.debug("Now populating dispenser.");
                            table = world.getLootTableManager().getLootTableFromLocation(new ResourceLocation(DimDoors.MODID + ":dispenser_projectiles"));
                        }
                        LootContext ctx = new LootContext.Builder(world).build();
                        table.fillInventory(inventory, world.rand, ctx);
                        DimDoors.log.debug("Inventory should be populated now. Chest is: " + (inventory.isEmpty() ? "empty." : "filled."));
                        if (inventory.isEmpty()) DimDoors.log.error(", however Inventory is: empty!");
                    }
                }
            }
        }
        // Find an entrance
        HashMap<TileEntityRift, Float> entranceWeights = new HashMap<>();
        for (TileEntityRift rift : rifts) { // Find an entrance
            if (rift.getDestination() instanceof PocketEntranceMarker)
                entranceWeights.put(rift, ((PocketEntranceMarker) rift.getDestination()).getWeight());
        }
        if (entranceWeights.size() == 0) {
            DimDoors.log.warn("Pocket had no possible entrance in schematic!");
            return;
        }
        TileEntityRift selectedEntrance = MathUtils.weightedRandom(entranceWeights);
        // Replace entrances with appropriate destinations
        for (TileEntityRift rift : rifts) {
            VirtualTarget dest = rift.getDestination();
            if (dest instanceof PocketEntranceMarker) {
                if (rift == selectedEntrance) {
                    PocketRegistry.instance(dim).markDirty();
                    rift.setDestination(((PocketEntranceMarker) dest).getIfDestination());
                    rift.register();
                    RiftRegistry.instance().addPocketEntrance(pocket, new Location(rift.getWorld(), rift.getPos()));
                } else rift.setDestination(((PocketEntranceMarker) dest).getOtherwiseDestination());
            }
        }
        // Link pocket exits back
        for (TileEntityRift rift : rifts) {
            VirtualTarget dest = rift.getDestination();
            if (dest instanceof PocketExitMarker) {
                if (linkProperties != null) rift.setProperties(linkProperties);
                rift.setDestination(rift.getProperties() == null || !rift.getProperties().oneWay ? linkTo : null);
                if (rift instanceof TileEntityEntranceRift && !rift.isAlwaysDelete())
                    ((TileEntityEntranceRift) rift).setLeaveRiftOnBreak(true); // We modified the door's state
            }
        }
        // register the rifts
        for (TileEntityRift rift : rifts) {
            rift.register();
            rift.markDirty();
        }
        if (SchematicHandler.INSTANCE.notUsedOftenEnough(this)) {
            //remove schematic from "cache"
            schematic = null;
        }
    }
}
