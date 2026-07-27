package org.dimdev.dimdoors.datagen;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import org.apache.commons.lang3.StringUtils;
import org.dimdev.dimdoors.ModRegistries;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.enchantment.ModEnchants;
import org.dimdev.dimdoors.entity.ModEntityTypes;
import org.dimdev.dimdoors.entity.stat.ModStats;
import org.dimdev.dimdoors.fluid.ModFluids;
import org.dimdev.dimdoors.item.ArmorSet;
import org.dimdev.dimdoors.item.ModItems;
import org.dimdev.dimdoors.painting.ModPaintings;
import org.dimdev.dimdoors.rift.targets.EscapeTarget;
import org.dimdev.dimdoors.rift.targets.VirtualTarget;
import org.dimdev.dimdoors.world.ModBiomes;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


//TODO: convert to proper DSL for the lulz when/if I decide to convert to kotlin.
public class LanguageProvider extends AbstractLanguageProvider {
    protected LanguageProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, registryLookup, "en_us");
    }



    @Override
    public void generateTranslations() {
        add(ModItems.DECAY);
        add(ModItems.DIMENSIONAL_DOORS);

        add(ModBlocks.GOLD_DOOR);
        add(ModBlocks.QUARTZ_DOOR);
        add(ModBlocks.STONE_DOOR);
        add(ModBlocks.DIMENSIONAL_PORTAL);
        add(ModBlocks.BLACK_FABRIC);
        add(ModBlocks.WHITE_FABRIC);
        add(ModBlocks.ORANGE_FABRIC);
        add(ModBlocks.MAGENTA_FABRIC);
        add(ModBlocks.LIGHT_BLUE_FABRIC);
        add(ModBlocks.LIGHT_GRAY_FABRIC);
        add(ModBlocks.YELLOW_FABRIC);
        add(ModBlocks.LIME_FABRIC);
        add(ModBlocks.PINK_FABRIC);
        add(ModBlocks.GRAY_FABRIC);
        add(ModBlocks.CYAN_FABRIC);
        add(ModBlocks.PURPLE_FABRIC);
        add(ModBlocks.BLUE_FABRIC);
        add(ModBlocks.BROWN_FABRIC);
        add(ModBlocks.GREEN_FABRIC);
        add(ModBlocks.RED_FABRIC);
        add(ModBlocks.BLACK_ANCIENT_FABRIC);
        add(ModBlocks.WHITE_ANCIENT_FABRIC);
        add(ModBlocks.ORANGE_ANCIENT_FABRIC);
        add(ModBlocks.MAGENTA_ANCIENT_FABRIC);
        add(ModBlocks.LIGHT_BLUE_ANCIENT_FABRIC);
        add(ModBlocks.LIGHT_GRAY_ANCIENT_FABRIC);
        add(ModBlocks.YELLOW_ANCIENT_FABRIC);
        add(ModBlocks.LIME_ANCIENT_FABRIC);
        add(ModBlocks.PINK_ANCIENT_FABRIC);
        add(ModBlocks.GRAY_ANCIENT_FABRIC);
        add(ModBlocks.CYAN_ANCIENT_FABRIC);
        add(ModBlocks.PURPLE_ANCIENT_FABRIC);
        add(ModBlocks.BLUE_ANCIENT_FABRIC);
        add(ModBlocks.BROWN_ANCIENT_FABRIC);
        add(ModBlocks.GREEN_ANCIENT_FABRIC);
        add(ModBlocks.RED_ANCIENT_FABRIC);
        add(ModBlocks.DECAYED_BLOCK);
        add(ModBlocks.UNFOLDED_BLOCK);
        add(ModBlocks.UNWARPED_BLOCK);
        add(ModBlocks.UNRAVELLED_BLOCK);
        add(ModBlocks.UNRAVELLED_FABRIC);
        add(ModBlocks.DETACHED_RIFT);
        add(ModBlocks.ETERNAL_FLUID);
        add(ModBlocks.SOLID_STATIC);
        add(ModBlocks.TESSELATING_LOOM);
        add(ModBlocks.REALITY_SPONGE);
        add(ModBlocks.DRIFTWOOD_WOOD);
        add(ModBlocks.DRIFTWOOD_LOG);
        add(ModBlocks.DRIFTWOOD_PLANKS);
        add(ModBlocks.DRIFTWOOD_LEAVES);
        add(ModBlocks.DRIFTWOOD_SAPLING);
        add(ModBlocks.DRIFTWOOD_FENCE);
        add(ModBlocks.DRIFTWOOD_GATE);
        add(ModBlocks.DRIFTWOOD_BUTTON);
        add(ModBlocks.DRIFTWOOD_SLAB);
        add(ModBlocks.DRIFTWOOD_STAIRS);
        add(ModBlocks.DRIFTWOOD_DOOR);
        add(ModBlocks.DRIFTWOOD_TRAPDOOR);
        add(ModBlocks.AMALGAM_BLOCK);
        add(ModBlocks.AMALGAM_DOOR);
        add(ModBlocks.AMALGAM_TRAPDOOR);
        add(ModBlocks.RUST);
        add(ModBlocks.AMALGAM_SLAB);
        add(ModBlocks.AMALGAM_STAIRS);
        add(ModBlocks.AMALGAM_ORE);

        add(ModBlocks.CLOD_BLOCK);
        add(ModBlocks.CLOD_ORE);
        add(ModBlocks.UNRAVELED_SPIKE);
        add(ModBlocks.PALE_SAND);
        add(ModBlocks.DARK_SAND_LAYER);
        add(ModBlocks.DARK_SAND);
        add(ModBlocks.LINT_LAYER);
        add(ModBlocks.STONE_SLAB);
        add(ModBlocks.STONE_STAIRS);
        add(ModBlocks.STONE_WALL);

        ModBlocks.DecayGroupSet.SETS.forEach(this::addBlockSet);

        add(ModBlocks.GRITTY_STONE);
        add(ModBlocks.LEAK);

        add(ModItems.RIFT_KEY, "Rift Key", () -> {
            //TODO: TO be populated later
        });

        addDoorAutoGen(Blocks.IRON_DOOR, "Public Door", () -> {
            info(0, "Place on the block under a rift");
            info(1, "to activate that rift or place");
            info(2, "anywhere else to create a");
            info(3, "pocket dimension.");
        });

        addDoorAutoGen(ModBlocks.STONE_DOOR, "Dungeon Door", () -> {
            info(0, "Place on the block under a rift");
            info(1, "to activate that rift or place");
            info(2, "anywhere else to create a");
            info(3, "dungeon.");
        });

        add(ModItems.RIFT_REMOVER, "Rift Remover", () -> {
            add("closing", "The rift will close soon");
            add("already_closing", "This rift is already closing");
            info(0, "Use near exposed rift");
            info(1, "to remove it and");
            info(2, "any nearby rifts.");
        });

        addDoorAutoGen(ModBlocks.GOLD_DOOR, "Dimensional Gold Door", () -> {
            info(0, "Similar to a Dimensional Door");
            info(1, "but shinier");
        });

        addArmor(ModItems.WORLD_THREAD_ARMOR, "Woven World Thread");

//        add("item.dimdoors.unstable_dimensional_door", "Unstable Dimensional Door");

        add(ModItems.RIFT_SIGNATURE, () -> {
            add("stored", "Location stored");
            add("created", "Rift created");

            scope("bound", () -> {
                info(1, "Leads to (%d, %d, %d)");
                info(0, "at dimension %d");
            });

            scope("unbound", () -> {
                info(0, "First click stores a location;.");
                info(1, "second click creates a pair of");
                info(2, "rifts linking the two locations.");
            });
        });

        add(ModItems.STABILIZED_RIFT_SIGNATURE, () -> {
            add("stored", "Location stored");
            add("created", "Rift created");

            scope("bound", () -> {
                info(0, "Leads to (%d, %d, %d)");
                info(1, "at dimension %d");
            });

            scope("unbound", () -> {
                info(0, "First click stores a location,.");
                info(1, "other clicks create rifts linking");
                info(2, "the first and last locations together.");
            });
        });

        add(ModItems.RIFT_CONFIGURATION_TOOL, () -> {
            //TODO: Figure out better working later.

            info(0, "Shift right click on");
            info(1, "a door to set");
            info(2, "to an id for");
            info(3, "pocket config in");
            info(4, "a datapack.");
        });


        add(ModItems.RIFT_STABILIZER, () -> {
            info("Use on a rift's core to stop its growth.");
            add("stabilized", "The rift has been stabilized and will stop growing");
            add("already_stabilized", "This rift is already stable");
        });

        add(ModItems.RIFT_BLADE, () -> {
            add("rift_miss", "You can only use this item on a rift's core");
            info(0, "Opens temporary doors on rifts");
            info(1, "and has a teleport attack.");
        });

        add(ModItems.WORLD_THREAD, "World Thread");
        add(ModItems.INFRANGIBLE_FIBER, "Infrangible Fiber");
        add(ModItems.FRAYED_FILAMENT, "Frayed Filament");
        add(ModItems.STABLE_FABRIC, "Stable Fabric");


        addDoorAutoGen(Blocks.OAK_DOOR, "Escape Door", () -> {
            info(0, "Place on the block under a rift");
            info(1, "to create a portal, or place anywhere");
            info(2, "in a pocket dimension to exit.");
        });

        addDoorAutoGen(ModBlocks.QUARTZ_DOOR, "Personal Door", () -> {
            info("Creates a pathway to your personal pocket.");
        });

        addDoorAutoGen(ModBlocks.AMALGAM_DOOR, "Myth Door", () -> {
            info(0, "Create a pathway to");
            info(1, "a dungeon so mythical");
            info(2, "that a normal door");
            info(3, "can not maintain.");
        });

        addDoorAutoGen(Blocks.CRIMSON_DOOR, "Myth Door", () -> {
            info(0, "Create a gateway to");
            info(1, "to an infernal dungeon.");
        });

//        builder.add("item.dimdoors.unstable_dimensional_door.info", "Caution, Leads to random destination"); //TODO: readd the unstable door

        addDisc(ModItems.CREEPY_RECORD, "Stevenrs11 - Creepy");

        add(ModItems.ETERNAL_FLUID_BUCKET);
        add(ModItems.LEAK_BUCKET);

        addDisc(ModItems.WHITE_VOID_RECORD, "Lachney - White Void");
        add(ModItems.DIMENSIONAL_ERASER, () -> {
            info("Erases entities");
        });

        add(ModItems.MONOLITH_SPAWNER, "Monolith Spawner");
        add(ModItems.MASK_WAND, "Mask Wand");
        add(ModItems.MASK_SHARD, "Mask Shard");
        add(ModItems.FUZZY_FIREBALL, "Fuzzy Fireball");
        add(ModItems.FABRIC_OF_FINALITY, "Fabric of Finality");
        add(ModItems.LIMINAL_LINT, "Liminal Lint");
        add(ModItems.ENDURING_FIBERS, "Enduring Fibers");
        add(ModItems.RIFT_PEARL, "Rift Pearl");
//        add(ModItems.FABRIC_OF_REALITY, "Fabric of Reality");
        add(ModItems.AMALGAM_LUMP, "Amalgam Lump");
        add(ModItems.CLOD, "Clod");

        addArmor(ModItems.GARMENT_OF_REALITY_ARMOR, "Garment of Reality");

        addDisc(ModItems.THEY_STARE_BACK_RECORD, "Firel - They Stare Back");

        add(ModFluids.ETERNAL_FLUID);
        add(ModFluids.LEAK);

        add(ModEntityTypes.MONOLITH);
        add(ModEntityTypes.MASK);

        scope("commands", () -> {
            builder.add("commands.dimteleport.usage", "/dimteleport <dimension> <x> <y> <z> [yaw] [pitch]");

            scope("fabricconvert", () -> {
                builder.add("commands.fabricconvert.usage", "/fabricconvert");
                builder.add("commands.fabricconvert.success", "All fabric of reality has been converted to black.");
            });

            scope("pocket", () -> {
                builder.add("commands.pocket.usage", "/pocket <group> <name> [setup]");
                builder.add("commands.pocket.group_not_found", "Group %s not found");
            });

            scope("dimdoors", () -> {
                builder.add("pocket.template_not_found", "Template %s not found");
                builder.add("saveschem.usage", "/saveschem <name>");
                builder.add("saveschem.success", "Pocket %s has been successfully saved");
            });


            add("generic.dimdoors.not_in_pocket_dim", "You must be in a pocket dimension to use this command.");
            add("generic.dimdoors.not_in_pocket", "You must be in a pocket to use this command.");
            add("generic.unknownValue", "Unknown value '%s'");
            add("pocket.unknownPocketTemplate", "Unknown Pocket Template '%s'");
            add("pocket.placedSchem", "Placed schematic %s at %s in world %s");
            add("pocket.loadedSchem", "Loaded schematic %s to clipboard. Paste it using //paste");
            add("pocket.log.creation.off", "Toggled logging of pocket creation off.");
            add("pocket.log.creation.on", "Toggled logging of pocket creation on.");
            add("pocket.log.creation.generating", "Generating pocket from template '%s' at location %s %s %s");
        });

        scope("rifts", () -> {
            add("unlinked1", "This rift doesn't lead anywhere");
            add("unlinked2", "This rift has closed");
            add("isLocked", "This rift is locked");
            add("cantUnlock", "Can't unlock this door");
            add("unlocked", "Unlocked");
            add("locked", "Locked");

            scope("destinations", () -> {
                add("escape.cannot_escape_limbo", "Nice try, but you'll need to either die or find some eternal fabric to get out of Limbo.");
                add("escape.not_in_pocket_dim", "You can only use this to escape from a pocket dimension!");
                add("escape.did_not_use_rift", "You didn't use a rift to enter the pocket dimension, so you ended up in Limbo!");
                add("escape.rift_has_closed", "The rift you used to enter the pocket dimension has closed and you ended up in Limbo!");
                add("private_pocket_exit.did_not_use_rift", "You didn't use a rift to enter the pocket dimension and you ended up in Limbo!");
                add("private_pocket_exit.rift_has_closed", "The rift you used to enter the pocket dimension has closed and you ended up in Limbo!");
            });

            add("entrances.rift_too_close", "Placing a door this close to a tear in the world would be dangerous. Shift-right-click to place anyway, or place it on the rift's core (tesseract) to bind it to the rift.");
            add("entrances.cannot_be_placed_on_rift", "This type of door can't be placed on a rift.");
        });


        scope("tools", () -> {
            add("rift_miss", "You can only use this item on a rift's core");
            add("signature_blocked", "Usage of the signature was block");
            add("target_became_block", "Failed, there is now a block at the stored location");
        });

        add(VirtualTarget.VirtualTargetType.AVAILABLE_LINK, "Random");

        virtualType(VirtualTarget.VirtualTargetType.ESCAPE, "Escape");
        virtualType(VirtualTarget.VirtualTargetType.RIFT_REFERENCE, "Rift Reference");
        virtualType(VirtualTarget.VirtualTargetType.GLOBAL, "Global");
        virtualType(VirtualTarget.VirtualTargetType.LIMBO, "Limbo");
        virtualType(VirtualTarget.VirtualTargetType.LOCAL, "Local");
        virtualType(VirtualTarget.VirtualTargetType.PUBLIC_POCKET, "Public Pocket");
        virtualType(VirtualTarget.VirtualTargetType.POCKET_ENTRANCE, "Pocket Entrance");
        virtualType(VirtualTarget.VirtualTargetType.POCKET_EXIT, "Pocket Exit");
        virtualType(VirtualTarget.VirtualTargetType.PRIVATE, "Private Pocket Entrance");
        virtualType(VirtualTarget.VirtualTargetType.PRIVATE_POCKET_EXIT, "Private Pocket Exit");
        virtualType(VirtualTarget.VirtualTargetType.RELATIVE, "Relative");
        virtualType(VirtualTarget.VirtualTargetType.ID_MARKER, "Id Marker");
        virtualType(VirtualTarget.VirtualTargetType.UNSTABLE, "Unstable");
        virtualType(VirtualTarget.VirtualTargetType.NONE, "None");

        scope("category.dimdoors", () -> {
            add("tesselating", "Tesselating");
            add("decays_into", "Decays Into");
        });

        add("dimdoors.destination", "Destination type");

        scope("config", () -> {
            scope("dimdoors", () -> {
                addTitle("Dimensional Doors");
                scope("general", () -> {
                    addCategory("General Settings");
                    addOption("depthSpreadFactor", "Depth Spread Factor", "The scale of the dispersion when escaping from a pocket or limbo, in blocks/depth. Limbo is treated as depth 50.");
                    addOption("riftCloseSpeed", "Rift Close Speed", "The speed at which rifts close when using the rift remover, in units of rift size per tick.");
                    addOption("riftGrowthSpeed", "Rift Growth Speed", "The speed at which rifts grow, in units of rift size per tick.");
                    addOption("enableRiftDecay", "Rift Growth Speed", "When true, blocks around a growing rift will unravel over time.");
                    addOption("teleportOffset", "Teleport Offset", "Distance in blocks to teleport the player in front of the dimensional door.");
                    addOption("riftBoundingBoxInCreative", "Rift Bounding Box in Creative", "When true, shows the bounding boxes of floating rifts when the player is in creative.");
                    addOption("endermanSpawnChance", "Enderman spawn chance", "The chance that an enderman spawns at a detached rift.");
                    addOption("endermanAggressiveChance", "Enderman aggressive chance", "The chance that an enderman spawned by a detached rift attacks the closest player.");
                    addOption("enableDebugMessages", "Enable Debug Messages", "When true, debug messages will be printed.");
                });

                scope("doors", () -> {
                    addCategory("Doors Settings");
                    addOption("closeDoorBehind", "Close Door Behind", "When true, Dimensional Doors will automatically close when the player enters their portal.");
                    addOption("doorList", "Doors", "Set overrides for enabling/disabling certain doors");
                    addOption("doorList.mode", "Mode", "Enable - Only generate dimensional variants of these doors. Disable - Prevent generating dimensional variants of these doors");
                    addOption("doorList.doors", "Doors", "A list of block ids for doors. If the door's item id is different than the block id, add that as well.");
                    addOption("placeRiftsInCreativeMode", "Place Rifts in Creative Mode", "If enabled, breaking a door in creative mode will spawn a rift");
                });

                scope("pockets", () -> {
                    addCategory("Pocket Settings");
                    addOption("pocketGridSize", "Pocket Grid Size", "Sets how many chunks apart all pockets in any pocket dimensions should be placed.");
                    addOption("maxPocketSize", "Maximum Pocket Size", "Sets the maximum size of any pocket. A size of x will allow for pockets up to (x + 1) * (x + 1) chunks.");
                    addOption("privatePocketSize", "Private Pocket Size", "Sets the minimum size of a newly created Private Pocket. If this is set to any value bigger than maxPocketSize, the value of maxPocketSize will be used instead.");
                    addOption("publicPocketSize", "Public Pocket Size", "Sets the minimum size of a newly created Public Pocket. If this is set to any value bigger than privatePocketSize, the value of privatePocketSize will be used instead.");
                    addOption("defaultWeightEquation", "Default Weight Equation", "Sets the equation to be used to compute weight when there is no / invalid weight equation present in the pocket generator json");
                    addOption("fallbackWeight", "Fallback weight", "Sets the fallback weight to be used if the default weight equation fails.");
                    addOption("classicPocketsResourcePackActivationType", "Classic Resource Pack Activation Type", "Default - Disabled but can be enabled, Default Enabled - Enabled but can be disabled, Always Enabled - Can not be disabled");
                    addOption("defaultPocketsResourcePackActivationType", "Default Resource Pack Activation Type", "Default - Disabled but can be enabled, Default Enabled - Enabled but can be disabled, Always Enabled - Can not be disabled");
                    addOption("asyncWorldEditPocketLoading", "Async WorldEdit Pocket Loading", "Sets loading pockets to your WorldEdit clipboard asynchronous or synchronous. Only affects when WorldEdit is installed.");
                    addOption("canUseRiftSignatureInPrivatePockets", "Can Use Rift Signature In Private Pockets", "If Enabled, rift signatures can be used within private pockets.");
                    addOption("blocksColoredPerDye", "Blocks Colored Per Dye", "The amount of blocks covered by a single dye whe dyeing a private pocket.");
                });

                scope("world", () -> {
                    addCategory("Worldgen Settings");
                    addOption("clusterGenChance", "Cluster Generation Chance", "Sets the chance (out of 1) that a cluster of rifts will generate in a given chunk.");
                    addOption("gatewayGenChance", "Gateway Generation Chance", "Sets the chance (out of 1) that a dimensional gateway will generate in a given chunk.");
                    addOption("clusterDimBlacklist", "Cluster Dimension Blacklist", "Dimension Blacklist for the generation of Rift Scar clusters. Add a dimension ID here to prevent generation in certain dimensions.");
                    addOption("gatewayDimBlacklist", "Gateway Dimension Blacklist", "Dimension Blacklist for the generation of Dimensional Portal gateways. Add a dimension ID here to prevent generation in certain dimensions.");
                });

                scope("dungeons", () -> {
                    addCategory("Dungeon Settings");
                    addOption("maxDungeonDepth", "Maximum Dungeon Depth", "The depth at which limbo is located. If a Rift reaches any deeper than this while searching for a new destination, the player trying to enter the Rift will be sent straight to Limbo.");
                });

                scope("monoliths", () -> {
                    addCategory("Monolith Settings");
                    addOption("dangerousLimboMonoliths", "Dangerous Limbo Monoliths", "When true, Monoliths in Limbo attack the player and deal damage.");
                    addOption("monolithTeleportation", "Monolith Teleportation", "When true, being exposed to the gaze of Monoliths for too long, will cause the player to be teleported to the void above Limbo.");
                });

                scope("limbo", () -> {
                    addCategory("Limbo Settings");
                    addOption("universalLimbo", "Universal Limbo", "When true, players are also teleported to Limbo when they die in any non-Pocket Dimension (except Limbo itself). Otherwise, players only go to Limbo if they die in a Pocket Dimension.");
                    addOption("hardcoreLimbo", "Hardcore Limbo", "When true, a player dying in Limbo will respawn in Limbo, making Eternal Fluid or Golden Dimensional Doors the only way to escape Limbo.");
                    addOption("limboBlocksCorruptingExitWorldAmount", "Exit World Decay Radius", "The radius around a player in which blocks can decay upon exiting limbo.");
                    addOption("worldsLeadingToLimbo", "Worlds Leading to Limbo", "Defines a blacklist/whitelist of worlds that will send the player to limbo upon death.");
                    addOption("worldsLeadingToLimbo.list", "List of world ids", "List of the ids for worlds in the blacklist/whitelist.");
                    addOption("worldsLeadingToLimbo.blacklist", "Is it a blacklist?", "Boolean that determines if list is a blacklist or white list for worlds.");
                    addOption("limboReturnDistance", "Limbo Return Radius", "Distance from spawn that limbo returns you");
                    addOption("escapeTargetWorld", "Escape To World", "Defines the id of the world players will spawn in upon exiting Limbo.  Leaving this blank will spawn players in the world their respawn point is in.");
                    addOption("escapeTargetWorldYSpawn", "Escape To World Y Level", "Defines the Y coordinate the player will spawn at when using \"Escape To World\"");
                    addOption("escapeToWorldSpawn", "Escape to World Spawn", "Boolean that determines if players exiting limbo will return relative to the worldspawn instead.  If true, escapeTargetWorld has no effect.");
                    addOption("limboReturnDistanceMax", "Max Limbo Return Distance", "Defines the maximum distance out the possible return locations can be from the target center.\n Setting both return distances to 0 cause the player to exactly appear at target center.");
                    addOption("limboReturnDistanceMin", "Min Limbo Return Distance", "Defines the minimum distance out the possible return locations can be from the target center.\n Setting both return distances to 0 cause the player to exactly appear at target center.");
                    addOption("decaySurroundings", "Decay Surroundings", "Does escaping limbo cause limbo decay around the location?");
                    addOption("tryPlayerBedSpawn", "Try Player Bed Spawn", "When true, the bed spawn of the player will be used as the center of possible return locations if available.");
                    addOption("defaultToWorldSpawn", "Default To World Spawn", "When true, the world spawn of the world the player is escaping from limbo to will be used as the center of possible return location.");
                });

                scope("graphics", () -> {
                    addCategory("Graphics Settings");
                    addOption("highlightRiftCoreFor", "Time to Highlight Rift Core", "How long, in milliseconds, the rift's core (tesseract animation) should be shown for when attempting to place a door near a large rift but not directly on it. Set to -1 to disable.");
                    addOption("showRiftCore", "Always Show Rift Cores", "Set this to true to always show rifts' cores (tesseract animation).");
                    addOption("riftSize", "Rift Size", "Multiplier affecting how large rifts should be rendered, 1 being the default size.");
                    addOption("riftJitter", "Rift Jitter", "Multiplier affecting how much rifts should jitter, 1 being the default size.");
                });

                scope("decay", () -> {
                    addCategory("Decay Settings");
                    addOption("decaySpreadChance", "Decay Spread Chance", "To be filled out.");
                    addOption("decayDelay", "Decay Delay", "In minecraft ticks (20 per second on a healthy server or game), the delay between when a queued decay is scheduled and it fired.");
                    addOption("decaysIntoAir", "dimdoors.config.option.decay.decaysIntoAir.tooltip", "To be filled out.");
                });
            });
        });

        scope("advancement", () -> {
            scope("dimdoors", () -> {
                addDesc("root", "Dimensional Doors", "Venture into the depths");
                addDesc("dark_ostiology", "Dark Ostiology", "Place an Oak Dimensional Door");
                addDesc("darklight", "Darklight", "Obtain Fabric of Reality");
                addDesc("door_to_adventure", "Door to Adventure", "Enter a dungeon");
                addDesc("enter_limbo", "Limbo", "Enter Limbo");
                addDesc("hole_in_the_sky", "Hole in the Sky", "Encounter a Rift");
                addDesc("home_away_from_home", "Home away from Home", "Enter your private pocket");
                addDesc("lost_and_found", "Lost and Found", "Open a chest in a Dungeon");
                addDesc("out_of_time", "Out of Time", "Set your spawn point in a pocket dimension");
                addDesc("public_pocket", "Public Pocket", "Enter a Public Pocket");
                addDesc("string_theory", "String Theory", "Collect World Thread");
                addDesc("world_unfurled", "World Unfurled", "Collect Unravelled Fabric");
                addDesc("unravelled_but_immutable", "Unravelled But Immutable", "Obtain Infrangible Fiber");
                addDesc("fuzzy_unreality", "Fuzzy Unreality", "Obtain Frayed Filament");
            });

            scope("mode", () -> {
                add("enable", "Enable");
                add("disable", "Disable");
            });

            scope("pocket", () -> {
                add("dyeAlreadyAbsorbed", "The pocket is already that color, so the rift didn't absorb the dye.");
                add("pocketHasBeenDyed", "The pocket has been dyed %s.");
                add("remainingNeededDyes", "The pocket has %s/%s of the dyes needed to be colored %s.");
            });
        });


        add("argument.dimdoors.schematic.invalidNamespace", "Invalid schematic namespace. Expected one of %s, found %s.");
        add("command.dimdoors.schematicv2.unknownSchematic", "Unknown schematic \"%s\" in namespace \"%s\" ");

        biome(ModBiomes.PUBLIC_BLACK_VOID_KEY, "Black void (Public Pockets)");
        biome(ModBiomes.DUNGEON_DANGEROUS_BLACK_VOID_KEY, "Dangerous Black void (Dungeon Pockets)");
        biome(ModBiomes.LIMBO_KEY, "Limbo");
        biome(ModBiomes.PERSONAL_WHITE_VOID_KEY, "White void (Private Pockets)");

        scope("limbo", () -> {
            scope("death", () -> {
                scope("fell", () -> {
                    scope("accident", () -> {
                        add("ladder", "%1$s fell off a ladder and fell into limbo");
                        add("vines", "%1$s fell off some vines and fell into limbo");
                        add("weeping_vines", "%1$s fell off some weeping vines and fell into limbo");
                        add("twisting_vines", "%1$s fell off some twisting vines and fell into limbo");
                        add("scaffolding", "%1$s fell off scaffolding and fell into limbo");
                        add("other_climbable", "%1$s fell while climbing and fell into limbo");
                        add("generic", "%1$s fell from a high place and fell into limbo");
                    });

                    add("killer", "%1$s was doomed to fall and fell into limbo");
                    add("assist", "%1$s was doomed to fall by %2$s and fell into limbo");
                    add("assist.item", "%1$s was doomed to fall by %2$s using %3$s and fell into limb");
                    add("finish", "%1$s fell too far and was sent to limbo by %2$s");
                    add("finish.item", "%1$s fell too far and was finished by %2$s using %3$s and fell into limbo");
                });

                scope("attack", () -> {
                    add("lightningBolt", "%1$s was struck by lightning and was sent to limbo");
                    add("lightningBolt.player", "%1$s was struck by lightning whilst fighting %2$s and was sent to limbo");
                    add("inFire", "%1$s went to Limbo in flames");
                    add("inFire.player", "%1$s walked into fire whilst fighting %2$s and was sent to Limbo");
                    add("onFire", "%1$s burned to Limbo");
                    add("onFire.player", "%1$s was burnt to a crisp whilst fighting %2$s and was sent tp Limbo");
                    add("lava", "%1$s tried to swim in lava and sank into Limbo and sank into Limbo");
                    add("lava.player", "%1$s tried to swim in lava to escape %2$s and sank into Limbo");
                    add("hotFloor", "%1$s discovered the floor was lava and sank into Limbo");
                    add("hotFloor.player", "%1$s walked into danger zone due to %2$s and sank into Limbo");
                    add("inWall", "%1$s suffocated into Limbo");
                    add("inWall.player", "%1$s suffocated into Limbo whilst fighting %2$s");
                    add("cramming", "%1$s was squished too much ans sent to Limbo");
                    add("cramming.player", "%1$s was squashed by %2$s");
                    add("drown", "%1$s drowned and sank into Limbo");
                    add("drown.player", "%1$s drowned whilst trying to escape %2$s and sank into Limbo");
                    add("starve", "%1$s starved to death and shriveled into Limbo");
                    add("starve.player", "%1$s starved to death whilst fighting %2$s and shriveled into Limbo");
                    add("cactus", "%1$s pricked a hole in reality");
                    add("cactus.player", "%1$s walked into a cactus whilst trying to escape %2$s and was sent to Limbo");
                    add("generic", "%1$s was sent to Limbo");
                    add("generic.player", "%1$s was sent to Limbo because of %2$s");
                    add("explosion", "%1$s was blown to Limbo");
                    add("explosion.player", "%1$s was blown to Limbo by %2$s");
                    add("explosion.player.item", "%1$s was blown to Limbo by %2$s using %3$s");
                    add("magic", "%1$s was cast into Limbo by magic");
                    add("magic.player", "%1$s was cast into Limbo by magic whilst trying to escape %2$s");
                    add("even_more_magic", "%1$s was cast into Limbo by even more magic");
                    add("message_too_long", "Actually, message was too long to deliver fully. Sorry! Here's stripped version, %s");
                    add("wither", "%1$s withered into Limbo");
                    add("wither.player", "%1$s withered into Limbo whilst fighting %2$s");
                    add("witherSkull", "%1$s was shot by a skull into Limbo from %2$s");
                    add("anvil", "%1$s was squashed into Limbo by a falling anvil");
                    add("anvil.player", "%1$s was squashed into Limbo by a falling anvil whilst fighting %2$s");
                    add("fallingBlock", "%1$s was squashed into Limbo by a falling block");
                    add("fallingBlock.player", "%1$s was squashed into Limbo by a falling block whilst fighting %2$s");
                    add("stalagmite", "%1$s was impaled into Limbo on a stalagmite");
                    add("stalagmite.player", "%1$s was impaled into Limbo on a stalagmite whilst fighting %2$s");
                    add("fallingStalactite", "%1$s was skewered into Limbo by a falling stalactite");
                    add("fallingStalactite.player", "%1$s was skewered into Limbo by a falling stalactite whilst fighting %2$s");
                    add("mob", "%1$s was slain by %2$s and was sent to Limbo");
                    add("mob.item", "%1$s was slain by %2$s using %3$s and was sent to Limbo");
                    add("player", "%1$s was slain by %2$s and was sent to Limbo");
                    add("player.item", "%1$s was slain by %2$s using %3$s and was sent to Limbo");
                    add("arrow", "%1$s was shot by %2$s and was sent to Limbo");
                    add("arrow.item", "%1$s was shot by %2$s using %3$s and was sent to Limbo");
                    add("fireball", "%1$s was fireballed into Limbo by %2$s");
                    add("fireball.item", "%1$s was fireballed into Limbo by %2$s using %3$s");
                    add("thrown", "%1$s was pummeled into Limbo by %2$s");
                    add("thrown.item", "%1$s was pummeled into Limbo by %2$s using %3$s");
                    add("indirectMagic", "%1$s was killed by %2$s using magic and was sent to Limbo");
                    add("indirectMagic.item", "%1$s was sent by %2$s using %3$s and was sent to Limbo");
                    add("thorns", "%1$s was sent to Limbo trying to hurt %2$s");
                    add("thorns.item", "%1$s was sent to Limbo by %3$s trying to hurt %2$s");
                    add("trident", "%1$s was impaled by %2$s into Limbo");
                    add("trident.item", "%1$s was impaled by %2$s with %3$s into Limbo");
                    add("fall", "%1$s hit the ground too hard and dropped into Limbo");
                    add("fall.player", "%1$s hit the ground too hard whilst trying to escape %2$s and dropped into Limbo");
                    add("outOfWorld", "%1$s fell into Limbo");
                    add("outOfWorld.player", "%1$s didn't want to live in the same world as %2$s and went to Limbo");
                    add("dragonBreath", "%1$s was roasted in dragon breath and was sent to Limbo");
                    add("dragonBreath.player", "%1$s was roasted in dragon breath by %2$s and was sent to Limbo");
                    add("flyIntoWall", "%1$s experienced kinetic energy and flew into Limbo");
                    add("flyIntoWall.player", "%1$s experienced kinetic energy whilst trying to escape %2$s and flew into Limbo");
                    add("fireworks", "%1$s went into Limbo with a bang");
                    add("fireworks.player", "%1$s went into Limbo with a bang whilst fighting %2$s");
                    add("fireworks.item", "%1$s went into Limbo with a bang due to a firework fired from %3$s by %2$s");
                    add("badRespawnPoint.message", "%1$s was killed by %2$s and was sent by Limbo");
                    add("badRespawnPoint.link", "Intentional Game Design");
                    add("sweetBerryBush", "%1$s poked a hole in reality");
                    add("sweetBerryBush.player", "%1$s poked a hole in reality whilst trying to escape %2$s");
                    add("sting", "%1$s bugged out to Limbo");
                    add("sting.player", "%1$s bugged out to Limbo by %2$s");
                    add("freeze", "%1$s froze into Limbo");
                    add("freeze.player", "%1$s was frozen into Limbo by %2$s");
                });
            });

            scope("exit", () -> {
                add("eternal_fluid", "%1$s bathed in reality");
                add("generic", "%1$s escaped Limbo");
                add("rift", "%1$s found a rift leading out of Limbo");
            });
        });

        addStats(ModStats.DEATHS_IN_POCKETS, "Deaths in Pocket");
        addStats(ModStats.TIMES_BEEN_TO_DUNGEON, "Times been to Dungeon");
        addStats(ModStats.TIMES_SENT_TO_LIMBO, "Times sent to Limbo");
        addStats(ModStats.TIMES_TELEPORTED_BY_MONOLITH, "Times teleported by Monolith");

        scope("resourcePackActivationType", () -> {
            add("normal", "Normal");
            add("defaultEnabled", "Default Enabled");
            add("alwaysEnabled", "Always Enabled");
        });

        addEnchantment(ModEnchants.STRING_THEORY_ENCHANTMENT, "String Theory");
        addEnchantment(ModEnchants.RENDING_ENCHANTMENT, "Rending");
        addEnchantment(ModEnchants.TRANSCENDENT_ENCHANTMENT, "Transcendent");
        addEnchantment(ModEnchants.TREPIDATION_ENCHANTMENT, "Trepidation");

        addPainting(ModPaintings.LIMBO, "Limbo", "Waterpicker");
        addPainting(ModPaintings.PORTAL, "Portal", "timetravellingBlockhead");
        addPainting(ModPaintings.EYES, "Eyes", "Anims");
        addPainting(ModPaintings.FREEDOM, "Freedom", "ImprovInAFedora");
        addPainting(ModPaintings.GATEWAY_AT_NIGHT, "Gateway At Night", "timetravellingBlockhead");

        add(ModFluids.ETERNAL_FLUID, "Eternal Fluid");
        add(ModFluids.FLOWING_ETERNAL_FLUID, "Flowing Eternal Fluid");
        add(ModFluids.LEAK, "Leak");
        add(ModFluids.FLOWING_LEAK, "Flowing Leak");
    }

    private void biome(ResourceKey<Biome> biome, String name) {
        var key = Util.makeDescriptionId("biome", biome.location());
        add(key, name);
    }

    private void virtualType(VirtualTarget.VirtualTargetType<?> type, String name) {
        var key = Util.makeDescriptionId("virtual_type", ModRegistries.VIRTUAL_TYPE.getKey(type));

        add(key, name);
    }

    private void info(String value) {
        add("info", value);
    }

    private void info(int i, String value) {
        add("info" + i, value);
    }

    private void addDoorAutoGen(Block block, String name, Runnable runnable) {
        var key = Util.makeDescriptionId("autogen", BuiltInRegistries.BLOCK.getKey(block));
        scope(key, () -> {
            add("name", name);
            runnable.run();
        });
    }

    private void addStats(ResourceLocation stat, String name) {
        builder.add(Util.makeDescriptionId("stat", stat), name);
    }

    private void addDesc(String key, String name, String desc) {
        add(key, name);
        add(key + ".desc", desc);
    }

    private void addCategory(String value) {
        add("category", value);
    }

    private void addTitle(String value) {
        add("title", value);
    }

    private void addOption(String key, String value, String tooltip) {
        add("option" + "." + key, value);
        add("option" + "." + key + ".tooltip", tooltip);
    }

    private void addArmor(ArmorSet set, String prefix) {
        add(set.helmet(), prefix + " Helmet");
        add(set.chestplate(), prefix + " Chestplate");
        add(set.leggings(), prefix + " Leggings");
        add(set.boots(), prefix + " Boots");
    }

    private void add(Item item, String entry) {
        builder.add(item, entry);
    }


    private void addDisc(Item item, String entry) {
        var key = item.getDescriptionId();

        builder.add(key, "Music Disc");
        builder.add(key + ".desc", entry);
    }

    private void addPainting(ResourceKey<PaintingVariant> key, String name, String author) {
        var baseLang = key.location().toLanguageKey("painting");
        builder.add(baseLang + ".title", name);
        builder.add(baseLang + ".author", author);
    }

    private void add(Fluid supplier, String contents) {
        builder.add(BuiltInRegistries.FLUID.getKey(supplier).toLanguageKey("fluid"), contents);
    }

    private void addBlockSet(ModBlocks.DecayGroupSet set) {
        add(set.fence());
        add(set.gate());
        add(set.button());
        add(set.slab());
        add(set.stairs());
        add(set.wall());
    }

    private <T> void addCapitalizedEntry(Registry<T> registry, T entry) {
        var location = registry.getKey(entry);
        var value = capitialize(location.getPath());
        builder.add(translationKey(registry, entry), value);
    }

    private void add(Item item, String name, Runnable runnable) {
        var key = item.getDescriptionId();
        builder.add(key, name);

        scope(key, runnable);
    }

    private <T> void add(Registry<T> registry, T entry, String value) {
        builder.add(translationKey(registry, entry), value);
    }

    private <T> String translationKey(Registry<T> registry, T entry) {
        var location = registry.getKey(entry);
        var registryKey = registry.key().location();
        return location.toLanguageKey((registryKey.getPath().equals("minecraft") ? "" : registryKey.getPath() + ".") + registryKey.getPath());
    }

    private void add(Object object) {
        add(object, () -> {});
    }



    private void add(Object object, Runnable runnable) {
        switch (object) {
            case String entry -> {
                var key = currentKeyPath.peek();

                add(key, entry);
            }

            case Block entry -> {
                var value = capitialize(BuiltInRegistries.BLOCK.getKey(entry).getPath());
                builder.add(entry, value);
                scope(entry.getDescriptionId(), runnable);
            }

            case Item entry -> {
                var value = capitialize(BuiltInRegistries.ITEM.getKey(entry).getPath());
                builder.add(entry, value);
                scope(entry.getDescriptionId(), runnable);
            }

            case CreativeModeTab tab -> {
                var string = capitialize(BuiltInRegistries.CREATIVE_MODE_TAB.getKey(tab).getPath());
                add(tab.getDisplayName(), string);
            }

            case Fluid entry -> {
                addCapitalizedEntry(BuiltInRegistries.FLUID, entry);
                scope(BuiltInRegistries.FLUID.getKey(entry).toLanguageKey("fluid"), runnable);
            }

            case null -> {

            }

            default -> {
            }
        }
    }

    private void addBiome(ResourceKey<Biome> key, String name) {
        builder.add(Util.makeDescriptionId("biome", key.location()), name);
    }

    private void addEnchantment(ResourceKey<Enchantment> key, String name) {
        builder.addEnchantment(key, name);
    }

    private void add(Object object, String value) {
        switch (object) {

            case CreativeModeTab tab -> {
                add(tab.getDisplayName(), value);
            }

            case null -> {

            }

            default -> {
            }
        }
    }

    private Map<Class<?>, Registry<?>> map = new HashMap<>();

    private void add(Component component, String string) {
        if(component.getContents() instanceof TranslatableContents translatable) {
            builder.add(translatable.getKey(), string);            
        }
    }

    public String capitialize(String name) {
        return Stream.of(name.split("_")).map(StringUtils::capitalize).collect(Collectors.joining(" "));
    }
}
