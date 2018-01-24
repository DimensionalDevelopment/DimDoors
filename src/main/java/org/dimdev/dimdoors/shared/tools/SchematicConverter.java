package org.dimdev.dimdoors.shared.tools;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockEndPortalFrame;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.*;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import org.dimdev.ddutils.nbt.NBTUtils;
import org.dimdev.ddutils.schem.Schematic;
import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.blocks.ModBlocks;
import org.dimdev.dimdoors.shared.entities.EntityMonolith;
import org.dimdev.dimdoors.shared.items.ModItems;
import org.dimdev.dimdoors.shared.rifts.destinations.AvailableLinkDestination;
import org.dimdev.dimdoors.shared.rifts.destinations.PocketEntranceMarker;
import org.dimdev.dimdoors.shared.rifts.destinations.PocketExitMarker;
import org.dimdev.dimdoors.shared.rifts.registry.LinkProperties;
import org.dimdev.dimdoors.shared.tileentities.TileEntityEntranceRift;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * @author Robijnvogel
 */
public final class SchematicConverter {

    private final static int LOCKED_CHEST_ID = 95;
    private final static int POTION_ID = 373;
    private final static int WRITTEN_BOOK_ID = 387;

    private final static int NBT_COMPOUND_TAG_ID = NBTUtils.NBT_COMPOUND_TAG_ID;
    private final static int STRING_TAG_ID = NBTUtils.NBT_COMPOUND_TAG_ID;
    private static final boolean GENERATE_DUNGEON_INFO = false;

