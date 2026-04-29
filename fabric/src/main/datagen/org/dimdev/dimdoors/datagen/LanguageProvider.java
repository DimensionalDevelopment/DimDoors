package org.dimdev.dimdoors.datagen;

import dev.architectury.registry.registries.RegistrySupplier;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import org.apache.commons.lang3.StringUtils;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.fluid.ModFluids;
import org.dimdev.dimdoors.item.ModItems;
import org.dimdev.dimdoors.painting.ModPaintings;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LanguageProvider extends FabricLanguageProvider {
    protected LanguageProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider registryLookup, TranslationBuilder builder) {
        add(builder, ModItems.DECAY);
        add(builder, ModItems.DIMENSIONAL_DOORS);

        builder.add("dimdoors.autogen_block_prefix", "Dimensional ");
        add(builder, ModBlocks.GOLD_DOOR);
        add(builder, ModBlocks.QUARTZ_DOOR);
        add(builder, ModBlocks.STONE_DOOR);
        add(builder, ModBlocks.DIMENSIONAL_PORTAL);
        add(builder, ModBlocks.BLACK_FABRIC);
        add(builder, ModBlocks.WHITE_FABRIC);
        add(builder, ModBlocks.ORANGE_FABRIC);
        add(builder, ModBlocks.MAGENTA_FABRIC);
        add(builder, ModBlocks.LIGHT_BLUE_FABRIC);
        add(builder, ModBlocks.LIGHT_GRAY_FABRIC);
        add(builder, ModBlocks.YELLOW_FABRIC);
        add(builder, ModBlocks.LIME_FABRIC);
        add(builder, ModBlocks.PINK_FABRIC);
        add(builder, ModBlocks.GRAY_FABRIC);
        add(builder, ModBlocks.CYAN_FABRIC);
        add(builder, ModBlocks.PURPLE_FABRIC);
        add(builder, ModBlocks.BLUE_FABRIC);
        add(builder, ModBlocks.BROWN_FABRIC);
        add(builder, ModBlocks.GREEN_FABRIC);
        add(builder, ModBlocks.RED_FABRIC);
        add(builder, ModBlocks.BLACK_ANCIENT_FABRIC);
        add(builder, ModBlocks.WHITE_ANCIENT_FABRIC);
        add(builder, ModBlocks.ORANGE_ANCIENT_FABRIC);
        add(builder, ModBlocks.MAGENTA_ANCIENT_FABRIC);
        add(builder, ModBlocks.LIGHT_BLUE_ANCIENT_FABRIC);
        add(builder, ModBlocks.YELLOW_ANCIENT_FABRIC);
        add(builder, ModBlocks.LIME_ANCIENT_FABRIC);
        add(builder, ModBlocks.PINK_ANCIENT_FABRIC);
        add(builder, ModBlocks.GRAY_ANCIENT_FABRIC);
        add(builder, ModBlocks.CYAN_ANCIENT_FABRIC);
        add(builder, ModBlocks.PURPLE_ANCIENT_FABRIC);
        add(builder, ModBlocks.BLUE_ANCIENT_FABRIC);
        add(builder, ModBlocks.BROWN_ANCIENT_FABRIC);
        add(builder, ModBlocks.GREEN_ANCIENT_FABRIC);
        add(builder, ModBlocks.RED_ANCIENT_FABRIC);
        add(builder, ModBlocks.DECAYED_BLOCK);
        add(builder, ModBlocks.UNFOLDED_BLOCK);
        add(builder, ModBlocks.UNWARPED_BLOCK);
        add(builder, ModBlocks.UNRAVELLED_BLOCK);
        add(builder, ModBlocks.UNRAVELLED_FABRIC);
        add(builder, ModBlocks.DETACHED_RIFT);
        add(builder, ModBlocks.ETERNAL_FLUID);
        add(builder, ModBlocks.SOLID_STATIC);
        add(builder, ModBlocks.TESSELATING_LOOM);
        add(builder, ModBlocks.REALITY_SPONGE);
        add(builder, ModBlocks.DRIFTWOOD_WOOD);
        add(builder, ModBlocks.DRIFTWOOD_LOG);
        add(builder, ModBlocks.DRIFTWOOD_PLANKS);
        add(builder, ModBlocks.DRIFTWOOD_LEAVES);
        add(builder, ModBlocks.DRIFTWOOD_SAPLING);
        add(builder, ModBlocks.DRIFTWOOD_FENCE);
        add(builder, ModBlocks.DRIFTWOOD_GATE);
        add(builder, ModBlocks.DRIFTWOOD_BUTTON);
        add(builder, ModBlocks.DRIFTWOOD_SLAB);
        add(builder, ModBlocks.DRIFTWOOD_STAIRS);
        add(builder, ModBlocks.DRIFTWOOD_DOOR);
        add(builder, ModBlocks.DRIFTWOOD_TRAPDOOR);
        add(builder, ModBlocks.AMALGAM_BLOCK);
        add(builder, ModBlocks.AMALGAM_DOOR);
        add(builder, ModBlocks.AMALGAM_TRAPDOOR);
        add(builder, ModBlocks.RUST);
        add(builder, ModBlocks.AMALGAM_SLAB);
        add(builder, ModBlocks.AMALGAM_STAIRS);
        add(builder, ModBlocks.AMALGAM_ORE);

        add(builder, ModBlocks.CLOD_BLOCK);
        add(builder, ModBlocks.CLOD_ORE);
        add(builder, ModBlocks.UNRAVELED_SPIKE);
        add(builder, ModBlocks.PALE_SAND);
        add(builder, ModBlocks.DARK_SAND_LAYER);
        add(builder, ModBlocks.DARK_SAND);
        add(builder, ModBlocks.LINT_LAYER);
        add(builder, ModBlocks.STONE_SLAB);
        add(builder, ModBlocks.STONE_STAIRS);
        add(builder, ModBlocks.STONE_WALL);

        ModBlocks.DecayGroupSet.SETS.forEach(set -> addBlockSet(builder, set));

        builder.add("block.dimdoors.gritty_stone", "Gritty Stone");
        builder.add("block.dimdoors.leak", "Leak");

        builder.add("dimdoors.autogen_item_prefix", "Dimensional ");
        builder.add("item.dimdoors.gold_door", "Gold Door");
        builder.add("item.dimdoors.quartz_door", "Quartz Door");
        builder.add("item.dimdoors.iron_dimensional_door", "Iron Dimensional Door");
        builder.add("item.dimdoors.gold_dimensional_door", "Gold Dimensional Door");
        builder.add("item.dimdoors.quartz_dimensional_door", "Quartz Dimensional Door");
        builder.add("item.dimdoors.unstable_dimensional_door", "Unstable Dimensional Door");
        builder.add("item.dimdoors.oak_dimensional_door", "Wood Dimensional Door");
        builder.add("item.dimdoors.rift_key", "Rift Key");
        builder.add("item.dimdoors.rift_signature", "Rift Signature");
        builder.add("item.dimdoors.rift_signature.stored", "Location stored");
        builder.add("item.dimdoors.rift_signature.created", "Rift created");
        builder.add("item.dimdoors.stabilized_rift_signature", "Stabilized Rift Signature");
        builder.add("item.dimdoors.stabilized_rift_signature.stored", "Location stored");
        builder.add("item.dimdoors.stabilized_rift_signature.created", "Rift created");
        builder.add("item.dimdoors.rift_configuration_tool", "Rift Configuration Tool");
        builder.add("item.dimdoors.rift_configuration_tool.info", "TODO");
        builder.add("item.dimdoors.rift_remover", "Rift Remover");
        builder.add("item.dimdoors.rift_remover.closing", "The rift will close soon");
        builder.add("item.dimdoors.rift_remover.already_closing", "This rift is already closing");
        builder.add("item.dimdoors.rift_stabilizer", "Rift Stabilizer");
        builder.add("item.dimdoors.rift_stabilizer.info", "Use on a rift's core to stop its growth.");
        builder.add("item.dimdoors.rift_stabilizer.stabilized", "The rift has been stabilized and will stop growing");
        builder.add("item.dimdoors.rift_stabilizer.already_stabilized", "This rift is already stable");
        builder.add("item.dimdoors.rift_blade", "Rift Blade");
        builder.add("item.dimdoors.rift_blade.rift_miss", "You can only use this item on a rift's core");
        builder.add("item.dimdoors.world_thread", "World Thread");
        builder.add("item.dimdoors.infrangible_fiber", "Infrangible Fiber");
        builder.add("item.dimdoors.frayed_filament", "Frayed Filament");
        builder.add("item.dimdoors.stable_fabric", "Stable Fabric");
        builder.add("item.dimdoors.world_thread_boots", "Woven World Thread Boots");
        builder.add("item.dimdoors.world_thread_helmet", "Woven World Thread Helmet");
        builder.add("item.dimdoors.world_thread_leggings", "Woven World Thread Leggings");
        builder.add("item.dimdoors.world_thread_chestplate", "Woven World Thread Chestplate");
        builder.add("item.dimdoors.rift_key.bound.info", "Bound");
        builder.add("item.dimdoors.rift_key.unbound.info", "Unbound");
        builder.add("item.dimdoors.rift_key.no_links", "There are no saved links.");
        builder.add("block.dimdoors.iron_dimensional_door.info0", "Place on the block under a rift");
        builder.add("block.dimdoors.iron_dimensional_door.info1", "to activate that rift or place");
        builder.add("block.dimdoors.iron_dimensional_door.info2", "anywhere else to create a");
        builder.add("block.dimdoors.iron_dimensional_door.info3", "pocket dimension.");
        builder.add("block.dimdoors.dungeon_door.info0", "Place on the block under a rift");
        builder.add("block.dimdoors.dungeon_door.info1", "to activate that rift or place");
        builder.add("block.dimdoors.dungeon_door.info2", "anywhere else to create a");
        builder.add("block.dimdoors.dungeon_door.info3", "dungeon.");
        builder.add("block.dimdoors.gold_dimensional_door.info0", "Similar to a Dimensional Door");
        builder.add("block.dimdoors.gold_dimensional_door.info1", "but shinier");
        builder.add("block.dimdoors.quartz_dimensional_door.info", "Creates a pathway to your personal pocket.");
        builder.add("item.dimdoors.rift_blade.info0", "Opens temporary doors on rifts");
        builder.add("item.dimdoors.rift_blade.info1", "and has a teleport attack.");
        builder.add("item.dimdoors.rift_remover.info0", "Use near exposed rift");
        builder.add("item.dimdoors.rift_remover.info1", "to remove it and");
        builder.add("item.dimdoors.rift_remover.info2", "any nearby rifts.");
        builder.add("item.dimdoors.rift_signature.bound.info1", "Leads to (%d, %d, %d)");
        builder.add("item.dimdoors.rift_signature.bound.info0", "at dimension %d");
        builder.add("item.dimdoors.rift_signature.unbound.info0", "First click stores a location;.");
        builder.add("item.dimdoors.rift_signature.unbound.info1", "second click creates a pair of");
        builder.add("item.dimdoors.rift_signature.unbound.info2", "rifts linking the two locations.");
        builder.add("item.dimdoors.stabilized_rift_signature.bound.info0", "Leads to (%d, %d, %d)");
        builder.add("item.dimdoors.stabilized_rift_signature.bound.info1", "at dimension %d");
        builder.add("item.dimdoors.stabilized_rift_signature.unbound.info0", "First click stores a location,.");
        builder.add("item.dimdoors.stabilized_rift_signature.unbound.info1", "other clicks create rifts linking");
        builder.add("item.dimdoors.stabilized_rift_signature.unbound.info2", "the first and last locations together.");
        builder.add("item.dimdoors.unstable_dimensional_door.info", "Caution, Leads to random destination");
        builder.add("block.dimdoors.oak_dimensional_door.info0", "Place on the block under a rift.");
        builder.add("block.dimdoors.oak_dimensional_door.info1", "to create a portal, or place anywhere.");
        builder.add("block.dimdoors.oak_dimensional_door.info2", "in a pocket dimension to exit.");
        builder.add("item.dimdoors.creepy_record", "Music Disc");
        builder.add("item.dimdoors.creepy_record.desc", "Stevenrs11 - Creepy");
        builder.add("item.dimdoors.eternal_fluid_bucket", "Eternal Fluid Bucket");
        builder.add("item.dimdoors.leak_bucket", "Leak Bucket");
        builder.add("item.dimdoors.white_void_record", "Music Disc");
        builder.add("item.dimdoors.white_void_record.desc", "Lachney - White Void");
        builder.add("item.dimdoors.dimensional_eraser", "Dimensional Eraser");
        builder.add("item.dimdoors.dimensional_eraser.desc", "Erases entities");
        builder.add("item.dimdoors.monolith_spawner", "Monolith Spawner");
        builder.add("item.dimdoors.mask_wand", "Mask Wand");
        builder.add("item.dimdoors.mask_shard", "Mask Shard");
        builder.add("item.dimdoors.fuzzy_fireball", "Fuzzy Fireball");
        builder.add("item.dimdoors.fabric_of_finality", "Fabric of Finality");
        builder.add("item.dimdoors.liminal_lint", "Liminal Lint");
        builder.add("item.dimdoors.enduring_fibers", "Enduring Fibers");
        builder.add("item.dimdoors.rift_pearl", "Rift Pearl");
        builder.add("item.dimdoors.fabric_of_reality", "Fabric of Reality");
        builder.add("item.dimdoors.amalgam_lump", "Amalgam Lump");
        builder.add("item.dimdoors.clod", "Clod");
        builder.add("item.dimdoors.garment_of_reality_helmet", "Garment of Reality Helmet");
        builder.add("item.dimdoors.garment_of_reality_chestplate", "Garment of Reality Chestplate");
        builder.add("item.dimdoors.garment_of_reality_leggings", "Garment of Reality Leggings");
        builder.add("item.dimdoors.garment_of_reality_boots", "Garment of Reality Boots");
        builder.add("item.dimdoors.they_stare_back_record", "Music Disc");
        builder.add("item.dimdoors.they_stare_back_record.desc", "Firel - They Stare Back");

        builder.add("dimdoors.virtualTarget.dimdoors.available_link", "Random");
        builder.add("dimdoors.virtualTarget.dimdoors.escape", "Escape");
        builder.add("dimdoors.virtualTarget.dimdoors.global", "Global");
        builder.add("dimdoors.virtualTarget.dimdoors.limbo", "Limbo");
        builder.add("dimdoors.virtualTarget.dimdoors.local", "Local");
        builder.add("dimdoors.virtualTarget.dimdoors.public_pocket", "Public Pocket");
        builder.add("dimdoors.virtualTarget.dimdoors.pocket_entrance", "Pocket Entrance");
        builder.add("dimdoors.virtualTarget.dimdoors.pocket_exit", "Pocket Exit");
        builder.add("dimdoors.virtualTarget.dimdoors.private", "Private Pocket Entrance");
        builder.add("dimdoors.virtualTarget.dimdoors.private_pocket_exit", "Private Pocket Exit");
        builder.add("dimdoors.virtualTarget.dimdoors.relative", "Relative");
        builder.add("dimdoors.virtualTarget.dimdoors.id_marker", "Id Marker");
        builder.add("dimdoors.virtualTarget.dimdoors.unstable", "Unstable");
        builder.add("dimdoors.virtualTarget.dimdoors.none", "None");

        builder.add("fluid.dimdoors.eternal_fabric", "Eternal Fabric");

        builder.add("entity.dimdoors.monolith", "Monolith");

        builder.add("commands.dimteleport.usage", "/dimteleport <dimension> <x> <y> <z> [yaw] [pitch]");
        builder.add("commands.fabricconvert.usage", "/fabricconvert");
        builder.add("commands.fabricconvert.success", "All fabric of reality has been converted to black.");
        builder.add("commands.pocket.usage", "/pocket <group> <name> [setup]");
        builder.add("commands.pocket.group_not_found", "Group %s not found");
        builder.add("commands.dimdoors.pocket.template_not_found", "Template %s not found");
        builder.add("commands.dimdoors.saveschem.usage", "/saveschem <name>");
        builder.add("commands.dimdoors.saveschem.success", "Pocket %s has been successfully saved");
        builder.add("commands.generic.dimdoors.not_in_pocket_dim", "You must be in a pocket dimension to use this command.");
        builder.add("commands.generic.dimdoors.not_in_pocket", "You must be in a pocket to use this command.");
        builder.add("commands.generic.unknownValue", "Unknown value '%s'");
        builder.add("commands.pocket.unknownPocketTemplate", "Unknown Pocket Template '%s'");
        builder.add("commands.pocket.placedSchem", "Placed schematic %s at %s in world %s");
        builder.add("commands.pocket.loadedSchem", "Loaded schematic %s to clipboard. Paste it using //paste");
        builder.add("commands.pocket.log.creation.off", "Toggled logging of pocket creation off.");
        builder.add("commands.pocket.log.creation.on", "Toggled logging of pocket creation on.");
        builder.add("commands.pocket.log.creation.generating", "Generating pocket from template '%s' at location %s %s %s");

        builder.add("rifts.unlinked1", "This rift doesn't lead anywhere");
        builder.add("rifts.unlinked2", "This rift has closed");
        builder.add("rifts.isLocked", "This rift is locked");
        builder.add("rifts.cantUnlock", "Can't unlock this door");
        builder.add("rifts.unlocked", "Unlocked");
        builder.add("rifts.locked", "Locked");
        builder.add("rifts.destinations.escape.cannot_escape_limbo", "Nice try, but you'll need to either die or find some eternal fabric to get out of Limbo.");
        builder.add("rifts.destinations.escape.not_in_pocket_dim", "You can only use this to escape from a pocket dimension!");
        builder.add("rifts.destinations.escape.did_not_use_rift", "You didn't use a rift to enter the pocket dimension, so you ended up in Limbo!");
        builder.add("rifts.destinations.escape.rift_has_closed", "The rift you used to enter the pocket dimension has closed and you ended up in Limbo!");
        builder.add("rifts.destinations.private_pocket_exit.did_not_use_rift", "You didn't use a rift to enter the pocket dimension and you ended up in Limbo!");
        builder.add("rifts.destinations.private_pocket_exit.rift_has_closed", "The rift you used to enter the pocket dimension has closed and you ended up in Limbo!");
        builder.add("rifts.entrances.rift_too_close", "Placing a door this close to a tear in the world would be dangerous. Shift-right-click to place anyway, or place it on the rift's core (tesseract) to bind it to the rift.");
        builder.add("rifts.entrances.cannot_be_placed_on_rift", "This type of door can't be placed on a rift.");

        builder.add("tools.rift_miss", "You can only use this item on a rift's core");
        builder.add("tools.signature_blocked", "Usage of the signature was block");
        builder.add("tools.target_became_block", "Failed, there is now a block at the stored location");

        builder.add("text.autoconfig.dimdoors.title", "Dimensional Doors");
        builder.add("text.autoconfig.dimdoors.category.general", "General Settings");
        builder.add("text.autoconfig.dimdoors.option.general.depthSpreadFactor", "Depth Spread Factor");
        builder.add("text.autoconfig.dimdoors.option.general.depthSpreadFactor.@Tooltip", "The scale of the dispersion when escaping from a pocket or limbo, in blocks/depth. Limbo is treated as depth 50.");
        builder.add("text.autoconfig.dimdoors.option.general.riftCloseSpeed", "Rift Close Speed");
        builder.add("text.autoconfig.dimdoors.option.general.riftCloseSpeed.@Tooltip", "The speed at which rifts close when using the rift remover, in units of rift size per tick.");
        builder.add("text.autoconfig.dimdoors.option.general.riftGrowthSpeed", "Rift Growth Speed");
        builder.add("text.autoconfig.dimdoors.option.general.riftGrowthSpeed.@Tooltip", "The speed at which rifts grow, in units of rift size per tick.");
        builder.add("text.autoconfig.dimdoors.option.general.enableRiftDecay", "Rift Growth Speed");
        builder.add("text.autoconfig.dimdoors.option.general.enableRiftDecay.@Tooltip", "When true, blocks around a growing rift will unravel over time.");
        builder.add("text.autoconfig.dimdoors.option.general.teleportOffset", "Teleport Offset");
        builder.add("text.autoconfig.dimdoors.option.general.teleportOffset.@Tooltip", "Distance in blocks to teleport the player in front of the dimensional door.");
        builder.add("text.autoconfig.dimdoors.option.general.riftBoundingBoxInCreative", "Rift Bounding Box in Creative");
        builder.add("text.autoconfig.dimdoors.option.general.riftBoundingBoxInCreative.@Tooltip", "When true, shows the bounding boxes of floating rifts when the player is in creative.");
        builder.add("text.autoconfig.dimdoors.option.general.endermanSpawnChance", "Enderman spawn chance");
        builder.add("text.autoconfig.dimdoors.option.general.endermanSpawnChance.@Tooltip", "The chance that an enderman spawns at a detached rift.");
        builder.add("text.autoconfig.dimdoors.option.general.endermanAggressiveChance", "Enderman aggressive chance");
        builder.add("text.autoconfig.dimdoors.option.general.endermanAggressiveChance.@Tooltip", "The chance that an enderman spawned by a detached rift attacks the closest player.");
        builder.add("text.autoconfig.dimdoors.option.general.enableDebugMessages", "Enable Debug Messages");
        builder.add("text.autoconfig.dimdoors.option.general.enableDebugMessages.@Tooltip", "When true, debug messages will be printed..");



        builder.add("text.autoconfig.dimdoors.category.doors", "Doors Settings");
        builder.add("text.autoconfig.dimdoors.option.doors.closeDoorBehind", "Close Door Behind");
        builder.add("text.autoconfig.dimdoors.option.doors.closeDoorBehind.@Tooltip", "When true, Dimensional Doors will automatically close when the player enters their portal.");
        builder.add("text.autoconfig.dimdoors.option.doors.doorList", "Doors");
        builder.add("text.autoconfig.dimdoors.option.doors.doorList.@Tooltip", "Set overrides for enabling/disabling certain doors");
        builder.add("text.autoconfig.dimdoors.option.doors.doorList.mode", "Mode");
        builder.add("text.autoconfig.dimdoors.option.doors.doorList.mode.@Tooltip", "Enable - Only generate dimensional variants of these doors. Disable - Prevent generating dimensional variants of these doors");
        builder.add("text.autoconfig.dimdoors.option.doors.doorList.doors", "Doors");
        builder.add("text.autoconfig.dimdoors.option.doors.doorList.doors.@Tooltip", "A list of block ids for doors. If the door's item id is different than the block id, add that as well.");
        builder.add("text.autoconfig.dimdoors.option.doors.placeRiftsInCreativeMode", "Place Rifts in Creative Mode");
        builder.add("text.autoconfig.dimdoors.option.doors.placeRiftsInCreativeMode.@Tooltip", "If enabled, breaking a door in creative mode will spawn a rift");


        builder.add("text.autoconfig.dimdoors.category.pockets", "Pocket Settings");
        builder.add("text.autoconfig.dimdoors.option.pockets.pocketGridSize", "Pocket Grid Size");
        builder.add("text.autoconfig.dimdoors.option.pockets.pocketGridSize.@Tooltip", "Sets how many chunks apart all pockets in any pocket dimensions should be placed.");
        builder.add("text.autoconfig.dimdoors.option.pockets.maxPocketSize", "Maximum Pocket Size");
        builder.add("text.autoconfig.dimdoors.option.pockets.maxPocketSize.@Tooltip", "Sets the maximum size of any pocket. A size of x will allow for pockets up to (x + 1) * (x + 1) chunks.");
        builder.add("text.autoconfig.dimdoors.option.pockets.privatePocketSize", "Private Pocket Size");
        builder.add("text.autoconfig.dimdoors.option.pockets.privatePocketSize.@Tooltip", "Sets the minimum size of a newly created Private Pocket. If this is set to any value bigger than maxPocketSize, the value of maxPocketSize will be used instead.");
        builder.add("text.autoconfig.dimdoors.option.pockets.publicPocketSize", "Public Pocket Size");
        builder.add("text.autoconfig.dimdoors.option.pockets.publicPocketSize.@Tooltip", "Sets the minimum size of a newly created Public Pocket. If this is set to any value bigger than privatePocketSize, the value of privatePocketSize will be used instead.");
        builder.add("text.autoconfig.dimdoors.option.pockets.defaultWeightEquation", "Default Weight Equation");
        builder.add("text.autoconfig.dimdoors.option.pockets.defaultWeightEquation.@Tooltip", "Sets the equation to be used to compute weight when there is no / invalid weight equation present in the pocket generator json");
        builder.add("text.autoconfig.dimdoors.option.pockets.fallbackWeight", "Fallback weight");
        builder.add("text.autoconfig.dimdoors.option.pockets.fallbackWeight.@Tooltip", "Sets the fallback weight to be used if the default weight equation fails.");
        builder.add("text.autoconfig.dimdoors.option.pockets.classicPocketsResourcePackActivationType", "Classic Resource Pack Activation Type");
        builder.add("text.autoconfig.dimdoors.option.pockets.classicPocketsResourcePackActivationType.@Tooltip", "Default - Disabled but can be enabled, Default Enabled - Enabled but can be disabled, Always Enabled - Can not be disabled");
        builder.add("text.autoconfig.dimdoors.option.pockets.defaultPocketsResourcePackActivationType", "Default Resource Pack Activation Type");
        builder.add("text.autoconfig.dimdoors.option.pockets.asyncWorldEditPocketLoading", "Async WorldEdit Pocket Loading");
        builder.add("text.autoconfig.dimdoors.option.pockets.asyncWorldEditPocketLoading.@Tooltip", "Sets loading pockets to your WorldEdit clipboard asynchronous or synchronous. Only affects when WorldEdit is installed.");
        builder.add("text.autoconfig.dimdoors.option.pockets.canUseRiftSignatureInPrivatePockets", "Can use Rift Signature in Private Pockets");
        builder.add("text.autoconfig.dimdoors.option.pockets.canUseRiftSignatureInPrivatePockets.@Tooltip", "If Enabled, rift signatures can be used within private pockets.");

        builder.add("dimdoors.pocket.dyeAlreadyAbsorbed", "The pocket is already that color, so the rift didn't absorb the dye.");
        builder.add("dimdoors.pocket.pocketHasBeenDyed", "The pocket has been dyed %s.");
        builder.add("dimdoors.pocket.remainingNeededDyes", "The pocket has %s/%s of the dyes needed to be colored %s.");

        builder.add("text.autoconfig.dimdoors.category.world", "Worldgen Settings");
        builder.add("text.autoconfig.dimdoors.option.world.clusterGenChance", "Cluster Generation Chance");
        builder.add("text.autoconfig.dimdoors.option.world.clusterGenChance.@Tooltip", "Sets the chance (out of 1) that a cluster of rifts will generate in a given chunk.");
        builder.add("text.autoconfig.dimdoors.option.world.gatewayGenChance", "Gateway Generation Chance");
        builder.add("text.autoconfig.dimdoors.option.world.gatewayGenChance.@Tooltip", "Sets the chance (out of 1) that a dimensional gateway will generate in a given chunk.");
        builder.add("text.autoconfig.dimdoors.option.world.clusterDimBlacklist", "Cluster Dimension Blacklist");
        builder.add("text.autoconfig.dimdoors.option.world.clusterDimBlacklist.@Tooltip", "Dimension Blacklist for the generation of Rift Scar clusters. Add a dimension ID here to prevent generation in certain dimensions.");
        builder.add("text.autoconfig.dimdoors.option.world.gatewayDimBlacklist", "Gateway Dimension Blacklist");
        builder.add("text.autoconfig.dimdoors.option.world.gatewayDimBlacklist.@Tooltip", "Dimension Blacklist for the generation of Dimensional Portal gateways. Add a dimension ID here to prevent generation in certain dimensions.");

        builder.add("text.autoconfig.dimdoors.category.dungeons", "Dungeon Settings");
        builder.add("text.autoconfig.dimdoors.option.dungeons.maxDungeonDepth", "Maximum Dungeon Depth");
        builder.add("text.autoconfig.dimdoors.option.dungeons.maxDungeonDepth.@Tooltip", "The depth at which limbo is located. If a Rift reaches any deeper than this while searching for a new destination, the player trying to enter the Rift will be sent straight to Limbo.");

        builder.add("text.autoconfig.dimdoors.category.monoliths", "Monolith Settings");
        builder.add("text.autoconfig.dimdoors.option.monoliths.dangerousLimboMonoliths", "Dangerous Limbo Monoliths");
        builder.add("text.autoconfig.dimdoors.option.monoliths.dangerousLimboMonoliths.@Tooltip", "When true, Monoliths in Limbo attack the player and deal damage.");
        builder.add("text.autoconfig.dimdoors.option.monoliths.monolithTeleportation", "Monolith Teleportation");
        builder.add("text.autoconfig.dimdoors.option.monoliths.monolithTeleportation.@Tooltip", "When true, being exposed to the gaze of Monoliths for too long, will cause the player to be teleported to the void above Limbo.");

        builder.add("text.autoconfig.dimdoors.category.limbo", "Limbo Settings");
        builder.add("text.autoconfig.dimdoors.option.limbo.universalLimbo", "Universal Limbo");
        builder.add("text.autoconfig.dimdoors.option.limbo.universalLimbo.@Tooltip", "When true, players are also teleported to Limbo when they die in any non-Pocket Dimension (except Limbo itself). Otherwise, players only go to Limbo if they die in a Pocket Dimension.");
        builder.add("text.autoconfig.dimdoors.option.limbo.hardcoreLimbo", "Hardcore Limbo");
        builder.add("text.autoconfig.dimdoors.option.limbo.hardcoreLimbo.@Tooltip", "When true, a player dying in Limbo will respawn in Limbo, making Eternal Fluid or Golden Dimensional Doors the only way to escape Limbo.");
        builder.add("text.autoconfig.dimdoors.option.limbo.limboBlocksCorruptingExitWorldAmount", "Exit World Decay Radius");
        builder.add("text.autoconfig.dimdoors.option.limbo.limboBlocksCorruptingExitWorldAmount.@Tooltip", "The radius around a player in which blocks can decay upon exiting limbo.");
        builder.add("text.autoconfig.dimdoors.option.limbo.worldsLeadingToLimbo", "Worlds Leading to Limbo");
        builder.add("text.autoconfig.dimdoors.option.limbo.worldsLeadingToLimbo.@Tooltip", "Defines a blacklist/whitelist of worlds that will send the player to limbo upon death.");
        builder.add("text.autoconfig.dimdoors.option.limbo.worldsLeadingToLimbo.list", "List of world ids");
        builder.add("text.autoconfig.dimdoors.option.limbo.worldsLeadingToLimbo.list.@Tooltip", "List of the ids for worlds in the blacklsit/whitelist.");
        builder.add("text.autoconfig.dimdoors.option.limbo.worldsLeadingToLimbo.blacklist", "Is it a blacklist?");
        builder.add("text.autoconfig.dimdoors.option.limbo.worldsLeadingToLimbo.blacklist.@Tooltip", "Boolean that determines if list is a blacklist or white list for worlds.");
        builder.add("text.autoconfig.dimdoors.option.limbo.limboReturnDistance.@Tooltip", "Distance from spawn that limbo returns you");
        builder.add("text.autoconfig.dimdoors.option.limbo.limboReturnDistance", "Limbo Return Radius");
        builder.add("text.autoconfig.dimdoors.option.limbo.escapeTargetWorld", "Escape To World");
        builder.add("text.autoconfig.dimdoors.option.limbo.escapeTargetWorld.@Tooltip", "Defines the id of the world players will spawn in upon exiting Limbo.  Leaving this blank will spawn players in the world their respawn point is in.");
        builder.add("text.autoconfig.dimdoors.option.limbo.escapeTargetWorldYSpawn", "Escape To World Y Level");
        builder.add("text.autoconfig.dimdoors.option.limbo.escapeTargetWorldYSpawn.@Tooltip", "Defines the Y coordinate the player will spawn at when using \"Escape To World\"");
        builder.add("text.autoconfig.dimdoors.option.limbo.escapeToWorldSpawn", "Escape to World Spawn");
        builder.add("text.autoconfig.dimdoors.option.limbo.escapeToWorldSpawn.@Tooltip", "Boolean that determines if players exiting limbo will return relative to the worldspawn instead.  If true, escapeTargetWorld has no effect.");
        builder.add("text.autoconfig.dimdoors.option.limbo.limboReturnDistanceMax", "Max Limbo Return Distance");
        builder.add("text.autoconfig.dimdoors.option.limbo.limboReturnDistanceMax.@Tooltip", "Defines the maximum distance out the possible return locations can be from the target center.\n Setting both return distances to 0 cause the player to exactly appear at target center.");
        builder.add("text.autoconfig.dimdoors.option.limbo.limboReturnDistanceMin", "Min Limbo Return Distance");
        builder.add("text.autoconfig.dimdoors.option.limbo.limboReturnDistanceMin.@Tooltip","Defines the minimum distance out the possible return locations can be from the target center.\n Setting both return distances to 0 cause the player to exactly appear at target center.");
        builder.add("text.autoconfig.dimdoors.option.limbo.decaySurroundings", "Decay Surroundings");
        builder.add("text.autoconfig.dimdoors.option.limbo.decaySurroundings.@Tooltip", "Does escaping limbo cause limbo decay around the location?");
        builder.add("text.autoconfig.dimdoors.option.limbo.tryPlayerBedSpawn", "Try Player Bed Spawn");
        builder.add("text.autoconfig.dimdoors.option.limbo.tryPlayerBedSpawn.@Tooltip", "When true, the bed spawn of the player will be used as the center of possible return locations if available.");
        builder.add("text.autoconfig.dimdoors.option.limbo.defaultToWorldSpawn", "Default To World Spawn");
        builder.add("text.autoconfig.dimdoors.option.limbo.defaultToWorldSpawn.@Tooltip", "When true, the world spawn of the world the player is escaping from limbo to will be used as the center of possible return location.");




        builder.add("text.autoconfig.dimdoors.category.graphics", "Graphics Settings");
        builder.add("text.autoconfig.dimdoors.option.graphics.highlightRiftCoreFor", "Time to Highlight Rift Core");
        builder.add("text.autoconfig.dimdoors.option.graphics.highlightRiftCoreFor.@Tooltip", "How long, in milliseconds, the rift's core (tesseract animation) should be shown for when attempting to place a door near a large rift but not directly on it. Set to -1 to disable.");
        builder.add("text.autoconfig.dimdoors.option.graphics.showRiftCore", "Always Show Rift Cores");
        builder.add("text.autoconfig.dimdoors.option.graphics.showRiftCore.@Tooltip", "Set this to true to always show rifts' cores (tesseract animation).");
        builder.add("text.autoconfig.dimdoors.option.graphics.riftSize", "Rift Size");
        builder.add("text.autoconfig.dimdoors.option.graphics.riftSize.@Tooltip", "Multiplier affecting how large rifts should be rendered, 1 being the default size.");
        builder.add("text.autoconfig.dimdoors.option.graphics.riftJitter", "Rift Jitter");
        builder.add("text.autoconfig.dimdoors.option.graphics.riftJitter.@Tooltip", "Multiplier affecting how much rifts should jitter, 1 being the default size.");

        builder.add("text.autoconfig.dimdoors.category.decay", "Decay Settings");
        builder.add("text.autoconfig.dimdoors.option.decay.decaySpreadChance", "Decay Spread Chance");
        builder.add("text.autoconfig.dimdoors.option.decay.decaySpreadChance.@Tooltip", "To be filled out.");
        builder.add("text.autoconfig.dimdoors.option.decay.decayDelay", "Decay Delay");
        builder.add("text.autoconfig.dimdoors.option.decay.decayDelay.@Tooltip", "In minecraft ticks (20 per second on a healthy server or game), the delay between when a queued decay is scheduled and it fired.");

        builder.add("argument.dimdoors.schematic.invalidNamespace", "Invalid schematic namespace. Expected one of %s, found %s.");
        builder.add("command.dimdoors.schematicv2.unknownSchematic", "Unknown schematic \"%s\" in namespace \"%s\" ");
        builder.add("dimdoors.destination", "Destination type");

        builder.add("dimdoors.advancement.root", "Dimensional Doors");
        builder.add("dimdoors.advancement.root.desc", "Venture into the depths");
        builder.add("dimdoors.advancement.dark_ostiology", "Dark Ostiology");
        builder.add("dimdoors.advancement.dark_ostiology.desc", "Place an Oak Dimensional Door");
        builder.add("dimdoors.advancement.darklight", "Darklight");
        builder.add("dimdoors.advancement.darklight.desc", "Obtain Fabric of Reality");
        builder.add("dimdoors.advancement.door_to_adventure", "Door to Adventure");
        builder.add("dimdoors.advancement.door_to_adventure.desc", "Enter a dungeon");
        builder.add("dimdoors.advancement.enter_limbo", "Limbo");
        builder.add("dimdoors.advancement.enter_limbo.desc", "Enter Limbo");
        builder.add("dimdoors.advancement.hole_in_the_sky", "Hole in the Sky");
        builder.add("dimdoors.advancement.hole_in_the_sky.desc", "Encounter a Rift");
        builder.add("dimdoors.advancement.home_away_from_home", "Home away from Home");
        builder.add("dimdoors.advancement.home_away_from_home.desc", "Enter your private pocket");
        builder.add("dimdoors.advancement.lost_and_found", "Lost and Found");
        builder.add("dimdoors.advancement.lost_and_found.desc", "Open a chest in a Dungeon");
        builder.add("dimdoors.advancement.out_of_time", "Out of Time");
        builder.add("dimdoors.advancement.out_of_time.desc", "Set your spawn point in a pocket dimension");
        builder.add("dimdoors.advancement.public_pocket", "Public Pocket");
        builder.add("dimdoors.advancement.public_pocket.desc", "Enter a Public Pocket");
        builder.add("dimdoors.advancement.string_theory", "String Theory");
        builder.add("dimdoors.advancement.string_theory.desc", "Collect World Thread");
        builder.add("dimdoors.advancement.world_unfurled", "World Unfurled");
        builder.add("dimdoors.advancement.world_unfurled.desc", "Collect Unravelled Fabric");
        builder.add("dimdoors.advancement.unravelled_but_immutable", "Unravelled But Immutable");
        builder.add("dimdoors.advancement.unravelled_but_immutable.desc", "Obtain Infrangible Fiber");
        builder.add("dimdoors.advancement.fuzzy_unreality", "Fuzzy Unreality");
        builder.add("dimdoors.advancement.fuzzy_unreality.desc", "Obtain Frayed Filament");

        builder.add("biome.dimdoors.black_void", "Black void (Public Pockets)");
        builder.add("biome.dimdoors.dangerous_black_void", "Dangerous Black void (Dungeon Pockets)");
        builder.add("biome.dimdoors.limbo", "Limbo");
        builder.add("biome.dimdoors.white_void", "White void (Private Pockets)");

        builder.add("limbo.death.fell.accident.ladder", "%1$s fell off a ladder and fell into limbo");
        builder.add("limbo.death.fell.accident.vines", "%1$s fell off some vines and fell into limbo");
        builder.add("limbo.death.fell.accident.weeping_vines", "%1$s fell off some weeping vines and fell into limbo");
        builder.add("limbo.death.fell.accident.twisting_vines", "%1$s fell off some twisting vines and fell into limbo");
        builder.add("limbo.death.fell.accident.scaffolding", "%1$s fell off scaffolding and fell into limbo");
        builder.add("limbo.death.fell.accident.other_climbable", "%1$s fell while climbing and fell into limbo");
        builder.add("limbo.death.fell.accident.generic", "%1$s fell from a high place and fell into limbo");
        builder.add("limbo.death.fell.killer", "%1$s was doomed to fall and fell into limbo");
        builder.add("limbo.death.fell.assist", "%1$s was doomed to fall by %2$s and fell into limbo");
        builder.add("limbo.death.fell.assist.item", "%1$s was doomed to fall by %2$s using %3$s and fell into limb");
        builder.add("limbo.death.fell.finish", "%1$s fell too far and was sent to limbo by %2$s");
        builder.add("limbo.death.fell.finish.item", "%1$s fell too far and was finished by %2$s using %3$s and fell into limbo");
        builder.add("limbo.death.attack.lightningBolt", "%1$s was struck by lightning and was sent to limbo");
        builder.add("limbo.death.attack.lightningBolt.player", "%1$s was struck by lightning whilst fighting %2$s and was sent to limbo");
        builder.add("limbo.death.attack.inFire", "%1$s went to Limbo in flames");
        builder.add("limbo.death.attack.inFire.player", "%1$s walked into fire whilst fighting %2$s and was sent to Limbo");
        builder.add("limbo.death.attack.onFire", "%1$s burned to Limbo");
        builder.add("limbo.death.attack.onFire.player", "%1$s was burnt to a crisp whilst fighting %2$s and was sent tp Limbo");
        builder.add("limbo.death.attack.lava", "%1$s tried to swim in lava and sank into Limbo and sank into Limbo");
        builder.add("limbo.death.attack.lava.player", "%1$s tried to swim in lava to escape %2$s and sank into Limbo");
        builder.add("limbo.death.attack.hotFloor", "%1$s discovered the floor was lava and sank into Limbo");
        builder.add("limbo.death.attack.hotFloor.player", "%1$s walked into danger zone due to %2$s and sank into Limbo");
        builder.add("limbo.death.attack.inWall", "%1$s suffocated into Limbo");
        builder.add("limbo.death.attack.inWall.player", "%1$s suffocated into Limbo whilst fighting %2$s");
        builder.add("limbo.death.attack.cramming", "%1$s was squished too much ans sent to Limbo");
        builder.add("limbo.death.attack.cramming.player", "%1$s was squashed by %2$s");
        builder.add("limbo.death.attack.drown", "%1$s drowned and sank into Limbo");
        builder.add("limbo.death.attack.drown.player", "%1$s drowned whilst trying to escape %2$s and sank into Limbo");
        builder.add("limbo.death.attack.starve", "%1$s starved to death and shriveled into Limbo");
        builder.add("limbo.death.attack.starve.player", "%1$s starved to death whilst fighting %2$s and shriveled into Limbo");
        builder.add("limbo.death.attack.cactus", "%1$s pricked a hole in reality");
        builder.add("limbo.death.attack.cactus.player", "%1$s walked into a cactus whilst trying to escape %2$s and was sent to Limbo");
        builder.add("limbo.death.attack.generic", "%1$s was sent to Limbo");
        builder.add("limbo.death.attack.generic.player", "%1$s was sent to Limbo because of %2$s");
        builder.add("limbo.death.attack.explosion", "%1$s was blown to Limbo");
        builder.add("limbo.death.attack.explosion.player", "%1$s was blown to Limbo by %2$s");
        builder.add("limbo.death.attack.explosion.player.item", "%1$s was blown to Limbo by %2$s using %3$s");
        builder.add("limbo.death.attack.magic", "%1$s was cast into Limbo by magic");
        builder.add("limbo.death.attack.magic.player", "%1$s was cast into Limbo by magic whilst trying to escape %2$s");
        builder.add("limbo.death.attack.even_more_magic", "%1$s was cast into Limbo by even more magic");
        builder.add("limbo.death.attack.message_too_long", "Actually, message was too long to deliver fully. Sorry! Here's stripped version, %s");
        builder.add("limbo.death.attack.wither", "%1$s withered into Limbo");
        builder.add("limbo.death.attack.wither.player", "%1$s withered into Limbo whilst fighting %2$s");
        builder.add("limbo.death.attack.witherSkull", "%1$s was shot by a skull into Limbo from %2$s");
        builder.add("limbo.death.attack.anvil", "%1$s was squashed into Limbo by a falling anvil");
        builder.add("limbo.death.attack.anvil.player", "%1$s was squashed into Limbo by a falling anvil whilst fighting %2$s");
        builder.add("limbo.death.attack.fallingBlock", "%1$s was squashed into Limbo by a falling block");
        builder.add("limbo.death.attack.fallingBlock.player", "%1$s was squashed into Limbo by a falling block whilst fighting %2$s");
        builder.add("limbo.death.attack.stalagmite", "%1$s was impaled into Limbo on a stalagmite");
        builder.add("limbo.death.attack.stalagmite.player", "%1$s was impaled into Limbo on a stalagmite whilst fighting %2$s");
        builder.add("limbo.death.attack.fallingStalactite", "%1$s was skewered into Limbo by a falling stalactite");
        builder.add("limbo.death.attack.fallingStalactite.player", "%1$s was skewered into Limbo by a falling stalactite whilst fighting %2$s");
        builder.add("limbo.death.attack.mob", "%1$s was slain by %2$s and was sent to Limbo");
        builder.add("limbo.death.attack.mob.item", "%1$s was slain by %2$s using %3$s and was sent to Limbo");
        builder.add("limbo.death.attack.player", "%1$s was slain by %2$s and was sent to Limbo");
        builder.add("limbo.death.attack.player.item", "%1$s was slain by %2$s using %3$s and was sent to Limbo");
        builder.add("limbo.death.attack.arrow", "%1$s was shot by %2$s and was sent to Limbo");
        builder.add("limbo.death.attack.arrow.item", "%1$s was shot by %2$s using %3$s and was sent to Limbo");
        builder.add("limbo.death.attack.fireball", "%1$s was fireballed into Limbo by %2$s");
        builder.add("limbo.death.attack.fireball.item", "%1$s was fireballed into Limbo by %2$s using %3$s");
        builder.add("limbo.death.attack.thrown", "%1$s was pummeled into Limbo by %2$s");
        builder.add("limbo.death.attack.thrown.item", "%1$s was pummeled into Limbo by %2$s using %3$s");
        builder.add("limbo.death.attack.indirectMagic", "%1$s was killed by %2$s using magic and was sent to Limbo");
        builder.add("limbo.death.attack.indirectMagic.item", "%1$s was sent by %2$s using %3$s and was sent to Limbo");
        builder.add("limbo.death.attack.thorns", "%1$s was sent to Limbo trying to hurt %2$s");
        builder.add("limbo.death.attack.thorns.item", "%1$s was sent to Limbo by %3$s trying to hurt %2$s");
        builder.add("limbo.death.attack.trident", "%1$s was impaled by %2$s into Limbo");
        builder.add("limbo.death.attack.trident.item", "%1$s was impaled by %2$s with %3$s into Limbo");
        builder.add("limbo.death.attack.fall", "%1$s hit the ground too hard and dropped into Limbo");
        builder.add("limbo.death.attack.fall.player", "%1$s hit the ground too hard whilst trying to escape %2$s and dropped into Limbo");
        builder.add("limbo.death.attack.outOfWorld", "%1$s fell into Limbo");
        builder.add("limbo.death.attack.outOfWorld.player", "%1$s didn't want to live in the same world as %2$s and went to Limbo");
        builder.add("limbo.death.attack.dragonBreath", "%1$s was roasted in dragon breath and was sent to Limbo");
        builder.add("limbo.death.attack.dragonBreath.player", "%1$s was roasted in dragon breath by %2$s and was sent to Limbo");
        builder.add("limbo.death.attack.flyIntoWall", "%1$s experienced kinetic energy and flew into Limbo");
        builder.add("limbo.death.attack.flyIntoWall.player", "%1$s experienced kinetic energy whilst trying to escape %2$s and flew into Limbo");
        builder.add("limbo.death.attack.fireworks", "%1$s went into Limbo with a bang");
        builder.add("limbo.death.attack.fireworks.player", "%1$s went into Limbo with a bang whilst fighting %2$s");
        builder.add("limbo.death.attack.fireworks.item", "%1$s went into Limbo with a bang due to a firework fired from %3$s by %2$s");
        builder.add("limbo.death.attack.badRespawnPoint.message", "%1$s was killed by %2$s and was sent by Limbo");
        builder.add("limbo.death.attack.badRespawnPoint.link", "Intentional Game Design");
        builder.add("limbo.death.attack.sweetBerryBush", "%1$s poked a hole in reality");
        builder.add("limbo.death.attack.sweetBerryBush.player", "%1$s poked a hole in reality whilst trying to escape %2$s");
        builder.add("limbo.death.attack.sting", "%1$s bugged out to Limbo");
        builder.add("limbo.death.attack.sting.player", "%1$s bugged out to Limbo by %2$s");
        builder.add("limbo.death.attack.freeze", "%1$s froze into Limbo");
        builder.add("limbo.death.attack.freeze.player", "%1$s was frozen into Limbo by %2$s");

        builder.add("limbo.exit.eternal_fluid", "%1$s bathed in reality");
        builder.add("limbo.exit.generic", "%1$s escaped Limbo");
        builder.add("limbo.exit.rift", "%1$s found a rift leading out of Limbo");
        builder.add("stat.dimdoors.deaths_in_pocket", "Deaths in Pocket");
        builder.add("stat.dimdoors.times_been_to_dungeon", "Times been to Dungeon");
        builder.add("stat.dimdoors.times_sent_to_limbo", "Times sent to Limbo");
        builder.add("stat.dimdoors.times_teleported_by_monolith", "Times teleported by Monolith");

        builder.add("resourcePackActivationType.normal", "Normal");
        builder.add("resourcePackActivationType.defaultEnabled", "Default Enabled");
        builder.add("resourcePackActivationType.alwaysEnabled", "Always Enabled");

        builder.add("enchantment.dimdoors.string_theory", "String Theory");

        builder.add("dimdoors.mode.enable", "Enable");
        builder.add("dimdoors.mode.disable", "Disable");

        builder.add("category.dimdoors.tesselating", "Tesselating");
        builder.add("category.dimdoors.decays_into", "Decays Into");

        add(builder, ModPaintings.LIMBO, "Limbo", "Waterpicker");
        add(builder, ModPaintings.PORTAL, "Portal", "timetravellingBlockhead");
        add(builder, ModPaintings.EYES, "Eyes", "Anims");
        add(builder, ModPaintings.FREEDOM, "Freedom", "ImprovInAFedora");
        add(builder, ModPaintings.GATEWAY_AT_NIGHT, "Gateway At Night", "timetravellingBlockhead");

        add(builder, ModFluids.ETERNAL_FLUID, "Eternal Fluid");
        add(builder, ModFluids.FLOWING_ETERNAL_FLUID, "Flowing Eternal Fluid");
        add(builder, ModFluids.LEAK, "Leak");
        add(builder, ModFluids.FLOWING_LEAK, "Flowing Leak");
    }

    private void add(TranslationBuilder builder, ResourceKey<PaintingVariant> key, String name, String author) {
        var baseLang = key.location().toLanguageKey("painting");
        builder.add(baseLang + ".title", name);
        builder.add(baseLang + ".author", author);
    }

    private void add(TranslationBuilder builder, Fluid supplier, String contents) {
        builder.add(BuiltInRegistries.FLUID.getKey(supplier).toLanguageKey("fluid"), contents);
    }

    private void addBlockSet(TranslationBuilder builder, ModBlocks.DecayGroupSet set) {
        add(builder, set.fence());
        add(builder, set.gate());
        add(builder, set.button());
        add(builder, set.slab());
        add(builder, set.stairs());
        add(builder, set.wall());
    }

    private void add(TranslationBuilder builder, Object object) {
        if(object instanceof Block block) {
            var string = capitialize(BuiltInRegistries.BLOCK.getKey(block).getPath());
            builder.add(block, string);
        } else if(object instanceof CreativeModeTab tab) {
            var string = capitialize(BuiltInRegistries.CREATIVE_MODE_TAB.getKey(tab).getPath());
            add(builder, tab.getDisplayName(), string);
        }
    }

    private void add(TranslationBuilder builder, Component component, String string) {
        if(component.getContents() instanceof TranslatableContents translatable) {
            builder.add(translatable.getKey(), string);            
        }
    }

    public String capitialize(String name) {
        return Stream.of(name.split("_")).map(StringUtils::capitalize).collect(Collectors.joining(" "));
    }
}
