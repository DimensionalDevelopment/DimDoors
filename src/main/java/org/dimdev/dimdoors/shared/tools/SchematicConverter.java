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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.util.Constants;
import org.dimdev.ddutils.schem.Schematic;
import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.blocks.BlockDimensionalDoor;
import org.dimdev.dimdoors.shared.blocks.BlockDimensionalDoorWood;
import org.dimdev.dimdoors.shared.blocks.ModBlocks;
import org.dimdev.dimdoors.shared.items.ModItems;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public final class SchematicConverter {

    private final static int LOCKED_CHEST_ID = 95;
    private final static int POTION_ID = 373;
    private final static int WRITTEN_BOOK_ID = 387;

    public static Schematic convertSchematic(NBTTagCompound nbt, String schematicId, String author) {
        Schematic schematic = new Schematic(nbt.getShort("Width"), nbt.getShort("Height"), nbt.getShort("Length"));

        schematic.name = schematicId;
        schematic.author = author;
        schematic.creationDate = -1; // Old schematics had no creation date
        schematic.requiredMods = new String[]{DimDoors.MODID};

        // <editor-fold desc="Tile entities">
        List<Vec3i> tileEntityPositions = new ArrayList<>();
        if (nbt.hasKey("TileEntities")) {
            NBTTagList tileEntitiesNBT = nbt.getTagList("TileEntities", Constants.NBT.TAG_COMPOUND);
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
                            continue; // Remove all Rifts from the Doors. These will get added back later
                        case "Furnace":
                            tileEntityNBT.setInteger("CookTimeTotal", 0);
                        case "Chest":
                        case "Trap":
                        case "Hopper": // There aren't any other kinds of inventories in the old schematics.
                            NBTTagList items = tileEntityNBT.getTagList("Items", Constants.NBT.TAG_COMPOUND);
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
                                            NBTTagList oldPages = subTag.getTagList("pages", Constants.NBT.TAG_STRING);
                                            //DimDoors.log.info("Written book has " + oldPages.tagCount() + " pages." + (oldPages.tagCount() == 0 ? " STRING_TAG_ID is " + STRING_TAG_ID : ""));
                                            NBTTagList newPages = new NBTTagList();
                                            for (NBTBase pageNBTBase : oldPages) {
                                                NBTTagString oldPageNBT = (NBTTagString) pageNBTBase;
                                                String oldPage = oldPageNBT.getString();
                                                //String newPage = "{\"text\":\"" + oldPage + "\"}"; //works as well, but leaves in actual paragraph break characters
                                                String newPage = ITextComponent.Serializer.componentToJson(new TextComponentString(oldPage)); //substitutes paragraph break characters with "\n"
                                                NBTTagString newPageNBT = new NBTTagString(newPage); //this HAS to be created new, because a change doesn't propagate up to pageNBTBase completely. Only the first word gets read from the tag list eventually. I don't know why, but it does.
                                                newPages.appendTag(newPageNBT);
                                                DimDoors.log.info("Converted written book page: \n{ " + oldPage + "\n into: \n" + newPageNBT.getString() + "\n.");
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

                                itemTag.setString("id", item.getRegistryName().toString()); //item.getItemStackDisplayName(ItemStack.EMPTY);
                                if (oldMeta != newMeta) {
                                    itemTag.setInteger("Damage", newMeta);
                                }
                                DimDoors.log.info("Item: " + item.getRegistryName());
                                //DimDoors.log.info("ID of itemstack in inventory set from " + oldID + " to '" + newID + "'.");
                            }
                            break;
                        case "Sign":
                            tileEntityNBT.setString("Text1", ITextComponent.Serializer.componentToJson(new TextComponentString(tileEntityNBT.getString("Text1"))));
                            tileEntityNBT.setString("Text2", ITextComponent.Serializer.componentToJson(new TextComponentString(tileEntityNBT.getString("Text2"))));
                            tileEntityNBT.setString("Text3", ITextComponent.Serializer.componentToJson(new TextComponentString(tileEntityNBT.getString("Text3"))));
                            tileEntityNBT.setString("Text4", ITextComponent.Serializer.componentToJson(new TextComponentString(tileEntityNBT.getString("Text4"))));
                            break;
                        case "Note":
                            tileEntityNBT.setBoolean("powered", false);
                            break;
                        default:
                            break;
                    }
                    String oldID = tileEntityNBT.getString("id");
                    String newID = translateId(oldID).toString();
                    tileEntityNBT.setString("id", newID);
                    schematic.tileEntities.add(tileEntityNBT);
                }
            }
        }
        // </editor-fold>

        // <editor-fold desc="Blocks">
        byte[] idArray = nbt.getByteArray("Blocks");
        byte[] addIdArray = nbt.getByteArray("AddBlocks");
        byte[] metaArray = nbt.getByteArray("Data");
        IBlockState lastWasSandstone;
        int entranceCount = 0;
        for (int x = 0; x < schematic.width; x++) {
            for (int z = 0; z < schematic.length; z++) {
                lastWasSandstone = null;
                for (int y = 0; y < schematic.height; y++) {
                    // Get the ID and meta at that position. See https://minecraft.gamepedia.com/Schematic_file_format
                    int index = x + z * schematic.width + y * schematic.width * schematic.length;
                    int id;
                    if (index >> 1 >= addIdArray.length) {
                        id = (short) (idArray[index] & 0xFF);
                    } else if ((index & 1) == 0) {
                        id = (short) (((addIdArray[index >> 1] & 0x0F) << 8) + (idArray[index] & 0xFF));
                    } else {
                        id = (short) (((addIdArray[index >> 1] & 0xF0) << 4) + (idArray[index] & 0xFF));
                    }
                    int meta = metaArray[index];

                    IBlockState state = getState(id, meta);
                    Block block = state.getBlock();

                    // Monoliths
                    if (block == Blocks.END_PORTAL_FRAME) {
                        NBTTagCompound monolithPlaceholder = new NBTTagCompound();
                        monolithPlaceholder.setString("placeholder", "monolith");
                        monolithPlaceholder.setDouble("x", x + 0.5d);
                        monolithPlaceholder.setDouble("y", y);
                        monolithPlaceholder.setDouble("z", x + 0.5d);

                        monolithPlaceholder.setFloat("yaw", state.getValue(BlockEndPortalFrame.FACING).getHorizontalAngle());
                        monolithPlaceholder.setFloat("pitch", 0);

                        schematic.entities.add(monolithPlaceholder);
                        state = Blocks.AIR.getDefaultState();
                        block = Blocks.AIR;
                    }

                    // Fix for the_nexus having a second door (SenseiKiwi's hideout) being an entrance
                    if (schematicId.equals("the_nexus") && y > 10 && block instanceof BlockDimensionalDoorWood) {
                        block = Blocks.OAK_DOOR;
                        //noinspection deprecation
                        state = block.getStateFromMeta(meta);
                    }

                    // Doors
                    if (block instanceof BlockDimensionalDoor && state.getValue(BlockDoor.HALF) == BlockDoor.EnumDoorHalf.LOWER) {
                        NBTTagCompound riftPlaceholder = new NBTTagCompound();
                        riftPlaceholder.setInteger("x", x);
                        riftPlaceholder.setInteger("y", y);
                        riftPlaceholder.setInteger("z", z);

                        if (block == ModBlocks.DIMENSIONAL_DOOR) {
                            riftPlaceholder.setString("placeholder", "deeper_depth_door");
                        } else if (block == ModBlocks.WARP_DIMENSIONAL_DOOR) {
                            if (lastWasSandstone != null) {
                                riftPlaceholder.setString("placeholder", "overworld_door");
                            } else {
                                riftPlaceholder.setString("placeholder", "pocket_entrance_door");
                                entranceCount++;
                            }
                        } else if (block == ModBlocks.DIMENSIONAL_PORTAL) {
                            riftPlaceholder.setString("placeholder", "gateway_portal");
                        }

                        schematic.tileEntities.add(riftPlaceholder);
                    } else if (block.hasTileEntity(state) && !tileEntityPositions.contains(new Vec3i(x, y, z))) {
                        TileEntity tileEntity = block.createTileEntity(null, state);
                        tileEntity.setPos(new BlockPos(x, y, z));
                        schematic.tileEntities.add(tileEntity.serializeNBT());
                        // DimDoors.log.info("Adding missing tile entity at " + new Vec3i(x, y, z) + " (state = " + state + ")");
                    }

                    if (lastWasSandstone != null) {
                        if (state.getBlock() == ModBlocks.WARP_DIMENSIONAL_DOOR) {
                            if (y >= 2) {
                                schematic.setBlockState(x, y - 1, z, schematic.getBlockState(x, y - 2, z));
                            } else {
                                DimDoors.log.error("Sandstone under warp door found at y = 0 in schematic " + schematicId);
                                if (schematicId.equals("small_rotunda_with_exit")) {
                                    schematic.setBlockState(x, y - 1, z, ModBlocks.FABRIC.getDefaultState());
                                } else {
                                    schematic.setBlockState(x, y - 1, z, lastWasSandstone);
                                }
                            }
                        } else {
                            schematic.setBlockState(x, y - 1, z, lastWasSandstone);
                        }
                    }

                    // There aren't any non-default sandstone blocks
                    if (block == Blocks.SANDSTONE) {
                        lastWasSandstone = state;
                    } else {
                        lastWasSandstone = null;
                        try {
                            schematic.setBlockState(x, y, z, state);
                        } catch (IndexOutOfBoundsException e) {
                            DimDoors.log.error("...", e);
                        }
                    }
                }
            }
        }
        // </editor-fold>

        if (!nbt.getTag("Entities").hasNoTags()) {
            throw new RuntimeException("Schematic contains entities, but those aren't implemented in the conversion code.");
        }

        return schematic;
    }

    private static IBlockState getState(int id, int meta) {
        Block block = Blocks.AIR;
        if (id <= 159 && id != LOCKED_CHEST_ID) {
            block = Block.getBlockById(id);
        } else {
            switch (id) {
                case 1973:
                    return ModBlocks.FABRIC.getDefaultState();
                case 1975:
                    return ModBlocks.WARP_DIMENSIONAL_DOOR.getStateFromMeta(meta);
                case 1970:
                    return ModBlocks.DIMENSIONAL_DOOR.getStateFromMeta(meta);
                case 1979:
                    return ModBlocks.DIMENSIONAL_PORTAL.getStateFromMeta(meta);
                case 220:
                    return ModBlocks.ANCIENT_FABRIC.getDefaultState();
                case LOCKED_CHEST_ID: // Locked chest's ID was replaced with stained glass in 1.7.2
                    DimDoors.log.error("Schematic contained a locked chest, which was removed in 1.7.2.");
                    break;
            }
        }

        if (id != 0 && block.getRegistryName().toString().equals("minecraft:air")) {
            throw new RuntimeException("Unknown ID " + id + " in schematic");
        }

        if (block.equals(Blocks.IRON_DOOR)) return ModBlocks.DIMENSIONAL_DOOR.getStateFromMeta(meta);
        if (block.equals(Blocks.OAK_DOOR)) return ModBlocks.WARP_DIMENSIONAL_DOOR.getStateFromMeta(meta);
        //noinspection deprecation
        return block.getStateFromMeta(meta);
    }

    @Nonnull
    private static ResourceLocation translateId(String id) {
        switch (id) {
            case "Sign":
                return TileEntity.getKey(TileEntitySign.class);
            case "Music":
                return TileEntity.getKey(TileEntityNote.class);
            case "Trap":
                return TileEntity.getKey(TileEntityDispenser.class);
            case "Comparator":
                return TileEntity.getKey(TileEntityComparator.class);
            case "Hopper":
                return TileEntity.getKey(TileEntityHopper.class);
            case "Furnace":
                return TileEntity.getKey(TileEntityFurnace.class);
            case "Chest":
                return TileEntity.getKey(TileEntityChest.class);
            default:
                throw new RuntimeException("Tile entity ID " + id + " not supported by conversion code");
        }
    }

    private static boolean isValidItemIDForSimpleConversion(int id) {
        return id > 0 && id != LOCKED_CHEST_ID && id != POTION_ID && id != WRITTEN_BOOK_ID &&
               (id <= 159 // 1.6.4 blocks
                || 256 <= id && id <= 422 // 1.6.4 items
                || 2256 <= id && id <= 2267); // 1.6.4 music discs
    }
}