    public static Schematic convertSchematic(NBTTagCompound nbt, String schematicId, String name, String author) {
        Schematic schematic = new Schematic();

        schematic.version = 1; //already the default value
        schematic.author = author;
        schematic.name = name; // This is passed as an argument by the SchematicHandler. The name is taken from the JSONs
        schematic.creationDate = System.currentTimeMillis();
        schematic.requiredMods = new String[]{DimDoors.MODID};

        schematic.width = nbt.getShort("Width");
        schematic.height = nbt.getShort("Height");
        schematic.length = nbt.getShort("Length");
        schematic.offset = new int[]{0, 0, 0};

        // Schematic info
        int ironDimDoors = 0;
        int warpDoors = 0;
        int monoliths = 0;
        int chests = 0;
        int dispensers = 0;
        int allPistonBases = 0;
        int tnt = 0;
        int diamondBlocks = 0;
        int goldBlocks = 0;
        int ironBlocks = 0;

        byte[] blockIdArray = nbt.getByteArray("Blocks");
        byte[] addId = nbt.getByteArray("AddBlocks");
        Map<Integer, Byte> palletteMap = new HashMap<>(); // block ID -> palette index
        byte currentPalletteIndex = 0;
        for (int i = 0; i < blockIdArray.length; i++) {
            int id;
            if (i >> 1 >= addId.length) {
                id = (short) (blockIdArray[i] & 0xFF);
            } else if ((i & 1) == 0) {
                id = (short) (((addId[i >> 1] & 0x0F) << 8) + (blockIdArray[i] & 0xFF));
            } else {
                id = (short) (((addId[i >> 1] & 0xF0) << 4) + (blockIdArray[i] & 0xFF));
            }
            if (palletteMap.containsKey(id)) {
                blockIdArray[i] = palletteMap.get(id);
            } else {

                IBlockState block = Blocks.AIR.getDefaultState(); //air is the default
                if (id <= 159 && id != LOCKED_CHEST_ID) {
                    block = Block.getBlockById(id).getDefaultState();
                } else {
                    switch (id) {
                        case 1973:
                            block = ModBlocks.FABRIC.getDefaultState();
                            break;
                        case 1975:
                            block = ModBlocks.WARP_DIMENSIONAL_DOOR.getDefaultState();
                            break;
                        case 1970:
                            block = ModBlocks.DIMENSIONAL_DOOR.getDefaultState();
                            break;
                        case 1979:
                            block = ModBlocks.DIMENSIONAL_PORTAL.getDefaultState();
                            break;
                        case 220:
                            block = ModBlocks.ANCIENT_FABRIC.getDefaultState();
                            break;
                        case LOCKED_CHEST_ID: // Locked chest's ID was replaced with stained glass in 1.7.2
                            DimDoors.log.error("Schematic contained a locked chest, which was removed in 1.7.2.");
                            break;
                    }
                }

                if (id != 0 && block.getBlock().getRegistryName().toString().equals("minecraft:air")) {
                    throw new RuntimeException("Unknown ID " + id + " in schematic " + schematicId);
                }
                if (block.equals(Blocks.IRON_DOOR)) {
                    block = ModBlocks.DIMENSIONAL_DOOR.getDefaultState();
                }
                if (block.equals(Blocks.OAK_DOOR)) {
                    block = ModBlocks.WARP_DIMENSIONAL_DOOR.getDefaultState();
                }
                schematic.palette.add(block);
                palletteMap.put(id, currentPalletteIndex);
                blockIdArray[i] = currentPalletteIndex;
                currentPalletteIndex++;
            }
        }

        List<Vec3i> tileEntityPositions = new ArrayList<>();
        if (nbt.hasKey("TileEntities")) {
            NBTTagList tileEntitiesNBT = nbt.getTagList("TileEntities", NBT_COMPOUND_TAG_ID);
            if (!tileEntitiesNBT.hasNoTags()) {
                for (int i = 0; i < tileEntitiesNBT.tagCount(); i++) {
                    NBTTagCompound tileEntityNBT = tileEntitiesNBT.getCompoundTagAt(i);
                    int x = tileEntityNBT.getInteger("x");
                    int y = tileEntityNBT.getInteger("y");
                    int z = tileEntityNBT.getInteger("z");
                    tileEntityPositions.add(new Vec3i(x, y, z));

                    switch (tileEntityNBT.getString("id")) {
                        case "TileEntityDimDoor":
                        case "TileEntityRift":
                            continue; // remove all Rifts from the Doors. These will get added back later
                        case "Chest":
                        case "Trap":
                        case "Hopper":
                        case "Furnace": //there aren't any other kinds of inventories in the old schematics. At least not with contents
                            NBTTagList items = tileEntityNBT.getTagList("Items", NBT_COMPOUND_TAG_ID);
                            for (int j = 0; j < items.tagCount(); j++) {
                                NBTTagCompound itemTag = items.getCompoundTagAt(j);
                                int oldID = itemTag.getInteger("id");
                                if (oldID == 0) { //if the slot is empty
                                    //whatever
                                    continue;
                                }
                                int oldMeta = itemTag.getInteger("Damage");

                                Item item = Item.getItemById(0); //air is the default
                                int newMeta = oldMeta;
                                if (isValidItemIDForSimpleConversion(oldID)) {
                                    item = Item.getItemById(oldID);
                                } else {
                                    switch (oldID) {
                                        case 220:
                                            item = ModItems.ANCIENT_FABRIC;
                                            newMeta = oldMeta == 0 ? 15 : 0;
                                            break;
                                        case 1970:
                                            item = ModItems.DIMENSIONAL_DOOR;
                                            break;
                                        case 1973:
                                            item = ModItems.FABRIC;
                                            newMeta = oldMeta == 0 ? 15 : 0;
                                            break;
                                        case 1975:
                                            item = ModItems.WARP_DIMENSIONAL_DOOR;
                                            break;
                                        case 1979: //Transient Portal
                                            break;
                                        case 5936:
                                            item = ModItems.WORLD_THREAD;
                                            break;
                                        case WRITTEN_BOOK_ID:
                                            item = Item.getItemById(oldID);
                                            NBTTagCompound subTag = itemTag.getCompoundTag("tag");
                                            NBTTagList oldPages = subTag.getTagList("pages", STRING_TAG_ID);
                                            //DimDoors.log.info("Written book has " + oldPages.tagCount() + " pages." + (oldPages.tagCount() == 0 ? " STRING_TAG_ID is " + STRING_TAG_ID : ""));
                                            NBTTagList newPages = new NBTTagList();
                                            for (NBTBase pageNBTBase : oldPages) {
                                                NBTTagString oldPageNBT = (NBTTagString) pageNBTBase;
                                                String oldPage = oldPageNBT.getString();
                                                //String newPage = "{\"text\":\"" + oldPage + "\"}"; //works as well, but leaves in actual paragraph break characters
                                                String newPage = ITextComponent.Serializer.componentToJson(new TextComponentString(oldPage)); //substitutes paragraph break characters with "\n"
                                                NBTTagString newPageNBT = new NBTTagString(newPage); //this HAS to be created new, because a change doesn't propagate up to pageNBTBase completely. Only the first word gets read from the tag list eventually. I don't know why, but it does.
                                                newPages.appendTag(newPageNBT);
                                                //DimDoors.log.info("Converted written book page: \n{ " + oldPage + "\n into: \n" + newPageNBT.getString() + "\n.");
                                            }
                                            subTag.setTag("pages", newPages);
                                            break;
                                        case POTION_ID:
                                            DimDoors.log.error("An inventory in this Schematic contained a potion. Potions were split into normal and splash potions after Minecraft 1.7. If this error shows, please contact a DimDoors developer.");
                                            //Luckily none of the old schematics seem to contain any potions, so we don't have to handle this.
                                            break;
                                        case LOCKED_CHEST_ID: // Locked chest's ID was replaced with stained glass in 1.7.2
                                            DimDoors.log.error("An inventory in this Schematic contained a locked chest. Locked Chests were removed in 1.7.2. If this error shows, please contact a DimDoors developer.");
                                            //Luckily none of the old schematics seem to contain any locked chest, so we don't have to handle this.
                                            break;
                                    }
                                }

                                itemTag.removeTag("id"); //not sure if needed, but just to be sure...
                                String newID = item.getRegistryName().toString(); //item.getItemStackDisplayName(ItemStack.EMPTY);
                                itemTag.setString("id", newID);
                                if (oldMeta != newMeta) {
                                    itemTag.setInteger("Damage", newMeta);
                                }
                                //DimDoors.log.info("ID of itemstack in inventory set from " + oldID + " to '" + newID + "'.");
                            }
                            break;
                        case "Sign":
                            tileEntityNBT.setString("Text1", ITextComponent.Serializer.componentToJson(new TextComponentString(tileEntityNBT.getString("Text1"))));
                            tileEntityNBT.setString("Text2", ITextComponent.Serializer.componentToJson(new TextComponentString(tileEntityNBT.getString("Text2"))));
                            tileEntityNBT.setString("Text3", ITextComponent.Serializer.componentToJson(new TextComponentString(tileEntityNBT.getString("Text3"))));
                            tileEntityNBT.setString("Text4", ITextComponent.Serializer.componentToJson(new TextComponentString(tileEntityNBT.getString("Text4"))));
                            break;
                        default:
                            break;
                    }
                    String oldID = tileEntityNBT.getString("id");
                    String newID = translateId(oldID);
                    tileEntityNBT.setString("id", newID);
                    schematic.tileEntities.add(tileEntityNBT);
                }
            }
        }

        byte[] dataIntArray = nbt.getByteArray("Data");
        schematic.blockData = new short[schematic.width][schematic.height][schematic.length];
        for (int x = 0; x < schematic.width; x++) {
            for (int y = 0; y < schematic.height; y++) {
                for (int z = 0; z < schematic.length; z++) {
                    int blockInt = blockIdArray[x + z * schematic.width + y * schematic.width * schematic.length]; //according to the documentation on https://github.com/SpongePowered/Schematic-Specification/blob/master/versions/schematic-1.md
                    int metadata = dataIntArray[x + z * schematic.width + y * schematic.width * schematic.length]; //according to the documentation on https://github.com/SpongePowered/Schematic-Specification/blob/master/versions/schematic-1.md

                    IBlockState baseState = schematic.palette.get(blockInt); //this is the default blockstate except for ancient fabric
                    if (baseState == baseState.getBlock().getDefaultState() || baseState.getBlock().equals(ModBlocks.FABRIC) || baseState.getBlock().equals(ModBlocks.ANCIENT_FABRIC)) { //should only be false if {@code baseState} is ancient fabric
                        IBlockState blockState;
                        if (baseState.getBlock().equals(ModBlocks.FABRIC) || baseState.getBlock().equals(ModBlocks.ANCIENT_FABRIC)) {
                            blockState = baseState;
                        } else {
                            //noinspection deprecation
                            blockState = baseState.getBlock().getStateFromMeta(metadata);
                        }
                        if (schematic.palette.contains(blockState)) { //check whether or not this blockstate is already in the list
                            blockInt = schematic.palette.indexOf(blockState);
                        } else {
                            schematic.palette.add(blockState);
                            //DimDoors.log.info("New blockstate detected. Original blockInt = " + blockInt + " and blockState is " + blockState);
                            blockInt = schematic.palette.size() - 1;
                        }
                        Block block = blockState.getBlock();

                        //counting blocks and features
                        if (block.equals(Blocks.DIAMOND_BLOCK)) {
                            diamondBlocks++;
                        } else if (block.equals(Blocks.GOLD_BLOCK)) {
                            goldBlocks++;
                        } else if (block.equals(Blocks.IRON_BLOCK)) {
                            ironBlocks++;
                        } else if (block.equals(Blocks.PISTON) || block.equals(Blocks.STICKY_PISTON)) {
                            allPistonBases++;
                        } else if (block.equals(Blocks.TNT)) {
                            tnt++;
                        } else if (block.equals(Blocks.CHEST)) {
                            chests++;
                        } else if (block.equals(Blocks.DISPENSER)) {
                            dispensers++;
                        } else if (block.equals(Blocks.END_PORTAL_FRAME)) {
                            monoliths++;
                        } else if (block.equals(ModBlocks.DIMENSIONAL_DOOR)) {
                            ironDimDoors++;
                        } else if (block.equals(ModBlocks.WARP_DIMENSIONAL_DOOR)) {
                            warpDoors++;
                        }

                        //Monoliths, Rifts and missing TileEntities
                        if (block.equals(Blocks.END_PORTAL_FRAME)) {
                            // I think it's safe to assume that air is present
                            blockInt = schematic.palette.indexOf(Blocks.AIR.getDefaultState());
                            EntityMonolith monolith = new EntityMonolith(null);
                            EnumFacing facing = blockState.getValue(BlockEndPortalFrame.FACING);
                            monolith.setLocationAndAngles(x + 0.5d, y, z + 0.5d, facing.getHorizontalAngle(), 0);
                            schematic.entities.add(monolith.serializeNBT());
                        } else if (block.equals(ModBlocks.DIMENSIONAL_DOOR) || block.equals(ModBlocks.WARP_DIMENSIONAL_DOOR) || block.equals(ModBlocks.DIMENSIONAL_PORTAL)) {
                            //DimDoors.log.info("Door found: " + block.getUnlocalizedName());
                            if (blockState.getProperties().get(BlockDoor.HALF).equals(BlockDoor.EnumDoorHalf.LOWER)) { //LOWER? seriously Runemoro? Fuck you. XD
                                TileEntityEntranceRift rift = (TileEntityEntranceRift) block.createTileEntity(null, blockState);
                                rift.setPos(new BlockPos(x, y, z));

                                rift.setProperties(LinkProperties.builder()
                                        .groups(new HashSet<>(Arrays.asList(0, 1)))
                                        .linksRemaining(1).build());

                                if (block.equals(ModBlocks.DIMENSIONAL_DOOR)) {
                                    rift.setDestination(AvailableLinkDestination.builder()
                                            .acceptedGroups(Collections.singleton(0))
                                            .coordFactor(1)
                                            .negativeDepthFactor(10000)
                                            .positiveDepthFactor(80)
                                            .weightMaximum(100)
                                            .newRiftWeight(1).build());
                                } else if (block.equals(ModBlocks.WARP_DIMENSIONAL_DOOR)) {
                                    IBlockState stateBelow = schematic.palette.get(schematic.blockData[x][y - 1][z]);
                                    if (stateBelow.getBlock().equals(Blocks.SANDSTONE)) {
                                        rift.setProperties(null); // TODO: this should be removed once the linking equations are made symmetric
                                        rift.setDestination(AvailableLinkDestination.builder()
                                                .acceptedGroups(Collections.singleton(0))
                                                .coordFactor(1)
                                                .negativeDepthFactor(0.00000000001) // The division result is cast to an int, so Double.MIN_VALUE would cause an overflow
                                                .positiveDepthFactor(Double.POSITIVE_INFINITY)
                                                .weightMaximum(100)
                                                .newRiftWeight(1).build());
                                        //change the sandstone to the block below it.
                                        if (y >= 2) {
                                            schematic.blockData[x][y - 1][z] = schematic.blockData[x][y - 2][z];
                                        } else {
                                            //this only happens for one of the old schematics
                                            schematic.blockData[x][y - 1][z] = schematic.blockData[x + 1][y][z];
                                            //DimDoors.log.error("Someone placed a door on a sandstone block at the bottom of a schematic. This causes problems and should be remedied. Schematic name: " + schematicId);
                                        }
                                    } else {
                                        rift.setDestination(PocketEntranceMarker.builder()
                                                .weight(1)
                                                .ifDestination(PocketExitMarker.builder().build())
                                                .otherwiseDestination(AvailableLinkDestination.builder()
                                                        .acceptedGroups(Collections.singleton(0))
                                                        .coordFactor(1)
                                                        .negativeDepthFactor(80)
                                                        .positiveDepthFactor(10000)
                                                        .weightMaximum(100)
                                                        .newRiftWeight(1).build()).build());
                                    }
                                } else if (block.equals(ModBlocks.DIMENSIONAL_PORTAL)) {
                                    rift.setProperties(LinkProperties.builder()
                                            .groups(new HashSet<>(Arrays.asList(0, 1)))
                                            .entranceWeight(50)
                                            .linksRemaining(1).build());
                                    rift.setDestination(AvailableLinkDestination.builder()
                                            .acceptedGroups(Collections.singleton(0))
                                            .coordFactor(1) // TODO: lower value?
                                            .negativeDepthFactor(Double.POSITIVE_INFINITY)
                                            .positiveDepthFactor(80) // TODO: lower value?
                                            .weightMaximum(300) // Link further away
                                            .newRiftWeight(1)
                                            .build());
                                    rift.setCloseAfterPassThrough(true);
                                }
                                rift.markStateChanged();

                                schematic.tileEntities.add(rift.serializeNBT());
                            }
                        } else if (block.hasTileEntity(blockState) && !tileEntityPositions.contains(new Vec3i(x, y, z))) {
                            TileEntity tileEntity = block.createTileEntity(null, blockState);
                            tileEntity.setPos(new BlockPos(x, y, z));
                            //tileEntitiesNBT.appendTag(tileEntity.serializeNBT());
                            schematic.tileEntities.add(tileEntity.serializeNBT());
                            //DimDoors.log.info("Adding missing tile entity at " + new Vec3i(x, y, z) + " (state = " + blockState + ")");
                        }
                    } else { // if this is ancient fabric
                        blockInt = schematic.palette.indexOf(baseState);
                    }
                    assert blockInt >= 0;
                    schematic.blockData[x][y][z] = (short) blockInt;
                }
            }
        }
        if (!nbt.getTag("Entities").hasNoTags()) {
            throw new RuntimeException("Schematic contains entities, but those aren't implemented in the conversion code");
        }
        schematic.paletteMax = schematic.palette.size() - 1;

        if (GENERATE_DUNGEON_INFO) {
            DimDoors.log.info(schematicId + "," + ironDimDoors + "," + warpDoors + "," + monoliths + "," + chests + ","
                              + dispensers + "," + allPistonBases + "," + tnt + "," + diamondBlocks + "," + goldBlocks + "," + ironBlocks);
        }

        return schematic;
    }

    @Nonnull
    private static String translateId(String id) { // TODO
        ResourceLocation location;
        switch (id) {
            case "Sign":
                location = TileEntity.getKey(TileEntitySign.class);
                break;
            case "Music":
                location = TileEntity.getKey(TileEntityNote.class);
                break;
            case "Trap":
                location = TileEntity.getKey(TileEntityDispenser.class);
                break;
            case "Comparator":
                location = TileEntity.getKey(TileEntityComparator.class);
                break;
            case "Hopper":
                location = TileEntity.getKey(TileEntityHopper.class);
                break;
            case "Furnace":
                location = TileEntity.getKey(TileEntityFurnace.class);
                break;
            case "Chest":
                location = TileEntity.getKey(TileEntityChest.class);
                break;
            default:
                throw new RuntimeException("Tile entity ID " + id + " not supported by conversion code");
        }
        if (location == null) {
            DimDoors.log.error("Resourcelocation of TileEntity with old ID: " + id + " was null. If you want to complain about log spam; " + (id.equals("Hopper") ? "it is very likely that it's FoamFix causing this. Please update it to at least version 0.9.0-1.12.2!" : "we have no idea what causes this, so please report it."));
            location = translateIdCrude(id);
        }
        return location.toString();
    }

    @Nonnull
    private static ResourceLocation translateIdCrude(String id) {
        switch (id) {
            case "Sign":
                return new ResourceLocation("minecraft:sign");
            case "Music":
                return new ResourceLocation("minecraft:noteblock");
            case "Trap":
                return new ResourceLocation("minecraft:dispenser");
            case "Comparator":
                return new ResourceLocation("minecraft:comparator");
            case "Hopper":
                return new ResourceLocation("minecraft:hopper");
            case "Furnace":
                return new ResourceLocation("minecraft:furnace");
            case "Chest":
            default:
                return new ResourceLocation("minecraft:chest");
        }
    }

    private static boolean isValidItemIDForSimpleConversion(int id) {
        return id > 0 && id != LOCKED_CHEST_ID && id != POTION_ID && id != WRITTEN_BOOK_ID &&
               (id <= 159 // 1.6.4 blocks
                || 256 <= id && id <= 422 // 1.6.4 items
                || 2256 <= id && id <= 2267); // 1.6.4 music discs
    }
}
